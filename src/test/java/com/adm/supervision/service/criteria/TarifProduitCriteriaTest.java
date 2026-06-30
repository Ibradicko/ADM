package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TarifProduitCriteriaTest {

    @Test
    void newTarifProduitCriteriaHasAllFiltersNullTest() {
        var tarifProduitCriteria = new TarifProduitCriteria();
        assertThat(tarifProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tarifProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var tarifProduitCriteria = new TarifProduitCriteria();

        setAllFilters(tarifProduitCriteria);

        assertThat(tarifProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tarifProduitCriteriaCopyCreatesNullFilterTest() {
        var tarifProduitCriteria = new TarifProduitCriteria();
        var copy = tarifProduitCriteria.copy();

        assertThat(tarifProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tarifProduitCriteria)
        );
    }

    @Test
    void tarifProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tarifProduitCriteria = new TarifProduitCriteria();
        setAllFilters(tarifProduitCriteria);

        var copy = tarifProduitCriteria.copy();

        assertThat(tarifProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tarifProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tarifProduitCriteria = new TarifProduitCriteria();

        assertThat(tarifProduitCriteria).hasToString("TarifProduitCriteria{}");
    }

    private static void setAllFilters(TarifProduitCriteria tarifProduitCriteria) {
        tarifProduitCriteria.id();
        tarifProduitCriteria.montant();
        tarifProduitCriteria.typePrix();
        tarifProduitCriteria.dateDebut();
        tarifProduitCriteria.dateFin();
        tarifProduitCriteria.actif();
        tarifProduitCriteria.produitId();
        tarifProduitCriteria.distinct();
    }

    private static Condition<TarifProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMontant()) &&
                condition.apply(criteria.getTypePrix()) &&
                condition.apply(criteria.getDateDebut()) &&
                condition.apply(criteria.getDateFin()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TarifProduitCriteria> copyFiltersAre(
        TarifProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMontant(), copy.getMontant()) &&
                condition.apply(criteria.getTypePrix(), copy.getTypePrix()) &&
                condition.apply(criteria.getDateDebut(), copy.getDateDebut()) &&
                condition.apply(criteria.getDateFin(), copy.getDateFin()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
