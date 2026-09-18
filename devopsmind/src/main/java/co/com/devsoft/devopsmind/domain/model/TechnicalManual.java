package co.com.devsoft.devopsmind.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;
import co.com.devsoft.devopsmind.domain.exception.InvalidDomainStateException;

public class TechnicalManual {

    private final ManualId id;
    private final String title;
    private final List<KnowledgeChunk> chunks;
    private boolean isDeprecated;

    public TechnicalManual(ManualId id, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDomainDataException("El título del manual técnico no puede estar vacío");
        }
        this.id = id != null ? id : ManualId.generate();
        this.title = title.trim();
        this.chunks = new ArrayList<>();
        this.isDeprecated = Boolean.FALSE;
    }

    public List<KnowledgeChunk> splitIntoChunks(String rawText, String sectionName) {
        if (this.isDeprecated) {
            throw new InvalidDomainStateException(
                    "Operación rechazada: El manual técnico se encuentra obsoleto/depreciado");
        }

        if (rawText == null || rawText.trim().isEmpty()) {
            throw new InvalidDomainDataException("El texto crudo para segmentar no puede estar vacío");
        }

        List<KnowledgeChunk> validChunks = new ArrayList<>();
        String[] paragraphs = rawText.split("\n\n");

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty())
                continue;

            KnowledgeChunk chunk = new KnowledgeChunk(paragraph.trim(), sectionName);
            if (chunk.isDescriptiveEnough()) {
                validChunks.add(chunk);
            }
        }

        this.chunks.addAll(validChunks);
        return Collections.unmodifiableList(validChunks);
    }

    public void deprecate() {
        this.isDeprecated = Boolean.TRUE;
        this.chunks.clear();
    }

    public ManualId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<KnowledgeChunk> getChunks() {
        return chunks;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TechnicalManual that = (TechnicalManual) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
