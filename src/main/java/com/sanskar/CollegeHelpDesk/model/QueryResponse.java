package com.sanskar.CollegeHelpDesk.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Builder
public record QueryResponse(
        String conversationId,
        String query,
        String answer,
        long totalTokens,
        long latency,
        boolean cached
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QueryResponse that = (QueryResponse) o;
        return latency == that.latency && cached == that.cached && totalTokens == that.totalTokens && Objects.equals(query, that.query) && Objects.equals(answer, that.answer) && Objects.equals(conversationId, that.conversationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, query, answer, totalTokens, latency, cached);
    }
}
