package com.adm.supervision.service;

import com.adm.supervision.domain.ExploitationBoutique;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Resolves the royalty rate (taux de redevance) applicable to a sale line, by priority:
 * matching {@link RegleRedevance} (produit &gt; groupe article &gt; locataire &gt; boutique, then
 * priorite/specificite/date), then the produit's own taux, then the boutique/locataire exploitation
 * default rate.
 */
@Service
public class RedevanceRateResolver {

    public BigDecimal resolveRate(
        List<RegleRedevance> rules,
        List<ExploitationBoutique> exploitations,
        Produit produit,
        Vente vente,
        LocalDate saleDate
    ) {
        return rules
            .stream()
            .filter(rule -> matches(rule, produit, vente, saleDate))
            .min(
                Comparator.comparing((RegleRedevance rule) -> rule.getPriorite() == null ? Integer.MAX_VALUE : rule.getPriorite())
                    .thenComparing((RegleRedevance rule) -> specificity(rule.getTypeRegle()), Comparator.reverseOrder())
                    .thenComparing(RegleRedevance::getDateDebut, Comparator.reverseOrder())
                    .thenComparing(rule -> rule.getId() == null ? Long.MAX_VALUE : rule.getId())
            )
            .map(RegleRedevance::getTaux)
            .orElseGet(() -> fallbackRate(exploitations, produit, vente, saleDate));
    }

    private boolean matches(RegleRedevance rule, Produit produit, Vente vente, LocalDate saleDate) {
        if (!Boolean.TRUE.equals(rule.getActif()) || saleDate.isBefore(rule.getDateDebut())) {
            return false;
        }
        if (rule.getDateFin() != null && saleDate.isAfter(rule.getDateFin())) {
            return false;
        }
        if (rule.getBoutique() != null && !Objects.equals(rule.getBoutique().getId(), vente.getBoutique().getId())) {
            return false;
        }
        if (rule.getLocataire() != null && !Objects.equals(rule.getLocataire().getId(), vente.getLocataire().getId())) {
            return false;
        }
        return switch (rule.getTypeRegle()) {
            case PRODUIT -> rule.getProduit() != null && Objects.equals(rule.getProduit().getId(), produit.getId());
            case GROUPE_ARTICLE -> rule.getGroupeArticle() != null &&
            produit.getGroupeArticle() != null &&
            Objects.equals(rule.getGroupeArticle().getId(), produit.getGroupeArticle().getId());
            case BOUTIQUE -> rule.getBoutique() != null && Objects.equals(rule.getBoutique().getId(), vente.getBoutique().getId());
            case LOCATAIRE -> rule.getLocataire() != null && Objects.equals(rule.getLocataire().getId(), vente.getLocataire().getId());
        };
    }

    private BigDecimal fallbackRate(List<ExploitationBoutique> exploitations, Produit produit, Vente vente, LocalDate saleDate) {
        if (produit.getTauxRedevanceApplicable() != null) {
            return produit.getTauxRedevanceApplicable();
        }
        return exploitations
            .stream()
            .filter(exploitation -> exploitation.getStatut() == StatutGeneral.ACTIF)
            .filter(exploitation -> Objects.equals(exploitation.getBoutique().getId(), vente.getBoutique().getId()))
            .filter(exploitation -> Objects.equals(exploitation.getLocataire().getId(), vente.getLocataire().getId()))
            .filter(exploitation -> !saleDate.isBefore(exploitation.getDateDebut()))
            .filter(exploitation -> exploitation.getDateFin() == null || !saleDate.isAfter(exploitation.getDateFin()))
            .map(ExploitationBoutique::getTauxRedevanceDefaut)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.ZERO);
    }

    private int specificity(TypeRegleRedevance type) {
        return switch (type) {
            case PRODUIT -> 4;
            case GROUPE_ARTICLE -> 3;
            case LOCATAIRE -> 2;
            case BOUTIQUE -> 1;
        };
    }
}
