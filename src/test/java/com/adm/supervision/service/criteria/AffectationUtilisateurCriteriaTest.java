package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AffectationUtilisateurCriteriaTest {

    @Test
    void newAffectationUtilisateurCriteriaHasAllFiltersNullTest() {
        var affectationUtilisateurCriteria = new AffectationUtilisateurCriteria();
        assertThat(affectationUtilisateurCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void affectationUtilisateurCriteriaFluentMethodsCreatesFiltersTest() {
        var affectationUtilisateurCriteria = new AffectationUtilisateurCriteria();

        setAllFilters(affectationUtilisateurCriteria);

        assertThat(affectationUtilisateurCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void affectationUtilisateurCriteriaCopyCreatesNullFilterTest() {
        var affectationUtilisateurCriteria = new AffectationUtilisateurCriteria();
        var copy = affectationUtilisateurCriteria.copy();

        assertThat(affectationUtilisateurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(affectationUtilisateurCriteria)
        );
    }

    @Test
    void affectationUtilisateurCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var affectationUtilisateurCriteria = new AffectationUtilisateurCriteria();
        setAllFilters(affectationUtilisateurCriteria);

        var copy = affectationUtilisateurCriteria.copy();

        assertThat(affectationUtilisateurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(affectationUtilisateurCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var affectationUtilisateurCriteria = new AffectationUtilisateurCriteria();

        assertThat(affectationUtilisateurCriteria).hasToString("AffectationUtilisateurCriteria{}");
    }

    private static void setAllFilters(AffectationUtilisateurCriteria affectationUtilisateurCriteria) {
        affectationUtilisateurCriteria.id();
        affectationUtilisateurCriteria.dateDebut();
        affectationUtilisateurCriteria.dateFin();
        affectationUtilisateurCriteria.actif();
        affectationUtilisateurCriteria.userId();
        affectationUtilisateurCriteria.boutiqueId();
        affectationUtilisateurCriteria.profilId();
        affectationUtilisateurCriteria.distinct();
    }

    private static Condition<AffectationUtilisateurCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDateDebut()) &&
                condition.apply(criteria.getDateFin()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getProfilId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AffectationUtilisateurCriteria> copyFiltersAre(
        AffectationUtilisateurCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDateDebut(), copy.getDateDebut()) &&
                condition.apply(criteria.getDateFin(), copy.getDateFin()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getProfilId(), copy.getProfilId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
