package co.com.devsoft.devopsmind.infrastructure.adapter.input.http.exception;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainStateException;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import co.com.devsoft.devopsmind.infrastructure.adapter.input.http.dto.RestErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "co.com.devsoft.devopsmind.infrastructure.adapter.input.http")
public class RestControllerAdviceHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        RestErrorResponse error = new RestErrorResponse(
                ex.getMessage(),
                status.name(),
                status.value(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidDomainDataException.class)
    public ResponseEntity<RestErrorResponse> handleInvalidData(
            InvalidDomainDataException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        RestErrorResponse error = new RestErrorResponse(
                ex.getMessage(),
                status.name(),
                status.value(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidDomainStateException.class)
    public ResponseEntity<RestErrorResponse> handleInvalidState(
            InvalidDomainStateException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        RestErrorResponse error = new RestErrorResponse(
                ex.getMessage(),
                status.name(),
                status.value(),
                LocalDateTime.now(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
