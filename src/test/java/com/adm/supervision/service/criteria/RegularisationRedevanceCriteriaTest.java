package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RegularisationRedevanceCriteriaTest {

    @Test
    void newRegularisationRedevanceCriteriaHasAllFiltersNullTest() {
        var regularisationRedevanceCriteria = new RegularisationRedevanceCriteria();
        assertThat(regularisationRedevanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void regularisationRedevanceCriteriaFluentMethodsCreatesFiltersTest() {
        var regularisationRedevanceCriteria = new RegularisationRedevanceCriteria();

        setAllFilters(regularisationRedevanceCriteria);

        assertThat(regularisationRedevanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void regularisationRedevanceCriteriaCopyCreatesNullFilterTest() {
        var regularisationRedevanceCriteria = new RegularisationRedevanceCriteria();
        var copy = regularisationRedevanceCriteria.copy();

        assertThat(regularisationRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(regularisationRedevanceCriteria)
        );
    }

    @Test
    void regularisationRedevanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var regularisationRedevanceCriteria = new RegularisationRedevanceCriteria();
        setAllFilters(regularisationRedevanceCriteria);

        var copy = regularisationRedevanceCriteria.copy();

        assertThat(regularisationRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(regularisationRedevanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var regularisationRedevanceCriteria = new RegularisationRedevanceCriteria();

        assertThat(regularisationRedevanceCriteria).hasToString("RegularisationRedevanceCriteria{}");
    }

    private static void setAllFilters(RegularisationRedevanceCriteria regularisationRedevanceCriteria) {
        regularisationRedevanceCriteria.id();
        regularisationRedevanceCriteria.reference();
        regularisationRedevanceCriteria.montant();
        regularisationRedevanceCriteria.motif();
        regularisationRedevanceCriteria.dateRegularisation();
        regularisationRedevanceCriteria.calculId();
        regularisationRedevanceCriteria.distinct();
    }

    private static Condition<RegularisationRedevanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getMontant()) &&
                condition.apply(criteria.getMotif()) &&
                condition.apply(criteria.getDateRegularisation()) &&
                condition.apply(criteria.getCalculId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RegularisationRedevanceCriteria> copyFiltersAre(
        RegularisationRedevanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getMontant(), copy.getMontant()) &&
                condition.apply(criteria.getMotif(), copy.getMotif()) &&
                condition.apply(criteria.getDateRegularisation(), copy.getDateRegularisation()) &&
                condition.apply(criteria.getCalculId(), copy.getCalculId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
