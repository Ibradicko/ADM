package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ModePaiementRefCriteriaTest {

    @Test
    void newModePaiementRefCriteriaHasAllFiltersNullTest() {
        var modePaiementRefCriteria = new ModePaiementRefCriteria();
        assertThat(modePaiementRefCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void modePaiementRefCriteriaFluentMethodsCreatesFiltersTest() {
        var modePaiementRefCriteria = new ModePaiementRefCriteria();

        setAllFilters(modePaiementRefCriteria);

        assertThat(modePaiementRefCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void modePaiementRefCriteriaCopyCreatesNullFilterTest() {
        var modePaiementRefCriteria = new ModePaiementRefCriteria();
        var copy = modePaiementRefCriteria.copy();

        assertThat(modePaiementRefCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(modePaiementRefCriteria)
        );
    }

    @Test
    void modePaiementRefCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var modePaiementRefCriteria = new ModePaiementRefCriteria();
        setAllFilters(modePaiementRefCriteria);

        var copy = modePaiementRefCriteria.copy();

        assertThat(modePaiementRefCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(modePaiementRefCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var modePaiementRefCriteria = new ModePaiementRefCriteria();

        assertThat(modePaiementRefCriteria).hasToString("ModePaiementRefCriteria{}");
    }

    private static void setAllFilters(ModePaiementRefCriteria modePaiementRefCriteria) {
        modePaiementRefCriteria.id();
        modePaiementRefCriteria.code();
        modePaiementRefCriteria.libelle();
        modePaiementRefCriteria.actif();
        modePaiementRefCriteria.distinct();
    }

    private static Condition<ModePaiementRefCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ModePaiementRefCriteria> copyFiltersAre(
        ModePaiementRefCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
