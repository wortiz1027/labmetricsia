package co.com.devsoft.hosttelemetry.domain;

public record ProcessInfo(int pid, String name, double cpuUsage, String memoryUsage) {

    public String toMarckdownRow() {
        return String.format("| %d | %s | %.1f%% | %s |", pid, name, cpuUsage, memoryUsage);
    }

}
