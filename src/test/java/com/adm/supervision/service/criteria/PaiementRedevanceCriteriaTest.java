package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaiementRedevanceCriteriaTest {

    @Test
    void newPaiementRedevanceCriteriaHasAllFiltersNullTest() {
        var paiementRedevanceCriteria = new PaiementRedevanceCriteria();
        assertThat(paiementRedevanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void paiementRedevanceCriteriaFluentMethodsCreatesFiltersTest() {
        var paiementRedevanceCriteria = new PaiementRedevanceCriteria();

        setAllFilters(paiementRedevanceCriteria);

        assertThat(paiementRedevanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void paiementRedevanceCriteriaCopyCreatesNullFilterTest() {
        var paiementRedevanceCriteria = new PaiementRedevanceCriteria();
        var copy = paiementRedevanceCriteria.copy();

        assertThat(paiementRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementRedevanceCriteria)
        );
    }

    @Test
    void paiementRedevanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paiementRedevanceCriteria = new PaiementRedevanceCriteria();
        setAllFilters(paiementRedevanceCriteria);

        var copy = paiementRedevanceCriteria.copy();

        assertThat(paiementRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(paiementRedevanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paiementRedevanceCriteria = new PaiementRedevanceCriteria();

        assertThat(paiementRedevanceCriteria).hasToString("PaiementRedevanceCriteria{}");
    }

    private static void setAllFilters(PaiementRedevanceCriteria paiementRedevanceCriteria) {
        paiementRedevanceCriteria.id();
        paiementRedevanceCriteria.reference();
        paiementRedevanceCriteria.montant();
        paiementRedevanceCriteria.datePaiement();
        paiementRedevanceCriteria.modePaiement();
        paiementRedevanceCriteria.commentaire();
        paiementRedevanceCriteria.calculId();
        paiementRedevanceCriteria.distinct();
    }

    private static Condition<PaiementRedevanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getMontant()) &&
                condition.apply(criteria.getDatePaiement()) &&
                condition.apply(criteria.getModePaiement()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getCalculId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaiementRedevanceCriteria> copyFiltersAre(
        PaiementRedevanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getMontant(), copy.getMontant()) &&
                condition.apply(criteria.getDatePaiement(), copy.getDatePaiement()) &&
                condition.apply(criteria.getModePaiement(), copy.getModePaiement()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getCalculId(), copy.getCalculId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
