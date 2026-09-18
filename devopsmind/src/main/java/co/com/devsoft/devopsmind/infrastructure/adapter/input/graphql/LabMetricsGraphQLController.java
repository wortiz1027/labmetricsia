package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import co.com.devsoft.devopsmind.domain.model.LabIncident;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.repository.LabIncidentStorage;
import co.com.devsoft.devopsmind.domain.repository.TechnicalManualStorage;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.KnowledgeChunkDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.LabIncidentDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.ServerMetricsInputDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.dto.TechnicalManualDTO;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.mapper.GraphQLMapper;

@Controller
public class LabMetricsGraphQLController {

    private static final Logger log = LoggerFactory.getLogger(LabMetricsGraphQLController.class);

    private final IndexManualUseCase indexManualUseCase;
    private final AnalyzeIncidentUseCase analyzeIncidentUseCase;
    private final TechnicalManualStorage technicalManualStorage;
    private final LabIncidentStorage labIncidentStorage;

    public LabMetricsGraphQLController(IndexManualUseCase indexManualUseCase,
            AnalyzeIncidentUseCase analyzeIncidentUseCase, TechnicalManualStorage technicalManualStorage,
            LabIncidentStorage labIncidentStorage) {
        this.indexManualUseCase = indexManualUseCase;
        this.analyzeIncidentUseCase = analyzeIncidentUseCase;
        this.technicalManualStorage = technicalManualStorage;
        this.labIncidentStorage = labIncidentStorage;
    }

    @QueryMapping
    public List<TechnicalManualDTO> activeManuals() {
        return this.technicalManualStorage.findActiveManuals().stream()
                .map(GraphQLMapper::toDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public LabIncidentDTO incidentById(@Argument String id) {
        return this.labIncidentStorage.findBy(id)
                .map(GraphQLMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        "El incidente de laboratorio solicitado con ID '%s' no fue localizado en la bitácora.", id)));
    }

    @QueryMapping
    public List<KnowledgeChunkDTO> searchKnowledge(@Argument String query, @Argument int maxResults) {
        return this.technicalManualStorage.findRelevantChunks(query, maxResults).stream()
                .map(GraphQLMapper::toDTO)
                .filter(opt -> opt != null && opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());

    }

    @MutationMapping
    public boolean indexManualText(@Argument String manualId,
            @Argument String rawText,
            @Argument String sectionName) {
        try {
            indexManualUseCase.index(new ManualId(manualId), rawText, sectionName);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("❌ Erro durante la indexación por resolver GraphQL: {}", e.getMessage());
            return Boolean.FALSE;
        }
    }

    @MutationMapping
    public LabIncidentDTO reportIncident(@Argument String errorDescription,
            @Argument ServerMetricsInputDTO metrics) {
        var domainMetrics = GraphQLMapper.toDomain(metrics);

        LabIncident analyzedIncident = analyzeIncidentUseCase.analyze(errorDescription, domainMetrics);

        return GraphQLMapper.toDTO(analyzedIncident);
    }

}
