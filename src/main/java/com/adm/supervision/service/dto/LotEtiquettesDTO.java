package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LotEtiquettes} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LotEtiquettesDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String reference;

    @NotNull
    private Instant dateGeneration;

    @Size(max = 80)
    private String formatImpression;

    @NotNull
    @Min(value = 1)
    private Integer nombreEtiquettes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public String getFormatImpression() {
        return formatImpression;
    }

    public void setFormatImpression(String formatImpression) {
        this.formatImpression = formatImpression;
    }

    public Integer getNombreEtiquettes() {
        return nombreEtiquettes;
    }

    public void setNombreEtiquettes(Integer nombreEtiquettes) {
        this.nombreEtiquettes = nombreEtiquettes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LotEtiquettesDTO)) {
            return false;
        }

        LotEtiquettesDTO lotEtiquettesDTO = (LotEtiquettesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lotEtiquettesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LotEtiquettesDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateGeneration='" + getDateGeneration() + "'" +
            ", formatImpression='" + getFormatImpression() + "'" +
            ", nombreEtiquettes=" + getNombreEtiquettes() +
            "}";
    }
}
