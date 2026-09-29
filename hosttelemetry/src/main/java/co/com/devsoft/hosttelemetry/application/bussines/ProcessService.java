package co.com.devsoft.hosttelemetry.application.bussines;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.devsoft.hosttelemetry.domain.ProcessInfo;
import co.com.devsoft.hosttelemetry.domain.TelemetryProvider;

@Service
public class ProcessService {

    private final TelemetryProvider provider;

    public ProcessService(TelemetryProvider provider) {
        this.provider = provider;
    }

    public String getTopCpuProcesses(Integer limit) {
        int actualLimit = (limit != null) ? limit : 3;

        var telemetryResult = this.provider.getTopCpuProcesses(actualLimit);

        return telemetryResult.<String>fold(processes -> buildMarckdownTable(processes),
                failure -> String.format("### ❌ FALLO DE TELEMETRÍA\n%s", failure.errorMessage()));
    }

    private String buildMarckdownTable(List<ProcessInfo> processes) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 📊 TELEMETRÍA DE PROCESOS DEL HOST (Métricas Estocásticas Dynamicas)\n");
        sb.append("| PID | PROCESO OPERATIVO | CPU USAGE | MEMORIA ASIGNADA |\n");
        sb.append("| --- | --- | --- | --- |\n");

        processes.forEach(p -> sb.append(p.toMarckdownRow()).append("\n"));

        return sb.toString();
    }
}
