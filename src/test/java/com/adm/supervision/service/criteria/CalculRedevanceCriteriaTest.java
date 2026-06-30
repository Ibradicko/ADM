package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CalculRedevanceCriteriaTest {

    @Test
    void newCalculRedevanceCriteriaHasAllFiltersNullTest() {
        var calculRedevanceCriteria = new CalculRedevanceCriteria();
        assertThat(calculRedevanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void calculRedevanceCriteriaFluentMethodsCreatesFiltersTest() {
        var calculRedevanceCriteria = new CalculRedevanceCriteria();

        setAllFilters(calculRedevanceCriteria);

        assertThat(calculRedevanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void calculRedevanceCriteriaCopyCreatesNullFilterTest() {
        var calculRedevanceCriteria = new CalculRedevanceCriteria();
        var copy = calculRedevanceCriteria.copy();

        assertThat(calculRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(calculRedevanceCriteria)
        );
    }

    @Test
    void calculRedevanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var calculRedevanceCriteria = new CalculRedevanceCriteria();
        setAllFilters(calculRedevanceCriteria);

        var copy = calculRedevanceCriteria.copy();

        assertThat(calculRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(calculRedevanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var calculRedevanceCriteria = new CalculRedevanceCriteria();

        assertThat(calculRedevanceCriteria).hasToString("CalculRedevanceCriteria{}");
    }

    private static void setAllFilters(CalculRedevanceCriteria calculRedevanceCriteria) {
        calculRedevanceCriteria.id();
        calculRedevanceCriteria.reference();
        calculRedevanceCriteria.periodeDebut();
        calculRedevanceCriteria.periodeFin();
        calculRedevanceCriteria.chiffreAffaires();
        calculRedevanceCriteria.montantRedevance();
        calculRedevanceCriteria.statut();
        calculRedevanceCriteria.dateCalcul();
        calculRedevanceCriteria.boutiqueId();
        calculRedevanceCriteria.locataireId();
        calculRedevanceCriteria.distinct();
    }

    private static Condition<CalculRedevanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getPeriodeDebut()) &&
                condition.apply(criteria.getPeriodeFin()) &&
                condition.apply(criteria.getChiffreAffaires()) &&
                condition.apply(criteria.getMontantRedevance()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCalcul()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CalculRedevanceCriteria> copyFiltersAre(
        CalculRedevanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getPeriodeDebut(), copy.getPeriodeDebut()) &&
                condition.apply(criteria.getPeriodeFin(), copy.getPeriodeFin()) &&
                condition.apply(criteria.getChiffreAffaires(), copy.getChiffreAffaires()) &&
                condition.apply(criteria.getMontantRedevance(), copy.getMontantRedevance()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCalcul(), copy.getDateCalcul()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId(), copy.getLocataireId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
