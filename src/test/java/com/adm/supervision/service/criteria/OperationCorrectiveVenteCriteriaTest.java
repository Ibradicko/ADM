package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OperationCorrectiveVenteCriteriaTest {

    @Test
    void newOperationCorrectiveVenteCriteriaHasAllFiltersNullTest() {
        var operationCorrectiveVenteCriteria = new OperationCorrectiveVenteCriteria();
        assertThat(operationCorrectiveVenteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void operationCorrectiveVenteCriteriaFluentMethodsCreatesFiltersTest() {
        var operationCorrectiveVenteCriteria = new OperationCorrectiveVenteCriteria();

        setAllFilters(operationCorrectiveVenteCriteria);

        assertThat(operationCorrectiveVenteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void operationCorrectiveVenteCriteriaCopyCreatesNullFilterTest() {
        var operationCorrectiveVenteCriteria = new OperationCorrectiveVenteCriteria();
        var copy = operationCorrectiveVenteCriteria.copy();

        assertThat(operationCorrectiveVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(operationCorrectiveVenteCriteria)
        );
    }

    @Test
    void operationCorrectiveVenteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var operationCorrectiveVenteCriteria = new OperationCorrectiveVenteCriteria();
        setAllFilters(operationCorrectiveVenteCriteria);

        var copy = operationCorrectiveVenteCriteria.copy();

        assertThat(operationCorrectiveVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(operationCorrectiveVenteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var operationCorrectiveVenteCriteria = new OperationCorrectiveVenteCriteria();

        assertThat(operationCorrectiveVenteCriteria).hasToString("OperationCorrectiveVenteCriteria{}");
    }

    private static void setAllFilters(OperationCorrectiveVenteCriteria operationCorrectiveVenteCriteria) {
        operationCorrectiveVenteCriteria.id();
        operationCorrectiveVenteCriteria.typeOperation();
        operationCorrectiveVenteCriteria.motif();
        operationCorrectiveVenteCriteria.montantImpact();
        operationCorrectiveVenteCriteria.dateOperation();
        operationCorrectiveVenteCriteria.venteId();
        operationCorrectiveVenteCriteria.utilisateurId();
        operationCorrectiveVenteCriteria.distinct();
    }

    private static Condition<OperationCorrectiveVenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTypeOperation()) &&
                condition.apply(criteria.getMotif()) &&
                condition.apply(criteria.getMontantImpact()) &&
                condition.apply(criteria.getDateOperation()) &&
                condition.apply(criteria.getVenteId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OperationCorrectiveVenteCriteria> copyFiltersAre(
        OperationCorrectiveVenteCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTypeOperation(), copy.getTypeOperation()) &&
                condition.apply(criteria.getMotif(), copy.getMotif()) &&
                condition.apply(criteria.getMontantImpact(), copy.getMontantImpact()) &&
                condition.apply(criteria.getDateOperation(), copy.getDateOperation()) &&
                condition.apply(criteria.getVenteId(), copy.getVenteId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
