package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BoutiqueCriteriaTest {

    @Test
    void newBoutiqueCriteriaHasAllFiltersNullTest() {
        var boutiqueCriteria = new BoutiqueCriteria();
        assertThat(boutiqueCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void boutiqueCriteriaFluentMethodsCreatesFiltersTest() {
        var boutiqueCriteria = new BoutiqueCriteria();

        setAllFilters(boutiqueCriteria);

        assertThat(boutiqueCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void boutiqueCriteriaCopyCreatesNullFilterTest() {
        var boutiqueCriteria = new BoutiqueCriteria();
        var copy = boutiqueCriteria.copy();

        assertThat(boutiqueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(boutiqueCriteria)
        );
    }

    @Test
    void boutiqueCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var boutiqueCriteria = new BoutiqueCriteria();
        setAllFilters(boutiqueCriteria);

        var copy = boutiqueCriteria.copy();

        assertThat(boutiqueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(boutiqueCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var boutiqueCriteria = new BoutiqueCriteria();

        assertThat(boutiqueCriteria).hasToString("BoutiqueCriteria{}");
    }

    private static void setAllFilters(BoutiqueCriteria boutiqueCriteria) {
        boutiqueCriteria.id();
        boutiqueCriteria.code();
        boutiqueCriteria.nom();
        boutiqueCriteria.type();
        boutiqueCriteria.emplacement();
        boutiqueCriteria.telephone();
        boutiqueCriteria.statut();
        boutiqueCriteria.dateCreation();
        boutiqueCriteria.distinct();
    }

    private static Condition<BoutiqueCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getEmplacement()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BoutiqueCriteria> copyFiltersAre(BoutiqueCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getEmplacement(), copy.getEmplacement()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
