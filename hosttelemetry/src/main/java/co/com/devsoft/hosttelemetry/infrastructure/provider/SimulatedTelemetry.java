package co.com.devsoft.hosttelemetry.infrastructure.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import co.com.devsoft.hosttelemetry.domain.ProcessInfo;
import co.com.devsoft.hosttelemetry.domain.Result;
import co.com.devsoft.hosttelemetry.domain.TelemetryProvider;

@Component
public class SimulatedTelemetry implements TelemetryProvider {

    @Override
    public Result<List<ProcessInfo>> getTopCpuProcesses(int limit) {
        if (limit <= 0)
            return Result.failure("El parámetro 'limit' debe ser un entero estrictamente mayor a cero.", null);

        return Result.of(() -> {
            List<ProcessInfo> processes = generateRandomMetrics();

            return processes.stream()
                    .sorted(Comparator.comparingDouble(ProcessInfo::cpuUsage).reversed())
                    .limit(limit)
                    .toList();
        }, "Error crítico inesperado recolectando telemetría del host.");
    }

    private List<ProcessInfo> generateRandomMetrics() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<ProcessInfo> processes = new ArrayList<>();

        processes.add(new ProcessInfo(1024, "ollama_container_daemon", random.nextDouble(30.0, 75.0),
                String.format("%.1f GB", random.nextDouble(6.0, 12.0))));
        processes.add(new ProcessInfo(2048, "mysqld_lts_service", random.nextDouble(10.0, 40.0),
                String.format("%.1f GB", random.nextDouble(1.5, 4.0))));
        processes.add(new ProcessInfo(4046, "postgres_vector_store", random.nextDouble(5.0, 35.0),
                String.format("%.1f GB", random.nextDouble(1.0, 3.0))));
        processes.add(new ProcessInfo(8192, "devopsmind_backend", random.nextDouble(1.0, 15.0),
                String.format("%.1f GB", random.nextDouble(0.5, 1.5))));

        return processes;
    }
}
