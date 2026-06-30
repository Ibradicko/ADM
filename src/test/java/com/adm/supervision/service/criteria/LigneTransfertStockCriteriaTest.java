package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneTransfertStockCriteriaTest {

    @Test
    void newLigneTransfertStockCriteriaHasAllFiltersNullTest() {
        var ligneTransfertStockCriteria = new LigneTransfertStockCriteria();
        assertThat(ligneTransfertStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneTransfertStockCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneTransfertStockCriteria = new LigneTransfertStockCriteria();

        setAllFilters(ligneTransfertStockCriteria);

        assertThat(ligneTransfertStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneTransfertStockCriteriaCopyCreatesNullFilterTest() {
        var ligneTransfertStockCriteria = new LigneTransfertStockCriteria();
        var copy = ligneTransfertStockCriteria.copy();

        assertThat(ligneTransfertStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneTransfertStockCriteria)
        );
    }

    @Test
    void ligneTransfertStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneTransfertStockCriteria = new LigneTransfertStockCriteria();
        setAllFilters(ligneTransfertStockCriteria);

        var copy = ligneTransfertStockCriteria.copy();

        assertThat(ligneTransfertStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneTransfertStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneTransfertStockCriteria = new LigneTransfertStockCriteria();

        assertThat(ligneTransfertStockCriteria).hasToString("LigneTransfertStockCriteria{}");
    }

    private static void setAllFilters(LigneTransfertStockCriteria ligneTransfertStockCriteria) {
        ligneTransfertStockCriteria.id();
        ligneTransfertStockCriteria.quantite();
        ligneTransfertStockCriteria.commentaire();
        ligneTransfertStockCriteria.transfertId();
        ligneTransfertStockCriteria.produitId();
        ligneTransfertStockCriteria.distinct();
    }

    private static Condition<LigneTransfertStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantite()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getTransfertId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneTransfertStockCriteria> copyFiltersAre(
        LigneTransfertStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantite(), copy.getQuantite()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getTransfertId(), copy.getTransfertId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
