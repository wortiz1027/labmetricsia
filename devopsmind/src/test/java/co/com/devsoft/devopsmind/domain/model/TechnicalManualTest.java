package co.com.devsoft.devopsmind.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainStateException;

@DisplayName("🧪 Pruebas Unitarias del Dominio :: Gestión de Manuales Técnicos")
public class TechnicalManualTest {

    @Test
    @DisplayName("📌 Regla 1: Debería fallar al intentar crear un manual con título vacío")
    void shouldFailWhenTitleIsEmpty() {
        assertThrows(InvalidDomainDataException.class, () -> new TechnicalManual(ManualId.generate(), ""));
        assertThrows(InvalidDomainDataException.class, () -> new TechnicalManual(ManualId.generate(), "   "));
    }

    @Test
    @DisplayName("📌 Regla 2: Debería segmentar el texto y aceptar solo chunks con suficiente densidad de datos")
    void shouldSplitTextAndAcceptOnlyDescriptiveChunks() {
        String template = """
                --- SECTION :: %s ---
                    Content: %s
                """;
        final String SECTION = "K8S_DEPLOY";
        TechnicalManual manual = new TechnicalManual(ManualId.generate(), "Manual de Kubernetes");
        String rawText = "Este es un párrafo de configuración avanzado para desplegar pods de alta disponibilidad usando arquitecturas de réplicas en nodos distribuidos.\n\nCorto.";

        List<KnowledgeChunk> chunks = manual.splitIntoChunks(rawText, "K8S_DEPLOY");

        assertEquals(1, chunks.size());
        assertEquals("K8S_DEPLOY", chunks.get(0).getSectionName());
        assertTrue(chunks.get(0).getTokenCountEstimate() >= 15);
        assertTrue(chunks.get(0).formatForContext().contains(String.format(template, SECTION, rawText)));
    }

    @Test
    @DisplayName("📌 Regla 1 (Continuación): Debería lanzar excepción si se intenta segmentar un manual depreciado")
    void shouldFailWhenSplittingOnDeprecatedManual() {
        TechnicalManual manual = new TechnicalManual(ManualId.generate(), "Manual Obsoleto de Docker v1");
        manual.deprecate();

        assertTrue(manual.isDeprecated());
        assertTrue(manual.getChunks().isEmpty());

        assertThrows(InvalidDomainStateException.class, () -> manual
                .splitIntoChunks("Texto de prueba largo para intentar forzar la segmentación semántica.", "DOCKER"));
    }
}
