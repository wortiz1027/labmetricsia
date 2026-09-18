package co.com.devsoft.devopsmind.domain.model;

import java.util.Objects;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;

public class KnowledgeChunk {
    private final String content;
    private final String sectionName;
    private final int tokenCountEstimate;

    public KnowledgeChunk(String content, String sectionName) {
        if (content == null || content.trim().isEmpty())
            throw new InvalidDomainDataException("El contenido del fragmento no puede estar vacío");

        this.content = content.trim();
        this.sectionName = sectionName != null ? sectionName.trim() : "GENERAL";
        this.tokenCountEstimate = estimateTokens(this.content);
    }

    private int estimateTokens(String text) {
        return (int) Math.ceil(text.length() / 4.0);
    }

    public boolean isDescriptiveEnough() {
        return this.tokenCountEstimate >= 15;
    }

    public String formatForContext() {
        String template = """
                --- SECTION :: %s ---
                    Content: %s
                """;
        return String.format(template, this.sectionName, this.content);
    }

    public String getContent() {
        return content;
    }

    public String getSectionName() {
        return sectionName;
    }

    public int getTokenCountEstimate() {
        return tokenCountEstimate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KnowledgeChunk that = (KnowledgeChunk) o;
        return Objects.equals(content, that.content) && Objects.equals(sectionName, that.sectionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sectionName);
    }

}
