package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneCalculRedevanceCriteriaTest {

    @Test
    void newLigneCalculRedevanceCriteriaHasAllFiltersNullTest() {
        var ligneCalculRedevanceCriteria = new LigneCalculRedevanceCriteria();
        assertThat(ligneCalculRedevanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneCalculRedevanceCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneCalculRedevanceCriteria = new LigneCalculRedevanceCriteria();

        setAllFilters(ligneCalculRedevanceCriteria);

        assertThat(ligneCalculRedevanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneCalculRedevanceCriteriaCopyCreatesNullFilterTest() {
        var ligneCalculRedevanceCriteria = new LigneCalculRedevanceCriteria();
        var copy = ligneCalculRedevanceCriteria.copy();

        assertThat(ligneCalculRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneCalculRedevanceCriteria)
        );
    }

    @Test
    void ligneCalculRedevanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneCalculRedevanceCriteria = new LigneCalculRedevanceCriteria();
        setAllFilters(ligneCalculRedevanceCriteria);

        var copy = ligneCalculRedevanceCriteria.copy();

        assertThat(ligneCalculRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneCalculRedevanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneCalculRedevanceCriteria = new LigneCalculRedevanceCriteria();

        assertThat(ligneCalculRedevanceCriteria).hasToString("LigneCalculRedevanceCriteria{}");
    }

    private static void setAllFilters(LigneCalculRedevanceCriteria ligneCalculRedevanceCriteria) {
        ligneCalculRedevanceCriteria.id();
        ligneCalculRedevanceCriteria.baseCalcul();
        ligneCalculRedevanceCriteria.tauxApplique();
        ligneCalculRedevanceCriteria.montantRedevance();
        ligneCalculRedevanceCriteria.calculId();
        ligneCalculRedevanceCriteria.venteId();
        ligneCalculRedevanceCriteria.distinct();
    }

    private static Condition<LigneCalculRedevanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBaseCalcul()) &&
                condition.apply(criteria.getTauxApplique()) &&
                condition.apply(criteria.getMontantRedevance()) &&
                condition.apply(criteria.getCalculId()) &&
                condition.apply(criteria.getVenteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneCalculRedevanceCriteria> copyFiltersAre(
        LigneCalculRedevanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBaseCalcul(), copy.getBaseCalcul()) &&
                condition.apply(criteria.getTauxApplique(), copy.getTauxApplique()) &&
                condition.apply(criteria.getMontantRedevance(), copy.getMontantRedevance()) &&
                condition.apply(criteria.getCalculId(), copy.getCalculId()) &&
                condition.apply(criteria.getVenteId(), copy.getVenteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
