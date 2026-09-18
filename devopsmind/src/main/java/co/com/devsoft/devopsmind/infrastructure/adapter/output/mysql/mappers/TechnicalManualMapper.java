package co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.mappers;

import co.com.devsoft.devopsmind.domain.model.ManualId;
import co.com.devsoft.devopsmind.domain.model.TechnicalManual;
import co.com.devsoft.devopsmind.infrastructure.adapter.output.mysql.entities.TechnicalManualEntity;

public final class TechnicalManualMapper {

    private TechnicalManualMapper() {
        throw new AssertionError("No se permite instanciar TechnicalManualMapper");
    }

    public static TechnicalManualEntity toEntity(TechnicalManual domain) {
        if (domain == null)
            return null;

        TechnicalManualEntity entity = new TechnicalManualEntity();
        entity.setId(domain.getId().getValue());
        entity.setTitle(domain.getTitle());
        entity.setIsDeprecated(domain.isDeprecated());
        entity.setChunkCount(domain.getChunks().size());

        entity.setVersion("1.0.0");
        entity.setFilePath(String.format("/workspaces/storage/%s.pdf", domain.getId().getValue()));
        entity.setFileSizeBytes(1024L);
        entity.setUploadedBy("SYSTEM-ADMIN");

        return entity;
    }

    public static TechnicalManual toDomain(TechnicalManualEntity entity) {
        if (entity == null)
            return null;

        TechnicalManual manual = new TechnicalManual(ManualId.generate(), entity.getTitle());

        if (Boolean.TRUE.equals(entity.getIsDeprecated())) {
            manual.deprecate();
        }

        return manual;
    }
}
