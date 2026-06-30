package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaiementVenteCriteriaTest {

    @Test
    void newPaiementVenteCriteriaHasAllFiltersNullTest() {
        var paiementVenteCriteria = new PaiementVenteCriteria();
        assertThat(paiementVenteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void paiementVenteCriteriaFluentMethodsCreatesFiltersTest() {
        var paiementVenteCriteria = new PaiementVenteCriteria();

        setAllFilters(paiementVenteCriteria);

        assertThat(paiementVenteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void paiementVenteCriteriaCopyCreatesNullFilterTest() {
        var paiementVenteCriteria = new PaiementVenteCriteria();
        var copy = paiementVenteCriteria.copy();

        assertThat(paiementVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementVenteCriteria)
        );
    }

    @Test
    void paiementVenteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paiementVenteCriteria = new PaiementVenteCriteria();
        setAllFilters(paiementVenteCriteria);

        var copy = paiementVenteCriteria.copy();

        assertThat(paiementVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementVenteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paiementVenteCriteria = new PaiementVenteCriteria();

        assertThat(paiementVenteCriteria).hasToString("PaiementVenteCriteria{}");
    }

    private static void setAllFilters(PaiementVenteCriteria paiementVenteCriteria) {
        paiementVenteCriteria.id();
        paiementVenteCriteria.montant();
        paiementVenteCriteria.statut();
        paiementVenteCriteria.referencePaiement();
        paiementVenteCriteria.datePaiement();
        paiementVenteCriteria.venteId();
        paiementVenteCriteria.modePaiementId();
        paiementVenteCriteria.distinct();
    }

    private static Condition<PaiementVenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMontant()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getReferencePaiement()) &&
                condition.apply(criteria.getDatePaiement()) &&
                condition.apply(criteria.getVenteId()) &&
                condition.apply(criteria.getModePaiementId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaiementVenteCriteria> copyFiltersAre(
        PaiementVenteCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMontant(), copy.getMontant()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getReferencePaiement(), copy.getReferencePaiement()) &&
                condition.apply(criteria.getDatePaiement(), copy.getDatePaiement()) &&
                condition.apply(criteria.getVenteId(), copy.getVenteId()) &&
                condition.apply(criteria.getModePaiementId(), copy.getModePaiementId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
