package co.com.devsoft.devopsmind.domain.repository;

import java.util.List;
import java.util.Optional;

import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;

public interface TechnicalManualStorage {
    TechnicalManual saveMetadata(TechnicalManual manual);
    Optional<TechnicalManual> findBy(ManualId id);
    List<TechnicalManual> findActiveManuals();

    void saveVectorChunks(ManualId id, List<KnowledgeChunk> chunks);
    List<KnowledgeChunk> findRelevantChunks(String query, int maxResults);
}
