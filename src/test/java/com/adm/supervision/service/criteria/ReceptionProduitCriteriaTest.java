package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReceptionProduitCriteriaTest {

    @Test
    void newReceptionProduitCriteriaHasAllFiltersNullTest() {
        var receptionProduitCriteria = new ReceptionProduitCriteria();
        assertThat(receptionProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void receptionProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var receptionProduitCriteria = new ReceptionProduitCriteria();

        setAllFilters(receptionProduitCriteria);

        assertThat(receptionProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void receptionProduitCriteriaCopyCreatesNullFilterTest() {
        var receptionProduitCriteria = new ReceptionProduitCriteria();
        var copy = receptionProduitCriteria.copy();

        assertThat(receptionProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(receptionProduitCriteria)
        );
    }

    @Test
    void receptionProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var receptionProduitCriteria = new ReceptionProduitCriteria();
        setAllFilters(receptionProduitCriteria);

        var copy = receptionProduitCriteria.copy();

        assertThat(receptionProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(receptionProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var receptionProduitCriteria = new ReceptionProduitCriteria();

        assertThat(receptionProduitCriteria).hasToString("ReceptionProduitCriteria{}");
    }

    private static void setAllFilters(ReceptionProduitCriteria receptionProduitCriteria) {
        receptionProduitCriteria.id();
        receptionProduitCriteria.reference();
        receptionProduitCriteria.dateReception();
        receptionProduitCriteria.fournisseur();
        receptionProduitCriteria.commentaire();
        receptionProduitCriteria.boutiqueId();
        receptionProduitCriteria.utilisateurId();
        receptionProduitCriteria.distinct();
    }

    private static Condition<ReceptionProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getDateReception()) &&
                condition.apply(criteria.getFournisseur()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReceptionProduitCriteria> copyFiltersAre(
        ReceptionProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getDateReception(), copy.getDateReception()) &&
                condition.apply(criteria.getFournisseur(), copy.getFournisseur()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
