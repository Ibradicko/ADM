package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LotEtiquettesCriteriaTest {

    @Test
    void newLotEtiquettesCriteriaHasAllFiltersNullTest() {
        var lotEtiquettesCriteria = new LotEtiquettesCriteria();
        assertThat(lotEtiquettesCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void lotEtiquettesCriteriaFluentMethodsCreatesFiltersTest() {
        var lotEtiquettesCriteria = new LotEtiquettesCriteria();

        setAllFilters(lotEtiquettesCriteria);

        assertThat(lotEtiquettesCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void lotEtiquettesCriteriaCopyCreatesNullFilterTest() {
        var lotEtiquettesCriteria = new LotEtiquettesCriteria();
        var copy = lotEtiquettesCriteria.copy();

        assertThat(lotEtiquettesCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(lotEtiquettesCriteria)
        );
    }

    @Test
    void lotEtiquettesCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var lotEtiquettesCriteria = new LotEtiquettesCriteria();
        setAllFilters(lotEtiquettesCriteria);

        var copy = lotEtiquettesCriteria.copy();

        assertThat(lotEtiquettesCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(lotEtiquettesCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var lotEtiquettesCriteria = new LotEtiquettesCriteria();

        assertThat(lotEtiquettesCriteria).hasToString("LotEtiquettesCriteria{}");
    }

    private static void setAllFilters(LotEtiquettesCriteria lotEtiquettesCriteria) {
        lotEtiquettesCriteria.id();
        lotEtiquettesCriteria.reference();
        lotEtiquettesCriteria.dateGeneration();
        lotEtiquettesCriteria.formatImpression();
        lotEtiquettesCriteria.nombreEtiquettes();
        lotEtiquettesCriteria.distinct();
    }

    private static Condition<LotEtiquettesCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getDateGeneration()) &&
                condition.apply(criteria.getFormatImpression()) &&
                condition.apply(criteria.getNombreEtiquettes()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LotEtiquettesCriteria> copyFiltersAre(
        LotEtiquettesCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getDateGeneration(), copy.getDateGeneration()) &&
                condition.apply(criteria.getFormatImpression(), copy.getFormatImpression()) &&
                condition.apply(criteria.getNombreEtiquettes(), copy.getNombreEtiquettes()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
