package co.com.devsoft.devopsmind.infrastructure.adapter.input.graphql.exception;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainStateException;
import co.com.devsoft.devopsmind.domain.exception.ResourceNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQLExceptionHandlerAdvice {

    @GraphQlExceptionHandler
    public GraphQLError handleResourceNotFound(ResourceNotFoundException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.NOT_FOUND) // Código semántico equivalente a 404
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidData(InvalidDomainDataException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.BAD_REQUEST) // Código semántico equivalente a 400
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleInvalidState(InvalidDomainStateException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.INTERNAL_ERROR) // Código semántico equivalente a 500
                .build();
    }
}
