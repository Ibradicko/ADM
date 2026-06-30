package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RegleRedevance.
 */
@Entity
@Table(name = "regle_redevance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegleRedevance implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_regle", nullable = false)
    private TypeRegleRedevance typeRegle;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "taux", precision = 21, scale = 2, nullable = false)
    private BigDecimal taux;

    @NotNull
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Min(value = 1)
    @Column(name = "priorite")
    private Integer priorite;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif;

    @ManyToOne(fetch = FetchType.LAZY)
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    private Locataire locataire;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupeArticle groupeArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RegleRedevance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public RegleRedevance code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeRegleRedevance getTypeRegle() {
        return this.typeRegle;
    }

    public RegleRedevance typeRegle(TypeRegleRedevance typeRegle) {
        this.setTypeRegle(typeRegle);
        return this;
    }

    public void setTypeRegle(TypeRegleRedevance typeRegle) {
        this.typeRegle = typeRegle;
    }

    public BigDecimal getTaux() {
        return this.taux;
    }

    public RegleRedevance taux(BigDecimal taux) {
        this.setTaux(taux);
        return this;
    }

    public void setTaux(BigDecimal taux) {
        this.taux = taux;
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public RegleRedevance dateDebut(LocalDate dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public RegleRedevance dateFin(LocalDate dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getPriorite() {
        return this.priorite;
    }

    public RegleRedevance priorite(Integer priorite) {
        this.setPriorite(priorite);
        return this;
    }

    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public RegleRedevance actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public RegleRedevance boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public Locataire getLocataire() {
        return this.locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

    public RegleRedevance locataire(Locataire locataire) {
        this.setLocataire(locataire);
        return this;
    }

    public GroupeArticle getGroupeArticle() {
        return this.groupeArticle;
    }

    public void setGroupeArticle(GroupeArticle groupeArticle) {
        this.groupeArticle = groupeArticle;
    }

    public RegleRedevance groupeArticle(GroupeArticle groupeArticle) {
        this.setGroupeArticle(groupeArticle);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public RegleRedevance produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegleRedevance)) {
            return false;
        }
        return getId() != null && getId().equals(((RegleRedevance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegleRedevance{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", typeRegle='" + getTypeRegle() + "'" +
            ", taux=" + getTaux() +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", priorite=" + getPriorite() +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
