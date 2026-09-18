package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record KnowledgeChunkResponse(
        String content,
        String sectionName,
        int tokenCountEstimate,
        String formatForContext) {
}
