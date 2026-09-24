package co.com.devsoft.hosttelemetry.domain;

import java.util.List;

public interface TelemetryProvider {

    Result<List<ProcessInfo>> getTopCpuProcesses(int limit);

}
