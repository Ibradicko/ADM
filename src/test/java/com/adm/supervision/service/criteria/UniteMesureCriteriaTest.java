package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class UniteMesureCriteriaTest {

    @Test
    void newUniteMesureCriteriaHasAllFiltersNullTest() {
        var uniteMesureCriteria = new UniteMesureCriteria();
        assertThat(uniteMesureCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void uniteMesureCriteriaFluentMethodsCreatesFiltersTest() {
        var uniteMesureCriteria = new UniteMesureCriteria();

        setAllFilters(uniteMesureCriteria);

        assertThat(uniteMesureCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void uniteMesureCriteriaCopyCreatesNullFilterTest() {
        var uniteMesureCriteria = new UniteMesureCriteria();
        var copy = uniteMesureCriteria.copy();

        assertThat(uniteMesureCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(uniteMesureCriteria)
        );
    }

    @Test
    void uniteMesureCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var uniteMesureCriteria = new UniteMesureCriteria();
        setAllFilters(uniteMesureCriteria);

        var copy = uniteMesureCriteria.copy();

        assertThat(uniteMesureCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(uniteMesureCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var uniteMesureCriteria = new UniteMesureCriteria();

        assertThat(uniteMesureCriteria).hasToString("UniteMesureCriteria{}");
    }

    private static void setAllFilters(UniteMesureCriteria uniteMesureCriteria) {
        uniteMesureCriteria.id();
        uniteMesureCriteria.code();
        uniteMesureCriteria.libelle();
        uniteMesureCriteria.symbole();
        uniteMesureCriteria.distinct();
    }

    private static Condition<UniteMesureCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getSymbole()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<UniteMesureCriteria> copyFiltersAre(UniteMesureCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getSymbole(), copy.getSymbole()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
