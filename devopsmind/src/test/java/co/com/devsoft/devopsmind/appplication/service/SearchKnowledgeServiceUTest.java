package co.com.devsoft.devopsmind.appplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.devsoft.devopsmind.application.service.SearchKnowledgeService;
import co.com.devsoft.devopsmind.domain.model.KnowledgeChunk;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@Tag("unitTest")
@DisplayName("🧪 Pruebas Unitarias de Consulta :: Búsqueda Semántica Vectorial RAG")
class SearchKnowledgeServiceUTest {

    @Mock
    private TechnicalManualStorage manualStorageMock;

    @InjectMocks
    private SearchKnowledgeService searchKnowledgeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("📌 Caso Feliz: Debería consultar pgvector y retornar los trozos de conocimiento descriptivos")
    void shouldReturnRelevantChunksFromVectorDb() {
        // GIVEN: Trozos simulados con suficiente densidad semántica (>15 tokens)
        List<KnowledgeChunk> expectedChunks = List.of(
                new KnowledgeChunk(
                        "Configuracion de balanceadores de carga usando round-robin en entornos distribuidos corporativos.",
                        "LB_CFG"),
                new KnowledgeChunk(
                        "Estrategias de clustering avanzado para pools de conexiones optimizados con timeouts bajos.",
                        "DB_POOL"));
        when(manualStorageMock.findRelevantChunks("balanceadores", 2)).thenReturn(expectedChunks);

        // WHEN: Ejecutamos el motor de búsqueda RAG
        List<KnowledgeChunk> result = searchKnowledgeService.search("balanceadores", 2);

        // THEN: Certificamos el retorno plano inmaculado
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("LB_CFG", result.get(0).getSectionName());
        verify(manualStorageMock, times(1)).findRelevantChunks("balanceadores", 2);
    }
}
