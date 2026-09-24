package co.com.devsoft.hosttelemetry.infrastructure.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import co.com.devsoft.hosttelemetry.application.bussines.ProcessService;

@Component
public class HostTelemetryTools {

    private final ProcessService process;

    public HostTelemetryTools(ProcessService process) {
        this.process = process;
    }

    @McpTool(name = "getTopCpuProcesses", // 🎯 FUERZA EL NOMBRE COMERCIAL DEL CONTRATO MCP
            description = "Recupera en tiempo real y con fluctuaciones dinámicas aleatorias los procesos que más CPU consumen en el host actual.")
    public String fetchTopCpuProcessesReport(
            @McpToolParam(description = "Cantidad máxima de procesos SRE a retornar en la tabla (por defecto es 3)", required = false) Integer limit) {

        int actualLimit = (limit != null) ? limit : 3;

        return process.getTopCpuProcesses(actualLimit);
    }

}
