package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StockProduitCriteriaTest {

    @Test
    void newStockProduitCriteriaHasAllFiltersNullTest() {
        var stockProduitCriteria = new StockProduitCriteria();
        assertThat(stockProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stockProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var stockProduitCriteria = new StockProduitCriteria();

        setAllFilters(stockProduitCriteria);

        assertThat(stockProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stockProduitCriteriaCopyCreatesNullFilterTest() {
        var stockProduitCriteria = new StockProduitCriteria();
        var copy = stockProduitCriteria.copy();

        assertThat(stockProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stockProduitCriteria)
        );
    }

    @Test
    void stockProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stockProduitCriteria = new StockProduitCriteria();
        setAllFilters(stockProduitCriteria);

        var copy = stockProduitCriteria.copy();

        assertThat(stockProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stockProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stockProduitCriteria = new StockProduitCriteria();

        assertThat(stockProduitCriteria).hasToString("StockProduitCriteria{}");
    }

    private static void setAllFilters(StockProduitCriteria stockProduitCriteria) {
        stockProduitCriteria.id();
        stockProduitCriteria.quantiteTheorique();
        stockProduitCriteria.stockAlerte();
        stockProduitCriteria.dateDernierMouvement();
        stockProduitCriteria.produitId();
        stockProduitCriteria.depotId();
        stockProduitCriteria.distinct();
    }

    private static Condition<StockProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantiteTheorique()) &&
                condition.apply(criteria.getStockAlerte()) &&
                condition.apply(criteria.getDateDernierMouvement()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDepotId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StockProduitCriteria> copyFiltersAre(
        StockProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantiteTheorique(), copy.getQuantiteTheorique()) &&
                condition.apply(criteria.getStockAlerte(), copy.getStockAlerte()) &&
                condition.apply(criteria.getDateDernierMouvement(), copy.getDateDernierMouvement()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDepotId(), copy.getDepotId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
