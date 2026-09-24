package co.com.devsoft.hosttelemetry.domain;

import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T value) implements Result<T> {
    }

    record Failure<T>(String errorMessage, Throwable cause) implements Result<T> {
    }

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> failure(String message, Throwable cause) {
        return new Failure<>(message, cause);
    }

    static <T> Result<T> of(Supplier<T> supplier, String failureMessage) {
        try {
            return Result.success(supplier.get());
        } catch (Exception e) {
            return Result.failure(failureMessage, e);
        }
    }

    default <R> R fold(Function<T, R> onSuccess, Function<Failure<T>, R> onFailure) {
        return switch (this) {
            case Success<T> s -> onSuccess.apply(s.value());
            case Failure<T> f -> onFailure.apply(f);
        };
    }
}
