package co.com.devsoft.devopsmind.appplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import co.com.devsoft.devopsmind.application.service.FindIncidentByIdService;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;

@Tag("unitTest")
@DisplayName("🧪 Pruebas Unitarias de Consulta :: Incidentes por ID")
class FindIncidentByIdServiceUTest {

    @Mock
    private LabIncidentStorage incidentStorageMock;

    @InjectMocks
    private FindIncidentByIdService incidentByIdService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("📌 Caso Feliz: Debería retornar el incidente envuelto en Optional si existe en MySQL")
    void shouldReturnIncidentWhenIdExists() {
        // GIVEN: Un incidente registrado en el mock de la bitácora
        String id = "INC-8547";
        LabIncident expectedIncident = new LabIncident("Fallo de red en pool Hikari");
        when(incidentStorageMock.findBy(id)).thenReturn(Optional.of(expectedIncident));

        // WHEN: Invocamos el caso de uso
        Optional<LabIncident> result = incidentByIdService.findById(id);

        // THEN: Validamos el contenedor inmutable
        assertTrue(result.isPresent());
        assertEquals("Fallo de red en pool Hikari", result.get().getErrorDescription());
        verify(incidentStorageMock, times(1)).findBy(id);
    }

    @Test
    @DisplayName("📌 Caso Alternativo: Debería retornar Optional vacío si el ID no figura en la bitácora")
    void shouldReturnEmptyOptionalWhenIdDoesNotExist() {
        // GIVEN: El storage devuelve vacío
        when(incidentStorageMock.findBy(anyString())).thenReturn(Optional.empty());

        // WHEN: Invocamos el caso de uso
        Optional<LabIncident> result = incidentByIdService.findById("INVALID-ID");

        // THEN: Comprobamos la ausencia limpia de datos
        assertFalse(result.isPresent());
        verify(incidentStorageMock, times(1)).findBy("INVALID-ID");
    }
}
