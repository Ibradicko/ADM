package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TransfertStockCriteriaTest {

    @Test
    void newTransfertStockCriteriaHasAllFiltersNullTest() {
        var transfertStockCriteria = new TransfertStockCriteria();
        assertThat(transfertStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void transfertStockCriteriaFluentMethodsCreatesFiltersTest() {
        var transfertStockCriteria = new TransfertStockCriteria();

        setAllFilters(transfertStockCriteria);

        assertThat(transfertStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void transfertStockCriteriaCopyCreatesNullFilterTest() {
        var transfertStockCriteria = new TransfertStockCriteria();
        var copy = transfertStockCriteria.copy();

        assertThat(transfertStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(transfertStockCriteria)
        );
    }

    @Test
    void transfertStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var transfertStockCriteria = new TransfertStockCriteria();
        setAllFilters(transfertStockCriteria);

        var copy = transfertStockCriteria.copy();

        assertThat(transfertStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(transfertStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var transfertStockCriteria = new TransfertStockCriteria();

        assertThat(transfertStockCriteria).hasToString("TransfertStockCriteria{}");
    }

    private static void setAllFilters(TransfertStockCriteria transfertStockCriteria) {
        transfertStockCriteria.id();
        transfertStockCriteria.reference();
        transfertStockCriteria.dateTransfert();
        transfertStockCriteria.statut();
        transfertStockCriteria.motif();
        transfertStockCriteria.boutiqueOrigineId();
        transfertStockCriteria.boutiqueDestinationId();
        transfertStockCriteria.utilisateurId();
        transfertStockCriteria.distinct();
    }

    private static Condition<TransfertStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getDateTransfert()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getMotif()) &&
                condition.apply(criteria.getBoutiqueOrigineId()) &&
                condition.apply(criteria.getBoutiqueDestinationId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TransfertStockCriteria> copyFiltersAre(
        TransfertStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getDateTransfert(), copy.getDateTransfert()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getMotif(), copy.getMotif()) &&
                condition.apply(criteria.getBoutiqueOrigineId(), copy.getBoutiqueOrigineId()) &&
                condition.apply(criteria.getBoutiqueDestinationId(), copy.getBoutiqueDestinationId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
