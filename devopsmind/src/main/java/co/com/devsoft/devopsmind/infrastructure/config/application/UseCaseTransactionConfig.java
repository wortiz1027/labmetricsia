package co.com.devsoft.devopsmind.infrastructure.config.application;

import java.util.function.Supplier;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.Transactional;

import co.com.devsoft.devopsmind.application.ports.input.UseCase;
import co.com.devsoft.devopsmind.infrastructure.adapter.exceptions.UseCaseTransactionException;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(
    basePackages = "co.com.devsoft.devopsmind.application.service",
    includeFilters = @ComponentScan .Filter(
        type = FilterType.ANNOTATION,
        value = UseCase.class
    )
)
public class UseCaseTransactionConfig {

    @Bean
    TransactionalUseCaseAspect TransactionalUseCaseAspect(UseCaseTransactionExecutor executor) {
        return new TransactionalUseCaseAspect(executor);
    }

    @Bean
    UseCaseTransactionExecutor UseCaseTransactionExecutor() {
        return new UseCaseTransactionExecutor();
    }

    public static class UseCaseTransactionExecutor {

        @Transactional("transactionManager")
        public <T> T executeInTransaction(Supplier<T> execution) {
            return execution.get();
        }
    }

    public static class TransactionalUseCaseAspect {
        private final UseCaseTransactionExecutor transactionExecutor;

        public TransactionalUseCaseAspect(UseCaseTransactionExecutor transactionExecutor) {
            this.transactionExecutor = transactionExecutor;
        }

        @Pointcut("@within(useCase)")
        void inUseCase(UseCase useCase) {
        }

        @Around("inUseCase(useCase)")
        public Object manageTransaction(ProceedingJoinPoint joinPoint, UseCase useCase) throws Throwable {
            return transactionExecutor.executeInTransaction(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new UseCaseTransactionException(String.format("%s%s",
                            "Error ejecutando Caso de Uso transaccional: ", e.getMessage()), e);
                }
            });
        }
    }

}
