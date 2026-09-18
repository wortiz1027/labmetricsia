package co.com.devsoft.devopsmind.infrastructure.config.ai;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

import javax.sql.DataSource;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(
            @Qualifier("postgresDataSource") DataSource postgresDataSource,
            EmbeddingModel embeddingModel) {

        JdbcTemplate postgresJdbcTemplate = new JdbcTemplate(postgresDataSource);

        return PgVectorStore.builder(postgresJdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .vectorTableName("vector_store")
                .schemaName("public")
                .maxDocumentBatchSize(10000)
                .initializeSchema(false)
                .build();
    }

}
