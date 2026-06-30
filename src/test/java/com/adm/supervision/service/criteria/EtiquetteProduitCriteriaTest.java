package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EtiquetteProduitCriteriaTest {

    @Test
    void newEtiquetteProduitCriteriaHasAllFiltersNullTest() {
        var etiquetteProduitCriteria = new EtiquetteProduitCriteria();
        assertThat(etiquetteProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void etiquetteProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var etiquetteProduitCriteria = new EtiquetteProduitCriteria();

        setAllFilters(etiquetteProduitCriteria);

        assertThat(etiquetteProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void etiquetteProduitCriteriaCopyCreatesNullFilterTest() {
        var etiquetteProduitCriteria = new EtiquetteProduitCriteria();
        var copy = etiquetteProduitCriteria.copy();

        assertThat(etiquetteProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(etiquetteProduitCriteria)
        );
    }

    @Test
    void etiquetteProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var etiquetteProduitCriteria = new EtiquetteProduitCriteria();
        setAllFilters(etiquetteProduitCriteria);

        var copy = etiquetteProduitCriteria.copy();

        assertThat(etiquetteProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(etiquetteProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var etiquetteProduitCriteria = new EtiquetteProduitCriteria();

        assertThat(etiquetteProduitCriteria).hasToString("EtiquetteProduitCriteria{}");
    }

    private static void setAllFilters(EtiquetteProduitCriteria etiquetteProduitCriteria) {
        etiquetteProduitCriteria.id();
        etiquetteProduitCriteria.quantite();
        etiquetteProduitCriteria.imprimee();
        etiquetteProduitCriteria.dateImpression();
        etiquetteProduitCriteria.produitId();
        etiquetteProduitCriteria.lotId();
        etiquetteProduitCriteria.distinct();
    }

    private static Condition<EtiquetteProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantite()) &&
                condition.apply(criteria.getImprimee()) &&
                condition.apply(criteria.getDateImpression()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getLotId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EtiquetteProduitCriteria> copyFiltersAre(
        EtiquetteProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantite(), copy.getQuantite()) &&
                condition.apply(criteria.getImprimee(), copy.getImprimee()) &&
                condition.apply(criteria.getDateImpression(), copy.getDateImpression()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getLotId(), copy.getLotId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
