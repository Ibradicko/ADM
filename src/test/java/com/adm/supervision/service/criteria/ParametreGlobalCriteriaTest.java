package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParametreGlobalCriteriaTest {

    @Test
    void newParametreGlobalCriteriaHasAllFiltersNullTest() {
        var parametreGlobalCriteria = new ParametreGlobalCriteria();
        assertThat(parametreGlobalCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void parametreGlobalCriteriaFluentMethodsCreatesFiltersTest() {
        var parametreGlobalCriteria = new ParametreGlobalCriteria();

        setAllFilters(parametreGlobalCriteria);

        assertThat(parametreGlobalCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void parametreGlobalCriteriaCopyCreatesNullFilterTest() {
        var parametreGlobalCriteria = new ParametreGlobalCriteria();
        var copy = parametreGlobalCriteria.copy();

        assertThat(parametreGlobalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(parametreGlobalCriteria)
        );
    }

    @Test
    void parametreGlobalCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var parametreGlobalCriteria = new ParametreGlobalCriteria();
        setAllFilters(parametreGlobalCriteria);

        var copy = parametreGlobalCriteria.copy();

        assertThat(parametreGlobalCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(parametreGlobalCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var parametreGlobalCriteria = new ParametreGlobalCriteria();

        assertThat(parametreGlobalCriteria).hasToString("ParametreGlobalCriteria{}");
    }

    private static void setAllFilters(ParametreGlobalCriteria parametreGlobalCriteria) {
        parametreGlobalCriteria.id();
        parametreGlobalCriteria.code();
        parametreGlobalCriteria.valeur();
        parametreGlobalCriteria.actif();
        parametreGlobalCriteria.distinct();
    }

    private static Condition<ParametreGlobalCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getValeur()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParametreGlobalCriteria> copyFiltersAre(
        ParametreGlobalCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getValeur(), copy.getValeur()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
