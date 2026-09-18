package co.com.devsoft.devopsmind.domain.model;

import java.util.Objects;
import java.util.UUID;

import co.com.devsoft.devopsmind.domain.exception.InvalidDomainDataException;

public class ManualId {
    private final String value;

    public ManualId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDomainDataException(
                    "El valor del identificador del manual no puede estar vacío o ser nulo");
        }
        this.value = value.trim();
    }

    public static ManualId generate() {
        return new ManualId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ManualId manualId = (ManualId) o;
        return Objects.equals(value, manualId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
