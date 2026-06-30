package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A TicketCaisse.
 */
@Entity
@Table(name = "ticket_caisse")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCaisse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "numero", length = 80, nullable = false, unique = true)
    private String numero;

    @NotNull
    @Column(name = "date_emission", nullable = false)
    private Instant dateEmission;

    @NotNull
    @Min(value = 1)
    @Column(name = "nombre_impressions", nullable = false)
    private Integer nombreImpressions;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "contenu")
    private String contenu;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire", "vendeur" }, allowSetters = true)
    private Vente vente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TicketCaisse id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return this.numero;
    }

    public TicketCaisse numero(String numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Instant getDateEmission() {
        return this.dateEmission;
    }

    public TicketCaisse dateEmission(Instant dateEmission) {
        this.setDateEmission(dateEmission);
        return this;
    }

    public void setDateEmission(Instant dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Integer getNombreImpressions() {
        return this.nombreImpressions;
    }

    public TicketCaisse nombreImpressions(Integer nombreImpressions) {
        this.setNombreImpressions(nombreImpressions);
        return this;
    }

    public void setNombreImpressions(Integer nombreImpressions) {
        this.nombreImpressions = nombreImpressions;
    }

    public String getContenu() {
        return this.contenu;
    }

    public TicketCaisse contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Vente getVente() {
        return this.vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public TicketCaisse vente(Vente vente) {
        this.setVente(vente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketCaisse)) {
            return false;
        }
        return getId() != null && getId().equals(((TicketCaisse) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCaisse{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", dateEmission='" + getDateEmission() + "'" +
            ", nombreImpressions=" + getNombreImpressions() +
            ", contenu='" + getContenu() + "'" +
            "}";
    }
}
