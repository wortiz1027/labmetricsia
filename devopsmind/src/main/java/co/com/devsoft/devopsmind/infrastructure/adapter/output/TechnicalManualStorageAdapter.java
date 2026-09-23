package co.com.devsoft.devopsmind.infrastructure.adapter.output;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.TechnicalManualEntity;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.mappers.TechnicalManualMapper;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.repositories.TechnicalManualMySQLRepository;

@Component
public class TechnicalManualStorageAdapter implements TechnicalManualStorage {

    private final TechnicalManualMySQLRepository repository;
    private final VectorStore store;

    public TechnicalManualStorageAdapter(TechnicalManualMySQLRepository repository, VectorStore store) {
        this.repository = repository;
        this.store = store;
    }

    @Override
    @Transactional(value = "transactionManager", propagation =  Propagation.NOT_SUPPORTED)
    public TechnicalManual saveMetadata(TechnicalManual manual) {
        TechnicalManualEntity entity = TechnicalManualMapper.toEntity(manual);

        this.repository.save(entity);
        return manual;
    }

    @Override
    public Optional<TechnicalManual> findBy(ManualId id) {
        return this.repository.findById(id.getValue()).map(TechnicalManualMapper::toDomain);
    }

    @Override
    public List<TechnicalManual> findActiveManuals() {
        return this.repository.findByIsDeprecatedFalse()
                .stream()
                .map(TechnicalManualMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveVectorChunks(ManualId id, List<KnowledgeChunk> chunks) {
        List<Document> documents = chunks.stream()
                .map(chunk -> new Document(chunk.getContent(),
                        Map.of("manual_id", id.getValue(),
                                "section_name", chunk.getSectionName(),
                                "token_estimate", chunk.getTokenCountEstimate())))
                .collect(Collectors.toList());

        this.store.add(documents);
    }

    @Override
    public List<KnowledgeChunk> findRelevantChunks(String query, int maxResults) {
        SearchRequest request = SearchRequest.builder().query(query)
                .topK(maxResults)
                .similarityThreshold(0.7)
                .build();

        List<Document> documents = this.store.similaritySearch(request);
        return documents.stream()
                .map(doc -> new KnowledgeChunk(doc.getText(),
                                               (String) doc.getMetadata().getOrDefault("section_name", "GENERAL")
                                              )
                    )
                .collect(Collectors.toList());
    }

}
