package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto;

public record KnowledgeChunkDTO(String content,
                                String sectionName,
                                int tokenCountEstimate,
                                String formatForContext
) {

}
