package com.sanskar.CollegeHelpDesk.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class MetadataLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        System.out.println("ADVISOR TRIGGERED");
        log.info("Request: {}", chatClientRequest.prompt().getContents());
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
        log.info(
                "ChatClientResponse metadata: PromptTokens={}, CompletionTokens={}, TotalTokens={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens()
        );
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        log.info("Request: {}", chatClientRequest.prompt().getContents());

        return streamAdvisorChain.nextStream(chatClientRequest)
                .doOnNext(chatClientResponse -> {
                    Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
                    log.info(
                            "ChatClientResponse metadata: PromptTokens={}, CompletionTokens={}, TotalTokens={}",
                            usage.getPromptTokens(),
                            usage.getCompletionTokens(),
                            usage.getTotalTokens()
                    );
                });
    }

    @Override
    public String getName() {
        return "MetadataLoggerAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // executed last in the chain (last for request processing, first for response processing)
        // Insane bug: I put LOWEST_PRECEDENCE here and it started to stop executing
    }
}
