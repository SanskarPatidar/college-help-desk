package com.sanskar.CollegeHelpDesk.service.query;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import com.sanskar.CollegeHelpDesk.service.cache.ConversationMemoryService;
import com.sanskar.CollegeHelpDesk.service.cache.QueryCacheService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryOrchestrator {
    private final QueryEmbeddingService queryEmbeddingService;
    private final VectorSearchService vectorSearchService;
    private final PromptBuilderService promptBuilderService;
    private final LlmQueryService llmQueryService;
    // private final RerankerService rerankerService;
    private final QueryRouterService queryRouterService;
    private final ConversationMemoryService conversationMemoryService;
    private final QueryCacheService queryCacheService;
    private final ExecutorService executor;


    public String ask(String query, String sessionId) {
        // using virtual threads for high concurrency with blocking calls
        Future<String> cacheFuture = executor.submit(() -> {
            return queryCacheService.searchSimilar(query);
        });

        Future<String> llmFuture = executor.submit(() -> {
            try {
                // detect tabs
                Set<ResourceType> tabs = queryRouterService.detectTabs(query);

                // embed query
                Embedding queryEmbedding = queryEmbeddingService.embedQuery(query);

                // searching in all relevant tabs
                List<TextSegment> allSegments = new ArrayList<>();
                for(ResourceType type : tabs){
                    allSegments.addAll(
                            vectorSearchService.search(queryEmbedding, type)
                    );
                }
                if(allSegments.isEmpty()){
                    return "No relevant information found.";
                }

                // reranking segments
                // List<TextSegment> rerankedSegments = rerankerService.rerank(query, allSegments, 3);

                // history fetch
                List<String[]> history = conversationMemoryService.getHistory(sessionId);

                // build prompt
                String prompt = promptBuilderService.buildPrompt(query, allSegments, history);

                // ask LLM
                String answer = llmQueryService.answer(prompt);

                // update history
                conversationMemoryService.addMessage(sessionId, query, answer);

                return answer;
            }
            catch(Exception e){
                log.info(e.getMessage());
                log.info("LLM task interrupted");
                return null;
            }
        });

        try {
            String cached = cacheFuture.get(); // wait/join call until get cache result
            if (cached != null) {
                log.info("Returned from semantic cache");
                // llmFuture.cancel(true); // stoping LLM call if cache hit
                log.info("LLM query cancelled");
                return cached;
            }

            // If cache miss, then wait for LLM
            String answer = llmFuture.get();
            executor.execute(() -> {
                log.info("Storing in cache");
                queryCacheService.store(query, answer);
            });
            return answer;
        }
        catch (Exception e) {
            log.error("Error in ask()", e);
        }
        return "";
    }
}
