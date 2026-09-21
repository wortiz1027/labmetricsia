package co.com.devsoft.devopsmind.infrastructure.adapter.exceptions;

public class UseCaseTransactionException extends RuntimeException {
    public UseCaseTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
