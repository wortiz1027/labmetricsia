package co.com.devsoft.devopsmind.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;
import co.com.devsoft.devopsmind.infrastructure.BaseIntegrationITest;
import jakarta.persistence.EntityManager;

@Tags({
        @Tag("integrationTest")
})
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("🧪 Pruebas de Integración :: Motor RAG Políglota (MySQL + pgvector)")
class ManualIndexingServiceITest extends BaseIntegrationITest {

    private final IndexManualUseCase indexManualUseCase;
    private final TechnicalManualStorage technicalManualStorage;
    private final JdbcTemplate postgresJdbcTemplate;
    private final EntityManager entityManager;

    public ManualIndexingServiceITest(IndexManualUseCase indexManualUseCase,
            TechnicalManualStorage technicalManualStorage,
            @Qualifier("postgresJdbcTemplate") JdbcTemplate postgresJdbcTemplate,
            EntityManager entityManager) {
        this.indexManualUseCase = indexManualUseCase;
        this.technicalManualStorage = technicalManualStorage;
        this.postgresJdbcTemplate = postgresJdbcTemplate;
        this.entityManager = entityManager;
    }

    @Test
    @DisplayName("📌 Flujo End-to-End :: Debería segmentar el texto, indexar vectores en Postgres y actualizar metadatos en MySQL")
    void shouldExecutePolglotIndexingEndToEnd() {
        ManualId manualId = ManualId.generate();
        TechnicalManual newManual = new TechnicalManual(manualId, "Manual de SRE Avanzado Ryzen 7");
        technicalManualStorage.saveMetadata(newManual);

        Optional<TechnicalManual> dbManualBefore = technicalManualStorage.findBy(manualId);
        assertTrue(dbManualBefore.isPresent(), "El manual maestro debió guardarse en MySQL.");

        String rawText = """
                Este es un párrafo de configuración avanzado diseñado específicamente para desplegar pods de alta disponibilidad
                utilizando arquitecturas de réplicas en nodos distribuidos dentro de la red del laboratorio DevOpsMind.
                Se requiere configurar los límites de memoria RAM de forma estricta para el Ryzen 7 con el fin de evitar
                colapsos semánticos o desbordamientos de buffers al momento de indexar los embeddings en pgvector de forma atómica.
                """;
        indexManualUseCase.index(manualId, rawText, "K8S_TUNING");

        entityManager.clear();

        Optional<TechnicalManual> dbManualAfter = technicalManualStorage.findBy(manualId);

        assertTrue(dbManualAfter.isPresent(), "MySQL debió persistir de forma definitiva el manual técnico.");
        assertEquals("Manual de SRE Avanzado Ryzen 7", dbManualAfter.get().getTitle(),
                "MySQL debió retener los metadatos inmutables.");

        String query = """
                SELECT COUNT(*)
                FROM vector_store
                """;
        Integer vectorRowCount = postgresJdbcTemplate.queryForObject(
                query,
                Integer.class);

        assertNotNull(vectorRowCount);
        assertTrue(vectorRowCount > 0,
                "PostgreSQL + pgvector debió almacenar físicamente los fragmentos vectoriales de 1024 dimensiones.");
    }

}
