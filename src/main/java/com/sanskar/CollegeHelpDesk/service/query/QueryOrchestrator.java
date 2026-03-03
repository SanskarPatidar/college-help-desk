package com.sanskar.CollegeHelpDesk.service.query;

import com.sanskar.CollegeHelpDesk.model.ChatMessage;
import com.sanskar.CollegeHelpDesk.model.QueryResponse;
import com.sanskar.CollegeHelpDesk.model.ResourceType;
import com.sanskar.CollegeHelpDesk.service.cache.QueryCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
public class QueryOrchestrator {
    @Autowired
    private VectorSearchService vectorSearchService;
    @Autowired
    private UserMessageBuilderService userMessageBuilderService;
    @Autowired
    private QueryRouterService queryRouterService;
    @Autowired
    private QueryCacheRepository queryCacheRepository;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    @Qualifier("finalQueryChatClient")
    private ChatClient chatClient;
    @Autowired
    private CompressionQueryTransformer compressionTransformer;
    @Autowired
    private TranslationQueryTransformer translationTransformer;
    @Value("classpath:/prompts/final_query_system_prompt.st")
    private Resource finalQueryPrompt;

    public QueryResponse ask(String query, String conversationId) {
        String transformedQuery = compressionTransformer.transform(
                translationTransformer.transform(new Query(query))
        ).text();
        System.out.println(transformedQuery);

        // using virtual threads for high concurrency with blocking calls
        Future<QueryResponse> cacheFuture = executor.submit(() -> {
            return queryCacheRepository.searchSimilar(transformedQuery, conversationId);
        });

        Future<QueryResponse> llmFuture = executor.submit(() -> {
            long start = System.currentTimeMillis();
            try {
                // detect tabs
                Set<ResourceType> tabs = queryRouterService.detectTabs(transformedQuery);

                // searching in all relevant tabs
                List<Document> allSegments = new ArrayList<>();
                for(ResourceType type : tabs){
                    allSegments.addAll(
                            vectorSearchService.search(query, type)
                    );
                }
                if(allSegments.isEmpty()){
                    log.warn("No context segments found from vector search for query");
                    return QueryResponse.builder()
                            .conversationId(conversationId)
                            .query(transformedQuery)
                            .answer("No relevant information found.")
                            .build();
                }

                String userMessage = userMessageBuilderService.buildUserMessage(allSegments, transformedQuery, conversationId);

                PromptTemplate promptTemplate = PromptTemplate.builder()
                        .resource(finalQueryPrompt)
                        .build();
                // calling llm for final answer generation
                ChatResponse response = chatClient
                        .prompt()
                        .system(promptTemplate.render())
                        .user(userMessage)
                        .call()
                        .chatClientResponse()
                        .chatResponse();

                return QueryResponse.builder()
                        .conversationId(conversationId)
                        .query(transformedQuery)
                        .answer(response.getResult().getOutput().getText())
                        .latency(System.currentTimeMillis() - start)
                        .totalTokens(response.getMetadata().getUsage().getTotalTokens())
                        .cached(false)
                        .build();

            }
            catch(Exception e){
                log.error(e.getMessage());
                log.info("Llm future interrupted");
                return QueryResponse.builder()
                        .conversationId(conversationId)
                        .query(transformedQuery)
                        .answer("No relevant information found.")
                        .build();
            }
        });

        try {
            QueryResponse cached = cacheFuture.get();
            if (cached != null) {
                llmFuture.cancel(true);
                chatMemory.add(conversationId, ChatMessage.queryResponseToMessage(cached));
                return cached;
            }
            QueryResponse queryResponse = llmFuture.get();
            executor.execute(() ->
                    queryCacheRepository.store(transformedQuery, queryResponse.answer())
            );
            chatMemory.add(conversationId, ChatMessage.queryResponseToMessage(queryResponse));
            return queryResponse;
        }
        catch (Exception e) {
            log.error("Error in ask()", e);
        }
        return QueryResponse.builder()
                .conversationId(conversationId)
                .query(transformedQuery)
                .answer("No relevant information found.")
                .build();
    }
}
