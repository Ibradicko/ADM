package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DepotStockCriteriaTest {

    @Test
    void newDepotStockCriteriaHasAllFiltersNullTest() {
        var depotStockCriteria = new DepotStockCriteria();
        assertThat(depotStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void depotStockCriteriaFluentMethodsCreatesFiltersTest() {
        var depotStockCriteria = new DepotStockCriteria();

        setAllFilters(depotStockCriteria);

        assertThat(depotStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void depotStockCriteriaCopyCreatesNullFilterTest() {
        var depotStockCriteria = new DepotStockCriteria();
        var copy = depotStockCriteria.copy();

        assertThat(depotStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(depotStockCriteria)
        );
    }

    @Test
    void depotStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var depotStockCriteria = new DepotStockCriteria();
        setAllFilters(depotStockCriteria);

        var copy = depotStockCriteria.copy();

        assertThat(depotStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(depotStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var depotStockCriteria = new DepotStockCriteria();

        assertThat(depotStockCriteria).hasToString("DepotStockCriteria{}");
    }

    private static void setAllFilters(DepotStockCriteria depotStockCriteria) {
        depotStockCriteria.id();
        depotStockCriteria.code();
        depotStockCriteria.libelle();
        depotStockCriteria.emplacement();
        depotStockCriteria.actif();
        depotStockCriteria.boutiqueId();
        depotStockCriteria.distinct();
    }

    private static Condition<DepotStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getEmplacement()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DepotStockCriteria> copyFiltersAre(DepotStockCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getEmplacement(), copy.getEmplacement()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
