package co.com.devsoft.devopsmind.application.ports.input;

import java.util.List;

import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;

public interface SearchKnowledgeUseCase {
    List<KnowledgeChunk> search(String query, int maxResults);
}
