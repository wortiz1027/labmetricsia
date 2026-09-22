package co.com.devsoft.devopsmind.application.service;

import java.util.List;

import co.com.devsoft.devopsmind.application.ports.input.SearchKnowledgeUseCase;
import co.com.devsoft.devopsmind.application.ports.input.UseCase;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@UseCase(description = "Ejecuta consultas de proximidad de coseno (HNSW) en pgvector para recuperar fragmentos documentales contextuales")
public class SearchKnowledgeService implements SearchKnowledgeUseCase {

    private final TechnicalManualStorage manualStorage;

    public SearchKnowledgeService(TechnicalManualStorage manualStorage) {
        this.manualStorage = manualStorage;
    }

    @Override
    public List<KnowledgeChunk> search(String query, int maxResults) {
        return manualStorage.findRelevantChunks(query, maxResults);
    }
}
