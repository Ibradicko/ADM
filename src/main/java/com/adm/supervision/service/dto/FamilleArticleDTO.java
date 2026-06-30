package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.FamilleArticle} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FamilleArticleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String code;

    @NotNull
    @Size(max = 150)
    private String libelle;

    @NotNull
    private StatutGeneral statut;

    @NotNull
    private GroupeArticleDTO groupeArticle;

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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public StatutGeneral getStatut() {
        return statut;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public GroupeArticleDTO getGroupeArticle() {
        return groupeArticle;
    }

    public void setGroupeArticle(GroupeArticleDTO groupeArticle) {
        this.groupeArticle = groupeArticle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FamilleArticleDTO)) {
            return false;
        }

        FamilleArticleDTO familleArticleDTO = (FamilleArticleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, familleArticleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FamilleArticleDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", statut='" + getStatut() + "'" +
            ", groupeArticle=" + getGroupeArticle() +
            "}";
    }
}
