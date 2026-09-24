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

import co.com.devsoft.devopsmind.application.service.FindActiveManualsService;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;

@Tag("unitTest")
@DisplayName("🧪 Pruebas Unitarias de Consulta :: Manuales Activos")
class FindActiveManualsServiceUTest {

    @Mock
    private TechnicalManualStorage manualStorageMock;

    @InjectMocks
    private FindActiveManualsService activeManualsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("📌 Caso Feliz: Debería orquestar y retornar la lista de manuales activos desde el almacenamiento")
    void shouldReturnActiveManualsSuccessfully() {
        // GIVEN: Una lista de manuales simulada en el Storage
        List<TechnicalManual> expectedManuals = List.of(
                new TechnicalManual(ManualId.generate(), "Manual K8s Prod"),
                new TechnicalManual(ManualId.generate(), "Manual Postgres Tuning"));
        when(manualStorageMock.findActiveManuals()).thenReturn(expectedManuals);

        // WHEN: Ejecutamos el caso de uso de lectura
        List<TechnicalManual> result = activeManualsService.findActive();

        // THEN: Verificamos los datos y la interacción legítima con MySQL
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Manual K8s Prod", result.get(0).getTitle());
        verify(manualStorageMock, times(1)).findActiveManuals();
    }
}
