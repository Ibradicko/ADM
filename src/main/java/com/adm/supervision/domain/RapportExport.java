package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.FormatExport;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RapportExport.
 */
@Entity
@Table(name = "rapport_export")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RapportExport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "reference", length = 80, nullable = false, unique = true)
    private String reference;

    @NotNull
    @Size(max = 100)
    @Column(name = "type_rapport", length = 100, nullable = false)
    private String typeRapport;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private FormatExport format;

    @Column(name = "periode_debut")
    private LocalDate periodeDebut;

    @Column(name = "periode_fin")
    private LocalDate periodeFin;

    @Size(max = 255)
    @Column(name = "chemin_fichier", length = 255)
    private String cheminFichier;

    @NotNull
    @Column(name = "date_generation", nullable = false)
    private Instant dateGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    private Locataire locataire;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RapportExport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public RapportExport reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTypeRapport() {
        return this.typeRapport;
    }

    public RapportExport typeRapport(String typeRapport) {
        this.setTypeRapport(typeRapport);
        return this;
    }

    public void setTypeRapport(String typeRapport) {
        this.typeRapport = typeRapport;
    }

    public FormatExport getFormat() {
        return this.format;
    }

    public RapportExport format(FormatExport format) {
        this.setFormat(format);
        return this;
    }

    public void setFormat(FormatExport format) {
        this.format = format;
    }

    public LocalDate getPeriodeDebut() {
        return this.periodeDebut;
    }

    public RapportExport periodeDebut(LocalDate periodeDebut) {
        this.setPeriodeDebut(periodeDebut);
        return this;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return this.periodeFin;
    }

    public RapportExport periodeFin(LocalDate periodeFin) {
        this.setPeriodeFin(periodeFin);
        return this;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    public String getCheminFichier() {
        return this.cheminFichier;
    }

    public RapportExport cheminFichier(String cheminFichier) {
        this.setCheminFichier(cheminFichier);
        return this;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public Instant getDateGeneration() {
        return this.dateGeneration;
    }

    public RapportExport dateGeneration(Instant dateGeneration) {
        this.setDateGeneration(dateGeneration);
        return this;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public RapportExport boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public Locataire getLocataire() {
        return this.locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

    public RapportExport locataire(Locataire locataire) {
        this.setLocataire(locataire);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public RapportExport utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RapportExport)) {
            return false;
        }
        return getId() != null && getId().equals(((RapportExport) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RapportExport{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeRapport='" + getTypeRapport() + "'" +
            ", format='" + getFormat() + "'" +
            ", periodeDebut='" + getPeriodeDebut() + "'" +
            ", periodeFin='" + getPeriodeFin() + "'" +
            ", cheminFichier='" + getCheminFichier() + "'" +
            ", dateGeneration='" + getDateGeneration() + "'" +
            "}";
    }
}
