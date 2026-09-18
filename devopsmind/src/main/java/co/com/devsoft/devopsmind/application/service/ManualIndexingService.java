package co.com.devsoft.devopsmind.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@Service
public class ManualIndexingService implements IndexManualUseCase {

    private static final Logger log = LoggerFactory.getLogger(ManualIndexingService.class);

    private final TechnicalManualStorage storage;

    public ManualIndexingService(TechnicalManualStorage storage) {
        this.storage = storage;
    }

    @Override
    public void index(ManualId id, String rawText, String sectionName) {
        log.info("🤖 [Servicio Aplicación] Iniciando indexación RAG para Manual ID: {}", id.getValue());

        TechnicalManual manual = this.storage.findBy(id)
                                        .orElseThrow(() -> new InvalidDomainDataException(String.format(
                                                                    "No se puede indexar: El manual técnico con ID '%s' no existe en el sistema.",
                                                                    id.getValue())));
        List<KnowledgeChunk> chunks = manual.splitIntoChunks(rawText, sectionName);

        if (chunks.isEmpty()) {
            log.info(
                    "⚠️ [Servicio Aplicación] El manual no generó fragmentos válidos o informativos. Proceso abortado.");
            return;
        }

        log.info("📦 [Servicio Aplicación] Segmentación exitosa. Chunks válidos generados: {}", chunks.size());

        this.storage.saveVectorChunks(id, chunks);
        this.storage.saveMetadata(manual);

        log.info("✅ [Servicio Aplicación] ¡Manual '%s' indexado semánticamente con éxito!", manual.getTitle());
    }

}
