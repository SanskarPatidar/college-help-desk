package com.sanskar.CollegeHelpDesk.model;

import lombok.Builder;

@Builder
public record QueryResponse(
        String conversationId,
        String query,
        String answer,
        long totalTokens,
        long latency,
        boolean cached
) {
}
