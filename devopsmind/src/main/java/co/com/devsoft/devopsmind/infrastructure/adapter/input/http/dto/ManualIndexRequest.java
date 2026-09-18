package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

public record ManualIndexRequest(
        String rawText,
        String sectionName) {
}
