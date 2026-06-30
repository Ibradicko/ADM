package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneInventaireStockCriteriaTest {

    @Test
    void newLigneInventaireStockCriteriaHasAllFiltersNullTest() {
        var ligneInventaireStockCriteria = new LigneInventaireStockCriteria();
        assertThat(ligneInventaireStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneInventaireStockCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneInventaireStockCriteria = new LigneInventaireStockCriteria();

        setAllFilters(ligneInventaireStockCriteria);

        assertThat(ligneInventaireStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneInventaireStockCriteriaCopyCreatesNullFilterTest() {
        var ligneInventaireStockCriteria = new LigneInventaireStockCriteria();
        var copy = ligneInventaireStockCriteria.copy();

        assertThat(ligneInventaireStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneInventaireStockCriteria)
        );
    }

    @Test
    void ligneInventaireStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneInventaireStockCriteria = new LigneInventaireStockCriteria();
        setAllFilters(ligneInventaireStockCriteria);

        var copy = ligneInventaireStockCriteria.copy();

        assertThat(ligneInventaireStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneInventaireStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneInventaireStockCriteria = new LigneInventaireStockCriteria();

        assertThat(ligneInventaireStockCriteria).hasToString("LigneInventaireStockCriteria{}");
    }

    private static void setAllFilters(LigneInventaireStockCriteria ligneInventaireStockCriteria) {
        ligneInventaireStockCriteria.id();
        ligneInventaireStockCriteria.quantiteTheorique();
        ligneInventaireStockCriteria.quantiteComptee();
        ligneInventaireStockCriteria.ecart();
        ligneInventaireStockCriteria.commentaire();
        ligneInventaireStockCriteria.inventaireId();
        ligneInventaireStockCriteria.produitId();
        ligneInventaireStockCriteria.distinct();
    }

    private static Condition<LigneInventaireStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantiteTheorique()) &&
                condition.apply(criteria.getQuantiteComptee()) &&
                condition.apply(criteria.getEcart()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getInventaireId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneInventaireStockCriteria> copyFiltersAre(
        LigneInventaireStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantiteTheorique(), copy.getQuantiteTheorique()) &&
                condition.apply(criteria.getQuantiteComptee(), copy.getQuantiteComptee()) &&
                condition.apply(criteria.getEcart(), copy.getEcart()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getInventaireId(), copy.getInventaireId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
