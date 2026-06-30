package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneMouvementStockCriteriaTest {

    @Test
    void newLigneMouvementStockCriteriaHasAllFiltersNullTest() {
        var ligneMouvementStockCriteria = new LigneMouvementStockCriteria();
        assertThat(ligneMouvementStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneMouvementStockCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneMouvementStockCriteria = new LigneMouvementStockCriteria();

        setAllFilters(ligneMouvementStockCriteria);

        assertThat(ligneMouvementStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneMouvementStockCriteriaCopyCreatesNullFilterTest() {
        var ligneMouvementStockCriteria = new LigneMouvementStockCriteria();
        var copy = ligneMouvementStockCriteria.copy();

        assertThat(ligneMouvementStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneMouvementStockCriteria)
        );
    }

    @Test
    void ligneMouvementStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneMouvementStockCriteria = new LigneMouvementStockCriteria();
        setAllFilters(ligneMouvementStockCriteria);

        var copy = ligneMouvementStockCriteria.copy();

        assertThat(ligneMouvementStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneMouvementStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneMouvementStockCriteria = new LigneMouvementStockCriteria();

        assertThat(ligneMouvementStockCriteria).hasToString("LigneMouvementStockCriteria{}");
    }

    private static void setAllFilters(LigneMouvementStockCriteria ligneMouvementStockCriteria) {
        ligneMouvementStockCriteria.id();
        ligneMouvementStockCriteria.quantite();
        ligneMouvementStockCriteria.stockAvant();
        ligneMouvementStockCriteria.stockApres();
        ligneMouvementStockCriteria.commentaire();
        ligneMouvementStockCriteria.mouvementId();
        ligneMouvementStockCriteria.produitId();
        ligneMouvementStockCriteria.depotId();
        ligneMouvementStockCriteria.distinct();
    }

    private static Condition<LigneMouvementStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantite()) &&
                condition.apply(criteria.getStockAvant()) &&
                condition.apply(criteria.getStockApres()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getMouvementId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDepotId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneMouvementStockCriteria> copyFiltersAre(
        LigneMouvementStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantite(), copy.getQuantite()) &&
                condition.apply(criteria.getStockAvant(), copy.getStockAvant()) &&
                condition.apply(criteria.getStockApres(), copy.getStockApres()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getMouvementId(), copy.getMouvementId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDepotId(), copy.getDepotId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
