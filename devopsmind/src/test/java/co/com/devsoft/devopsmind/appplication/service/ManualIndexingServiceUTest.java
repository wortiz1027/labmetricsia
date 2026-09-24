package co.com.devsoft.devopsmind.appplication.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.devsoft.devopsmind.application.service.ManualIndexingService;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@Tags({
        @Tag("unitTest")
})
@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 Pruebas Unitarias de Aplicación :: Indexación RAG de Manuales")
public class ManualIndexingServiceUTest {

    @Mock
    private TechnicalManualStorage manualStorageMock;

    @InjectMocks
    private ManualIndexingService indexingService;

    @Test
    @DisplayName("📌 Caso Feliz: Debería orquestar la segmentación e indexar trozos vectoriales con éxito")
    void shouldIndexManualSuccessfully() {
        ManualId manualId = ManualId.generate();
        TechnicalManual manualTemplate = new TechnicalManual(manualId, "Manual de Arquitectura Hexagonal");

        when(manualStorageMock.findBy(any(ManualId.class))).thenReturn(Optional.of(manualTemplate));

        String rawText = "Este es un parrafo de configuracion avanzado diseñado especificamente para desplegar pods de alta disponibilidad utilizando arquitecturas de replicas en nodos distribuidos dentro de la red del laboratorio DevOpsMind con soporte vectorial para pgvector corporativo.";

        indexingService.index(manualId, rawText, "HEX_ARCH");

        verify(manualStorageMock, times(1)).saveVectorChunks(any(ManualId.class), anyList());
        verify(manualStorageMock, times(1)).saveMetadata(any(TechnicalManual.class));
    }

    @Test
    @DisplayName("📌 Caso Alternativo: Debería lanzar excepción si el manual solicitado no existe en MySQL")
    void shouldThrowExceptionWhenManualDoesNotExist() {
        ManualId manualId = ManualId.generate();
        when(manualStorageMock.findBy(any(ManualId.class))).thenReturn(Optional.empty());

        assertThrows(InvalidDomainDataException.class,
                () -> indexingService.index(manualId, "Texto largo de prueba de infraestructura...", "GENERAL"));

        verify(manualStorageMock, never()).saveVectorChunks(any(), any());
    }
}
