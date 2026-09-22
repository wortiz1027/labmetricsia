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
import co.com.devsoft.devopsmind.application.ports.input.FindActiveManualsUseCase;
import co.com.devsoft.devopsmind.application.ports.input.FindIncidentByIdUseCase;
import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.application.ports.input.SearchKnowledgeUseCase;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
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
    private final FindActiveManualsUseCase findActiveManualsUseCase;
    private final FindIncidentByIdUseCase findIncidentByIdUseCase;
    private final SearchKnowledgeUseCase searchKnowledgeUseCase;

    public LabMetricsGraphQLController(IndexManualUseCase indexManualUseCase,
            AnalyzeIncidentUseCase analyzeIncidentUseCase,
            FindActiveManualsUseCase findActiveManualsUseCase,
            FindIncidentByIdUseCase findIncidentByIdUseCase,
            SearchKnowledgeUseCase searchKnowledgeUseCase) {
        this.indexManualUseCase = indexManualUseCase;
        this.analyzeIncidentUseCase = analyzeIncidentUseCase;
        this.findActiveManualsUseCase = findActiveManualsUseCase;
        this.findIncidentByIdUseCase = findIncidentByIdUseCase;
        this.searchKnowledgeUseCase = searchKnowledgeUseCase;
    }

    @QueryMapping
    public List<TechnicalManualDTO> activeManuals() {
        return findActiveManualsUseCase.findActive().stream()
                .map(GraphQLMapper::toDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public LabIncidentDTO incidentById(@Argument String id) {
        return findIncidentByIdUseCase.findById(id)
                .map(GraphQLMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El incidente solicitado con ID '" + id + "' no fue localizado."));
    }

    @QueryMapping
    public List<KnowledgeChunkDTO> searchKnowledge(@Argument String query, @Argument int maxResults) {
        return searchKnowledgeUseCase.search(query, maxResults).stream()
                .map(GraphQLMapper::toDTO)
                .filter(opt -> opt != null && opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());
    }

    @MutationMapping
    public boolean indexManualText(@Argument String manualId, @Argument String rawText, @Argument String sectionName) {
        indexManualUseCase.index(new co.com.devsoft.devopsmind.domain.model.ManualId(manualId), rawText, sectionName);
        return true;
    }

    @MutationMapping
    public LabIncidentDTO reportIncident(@Argument String errorDescription, @Argument ServerMetricsInputDTO metrics) {
        var domainMetrics = GraphQLMapper.toDomain(metrics);
        var analyzed = analyzeIncidentUseCase.analyze(errorDescription, domainMetrics);
        return GraphQLMapper.toDTO(analyzed);
    }

}
