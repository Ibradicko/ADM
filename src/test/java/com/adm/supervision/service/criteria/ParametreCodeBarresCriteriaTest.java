package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParametreCodeBarresCriteriaTest {

    @Test
    void newParametreCodeBarresCriteriaHasAllFiltersNullTest() {
        var parametreCodeBarresCriteria = new ParametreCodeBarresCriteria();
        assertThat(parametreCodeBarresCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void parametreCodeBarresCriteriaFluentMethodsCreatesFiltersTest() {
        var parametreCodeBarresCriteria = new ParametreCodeBarresCriteria();

        setAllFilters(parametreCodeBarresCriteria);

        assertThat(parametreCodeBarresCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void parametreCodeBarresCriteriaCopyCreatesNullFilterTest() {
        var parametreCodeBarresCriteria = new ParametreCodeBarresCriteria();
        var copy = parametreCodeBarresCriteria.copy();

        assertThat(parametreCodeBarresCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(parametreCodeBarresCriteria)
        );
    }

    @Test
    void parametreCodeBarresCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var parametreCodeBarresCriteria = new ParametreCodeBarresCriteria();
        setAllFilters(parametreCodeBarresCriteria);

        var copy = parametreCodeBarresCriteria.copy();

        assertThat(parametreCodeBarresCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(parametreCodeBarresCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var parametreCodeBarresCriteria = new ParametreCodeBarresCriteria();

        assertThat(parametreCodeBarresCriteria).hasToString("ParametreCodeBarresCriteria{}");
    }

    private static void setAllFilters(ParametreCodeBarresCriteria parametreCodeBarresCriteria) {
        parametreCodeBarresCriteria.id();
        parametreCodeBarresCriteria.formatParDefaut();
        parametreCodeBarresCriteria.prefixe();
        parametreCodeBarresCriteria.longueur();
        parametreCodeBarresCriteria.actif();
        parametreCodeBarresCriteria.distinct();
    }

    private static Condition<ParametreCodeBarresCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFormatParDefaut()) &&
                condition.apply(criteria.getPrefixe()) &&
                condition.apply(criteria.getLongueur()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParametreCodeBarresCriteria> copyFiltersAre(
        ParametreCodeBarresCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFormatParDefaut(), copy.getFormatParDefaut()) &&
                condition.apply(criteria.getPrefixe(), copy.getPrefixe()) &&
                condition.apply(criteria.getLongueur(), copy.getLongueur()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
