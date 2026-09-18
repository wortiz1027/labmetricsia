package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto;

import java.time.LocalDateTime;

public record RestErrorResponse(
        String message,
        String errorType,
        int statusCode,
        LocalDateTime timestamp,
        String path) {
}
