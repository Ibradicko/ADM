package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProfilMetierCriteriaTest {

    @Test
    void newProfilMetierCriteriaHasAllFiltersNullTest() {
        var profilMetierCriteria = new ProfilMetierCriteria();
        assertThat(profilMetierCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void profilMetierCriteriaFluentMethodsCreatesFiltersTest() {
        var profilMetierCriteria = new ProfilMetierCriteria();

        setAllFilters(profilMetierCriteria);

        assertThat(profilMetierCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void profilMetierCriteriaCopyCreatesNullFilterTest() {
        var profilMetierCriteria = new ProfilMetierCriteria();
        var copy = profilMetierCriteria.copy();

        assertThat(profilMetierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(profilMetierCriteria)
        );
    }

    @Test
    void profilMetierCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var profilMetierCriteria = new ProfilMetierCriteria();
        setAllFilters(profilMetierCriteria);

        var copy = profilMetierCriteria.copy();

        assertThat(profilMetierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(profilMetierCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var profilMetierCriteria = new ProfilMetierCriteria();

        assertThat(profilMetierCriteria).hasToString("ProfilMetierCriteria{}");
    }

    private static void setAllFilters(ProfilMetierCriteria profilMetierCriteria) {
        profilMetierCriteria.id();
        profilMetierCriteria.code();
        profilMetierCriteria.libelle();
        profilMetierCriteria.statut();
        profilMetierCriteria.permissionsId();
        profilMetierCriteria.distinct();
    }

    private static Condition<ProfilMetierCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getPermissionsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProfilMetierCriteria> copyFiltersAre(
        ProfilMetierCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getPermissionsId(), copy.getPermissionsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
