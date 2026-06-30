package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ScanInconnu.
 */
@Entity
@Table(name = "scan_inconnu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScanInconnu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "code_scanne", length = 80, nullable = false)
    private String codeScanne;

    @Size(max = 80)
    @Column(name = "ecran_origine", length = 80)
    private String ecranOrigine;

    @NotNull
    @Column(name = "date_scan", nullable = false)
    private Instant dateScan;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @NotNull
    @Column(name = "resolu", nullable = false)
    private Boolean resolu;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produitAffecte;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScanInconnu id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeScanne() {
        return this.codeScanne;
    }

    public ScanInconnu codeScanne(String codeScanne) {
        this.setCodeScanne(codeScanne);
        return this;
    }

    public void setCodeScanne(String codeScanne) {
        this.codeScanne = codeScanne;
    }

    public String getEcranOrigine() {
        return this.ecranOrigine;
    }

    public ScanInconnu ecranOrigine(String ecranOrigine) {
        this.setEcranOrigine(ecranOrigine);
        return this;
    }

    public void setEcranOrigine(String ecranOrigine) {
        this.ecranOrigine = ecranOrigine;
    }

    public Instant getDateScan() {
        return this.dateScan;
    }

    public ScanInconnu dateScan(Instant dateScan) {
        this.setDateScan(dateScan);
        return this;
    }

    public void setDateScan(Instant dateScan) {
        this.dateScan = dateScan;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public ScanInconnu commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getResolu() {
        return this.resolu;
    }

    public ScanInconnu resolu(Boolean resolu) {
        this.setResolu(resolu);
        return this;
    }

    public void setResolu(Boolean resolu) {
        this.resolu = resolu;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public ScanInconnu boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public Produit getProduitAffecte() {
        return this.produitAffecte;
    }

    public void setProduitAffecte(Produit produit) {
        this.produitAffecte = produit;
    }

    public ScanInconnu produitAffecte(Produit produit) {
        this.setProduitAffecte(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScanInconnu)) {
            return false;
        }
        return getId() != null && getId().equals(((ScanInconnu) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScanInconnu{" +
            "id=" + getId() +
            ", codeScanne='" + getCodeScanne() + "'" +
            ", ecranOrigine='" + getEcranOrigine() + "'" +
            ", dateScan='" + getDateScan() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", resolu='" + getResolu() + "'" +
            "}";
    }
}
