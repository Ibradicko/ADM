package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProduitCriteriaTest {

    @Test
    void newProduitCriteriaHasAllFiltersNullTest() {
        var produitCriteria = new ProduitCriteria();
        assertThat(produitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void produitCriteriaFluentMethodsCreatesFiltersTest() {
        var produitCriteria = new ProduitCriteria();

        setAllFilters(produitCriteria);

        assertThat(produitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void produitCriteriaCopyCreatesNullFilterTest() {
        var produitCriteria = new ProduitCriteria();
        var copy = produitCriteria.copy();

        assertThat(produitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(produitCriteria)
        );
    }

    @Test
    void produitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var produitCriteria = new ProduitCriteria();
        setAllFilters(produitCriteria);

        var copy = produitCriteria.copy();

        assertThat(produitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(produitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var produitCriteria = new ProduitCriteria();

        assertThat(produitCriteria).hasToString("ProduitCriteria{}");
    }

    private static void setAllFilters(ProduitCriteria produitCriteria) {
        produitCriteria.id();
        produitCriteria.codeInterne();
        produitCriteria.designation();
        produitCriteria.typePrix();
        produitCriteria.prixVente();
        produitCriteria.tauxRedevanceApplicable();
        produitCriteria.statut();
        produitCriteria.dateCreation();
        produitCriteria.boutiqueId();
        produitCriteria.groupeArticleId();
        produitCriteria.familleArticleId();
        produitCriteria.sousFamilleArticleId();
        produitCriteria.uniteMesureId();
        produitCriteria.distinct();
    }

    private static Condition<ProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCodeInterne()) &&
                condition.apply(criteria.getDesignation()) &&
                condition.apply(criteria.getTypePrix()) &&
                condition.apply(criteria.getPrixVente()) &&
                condition.apply(criteria.getTauxRedevanceApplicable()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getGroupeArticleId()) &&
                condition.apply(criteria.getFamilleArticleId()) &&
                condition.apply(criteria.getSousFamilleArticleId()) &&
                condition.apply(criteria.getUniteMesureId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProduitCriteria> copyFiltersAre(ProduitCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCodeInterne(), copy.getCodeInterne()) &&
                condition.apply(criteria.getDesignation(), copy.getDesignation()) &&
                condition.apply(criteria.getTypePrix(), copy.getTypePrix()) &&
                condition.apply(criteria.getPrixVente(), copy.getPrixVente()) &&
                condition.apply(criteria.getTauxRedevanceApplicable(), copy.getTauxRedevanceApplicable()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getGroupeArticleId(), copy.getGroupeArticleId()) &&
                condition.apply(criteria.getFamilleArticleId(), copy.getFamilleArticleId()) &&
                condition.apply(criteria.getSousFamilleArticleId(), copy.getSousFamilleArticleId()) &&
                condition.apply(criteria.getUniteMesureId(), copy.getUniteMesureId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
