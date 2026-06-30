package com.adm.supervision.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.TicketCaisse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCaisseDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String numero;

    @NotNull
    private Instant dateEmission;

    @NotNull
    @Min(value = 1)
    private Integer nombreImpressions;

    @Lob
    private String contenu;

    @NotNull
    private VenteDTO vente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Instant getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(Instant dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Integer getNombreImpressions() {
        return nombreImpressions;
    }

    public void setNombreImpressions(Integer nombreImpressions) {
        this.nombreImpressions = nombreImpressions;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TicketCaisseDTO)) {
            return false;
        }

        TicketCaisseDTO ticketCaisseDTO = (TicketCaisseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ticketCaisseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCaisseDTO{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", dateEmission='" + getDateEmission() + "'" +
            ", nombreImpressions=" + getNombreImpressions() +
            ", contenu='" + getContenu() + "'" +
            ", vente=" + getVente() +
            "}";
    }
}
