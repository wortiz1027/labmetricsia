package co.com.devsoft.devopsmind.infrastructure.adapter.input.http;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.devsoft.devopsmind.application.ports.input.AnalyzeIncidentUseCase;
import co.com.devsoft.devopsmind.application.ports.input.FindIncidentByIdUseCase;
import co.com.devsoft.devopsmind.application.ports.input.IndexManualUseCase;
import co.com.devsoft.devopsmind.application.ports.input.SearchKnowledgeUseCase;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.IncidentReportRequest;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.KnowledgeChunkResponse;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.LabIncidentResponse;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.ManualIndexRequest;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.mapper.RestMapper;

@RestController
@RequestMapping("/api/v1/lab")
public class LabMetricsRestController {

    private final IndexManualUseCase indexManualUseCase;
    private final AnalyzeIncidentUseCase analyzeIncidentUseCase;
    private final FindIncidentByIdUseCase findIncidentByIdUseCase;
    private final SearchKnowledgeUseCase searchKnowledgeUseCase;

    public LabMetricsRestController(IndexManualUseCase indexManualUseCase,
            AnalyzeIncidentUseCase analyzeIncidentUseCase,
            FindIncidentByIdUseCase findIncidentByIdUseCase,
            SearchKnowledgeUseCase searchKnowledgeUseCase) {
        this.indexManualUseCase = indexManualUseCase;
        this.analyzeIncidentUseCase = analyzeIncidentUseCase;
        this.findIncidentByIdUseCase = findIncidentByIdUseCase;
        this.searchKnowledgeUseCase = searchKnowledgeUseCase;
    }

    @PostMapping("/manuals/{manualId}/index")
    public ResponseEntity<Void> indexManualText(@PathVariable String manualId,
            @RequestBody ManualIndexRequest request) {
        indexManualUseCase.index(new ManualId(manualId), request.rawText(), request.sectionName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/incidents")
    public ResponseEntity<LabIncidentResponse> reportIncident(@RequestBody IncidentReportRequest request) {
        var domainMetrics = RestMapper.toDomain(request.metrics());
        var analyzed = analyzeIncidentUseCase.analyze(request.errorDescription(), domainMetrics);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestMapper.toResponse(analyzed));
    }

    @GetMapping("/incidents/{id}")
    public ResponseEntity<LabIncidentResponse> getIncidentById(@PathVariable String id) {
        var response = findIncidentByIdUseCase.findById(id)
                .map(RestMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El incidente con ID '" + id + "' no existe en los registros de observabilidad."));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/knowledge/search")
    public ResponseEntity<List<KnowledgeChunkResponse>> searchKnowledge(@RequestParam String query,
            @RequestParam(defaultValue = "3") int maxResults) {
        List<KnowledgeChunkResponse> chunks = searchKnowledgeUseCase.search(query, maxResults).stream()
                .map(RestMapper::toResponse)
                .filter(opt -> opt != null && opt.isPresent())
                .map(opt -> opt.get())
                .collect(Collectors.toList());
        return ResponseEntity.ok(chunks);
    }
}
