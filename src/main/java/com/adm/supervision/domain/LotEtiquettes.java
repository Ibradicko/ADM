package com.adm.supervision.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LotEtiquettes.
 */
@Entity
@Table(name = "lot_etiquettes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LotEtiquettes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "reference", length = 50, nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "date_generation", nullable = false)
    private Instant dateGeneration;

    @Size(max = 80)
    @Column(name = "format_impression", length = 80)
    private String formatImpression;

    @NotNull
    @Min(value = 1)
    @Column(name = "nombre_etiquettes", nullable = false)
    private Integer nombreEtiquettes;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LotEtiquettes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public LotEtiquettes reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateGeneration() {
        return this.dateGeneration;
    }

    public LotEtiquettes dateGeneration(Instant dateGeneration) {
        this.setDateGeneration(dateGeneration);
        return this;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public String getFormatImpression() {
        return this.formatImpression;
    }

    public LotEtiquettes formatImpression(String formatImpression) {
        this.setFormatImpression(formatImpression);
        return this;
    }

    public void setFormatImpression(String formatImpression) {
        this.formatImpression = formatImpression;
    }

    public Integer getNombreEtiquettes() {
        return this.nombreEtiquettes;
    }

    public LotEtiquettes nombreEtiquettes(Integer nombreEtiquettes) {
        this.setNombreEtiquettes(nombreEtiquettes);
        return this;
    }

    public void setNombreEtiquettes(Integer nombreEtiquettes) {
        this.nombreEtiquettes = nombreEtiquettes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LotEtiquettes)) {
            return false;
        }
        return getId() != null && getId().equals(((LotEtiquettes) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LotEtiquettes{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateGeneration='" + getDateGeneration() + "'" +
            ", formatImpression='" + getFormatImpression() + "'" +
            ", nombreEtiquettes=" + getNombreEtiquettes() +
            "}";
    }
}
