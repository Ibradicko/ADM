package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.CodeBarresProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CodeBarresProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String code;

    @NotNull
    private TypeCodeBarres type;

    @NotNull
    private Boolean principal;

    @NotNull
    private Boolean genereParSysteme;

    @NotNull
    private Boolean actif;

    @NotNull
    private Instant dateAffectation;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeCodeBarres getType() {
        return type;
    }

    public void setType(TypeCodeBarres type) {
        this.type = type;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Boolean getGenereParSysteme() {
        return genereParSysteme;
    }

    public void setGenereParSysteme(Boolean genereParSysteme) {
        this.genereParSysteme = genereParSysteme;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Instant getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(Instant dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeBarresProduitDTO)) {
            return false;
        }

        CodeBarresProduitDTO codeBarresProduitDTO = (CodeBarresProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, codeBarresProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CodeBarresProduitDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", type='" + getType() + "'" +
            ", principal='" + getPrincipal() + "'" +
            ", genereParSysteme='" + getGenereParSysteme() + "'" +
            ", actif='" + getActif() + "'" +
            ", dateAffectation='" + getDateAffectation() + "'" +
            ", produit=" + getProduit() +
            "}";
    }
}
