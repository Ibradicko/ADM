package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneReceptionProduitCriteriaTest {

    @Test
    void newLigneReceptionProduitCriteriaHasAllFiltersNullTest() {
        var ligneReceptionProduitCriteria = new LigneReceptionProduitCriteria();
        assertThat(ligneReceptionProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneReceptionProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneReceptionProduitCriteria = new LigneReceptionProduitCriteria();

        setAllFilters(ligneReceptionProduitCriteria);

        assertThat(ligneReceptionProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneReceptionProduitCriteriaCopyCreatesNullFilterTest() {
        var ligneReceptionProduitCriteria = new LigneReceptionProduitCriteria();
        var copy = ligneReceptionProduitCriteria.copy();

        assertThat(ligneReceptionProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneReceptionProduitCriteria)
        );
    }

    @Test
    void ligneReceptionProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneReceptionProduitCriteria = new LigneReceptionProduitCriteria();
        setAllFilters(ligneReceptionProduitCriteria);

        var copy = ligneReceptionProduitCriteria.copy();

        assertThat(ligneReceptionProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneReceptionProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneReceptionProduitCriteria = new LigneReceptionProduitCriteria();

        assertThat(ligneReceptionProduitCriteria).hasToString("LigneReceptionProduitCriteria{}");
    }

    private static void setAllFilters(LigneReceptionProduitCriteria ligneReceptionProduitCriteria) {
        ligneReceptionProduitCriteria.id();
        ligneReceptionProduitCriteria.quantiteAttendue();
        ligneReceptionProduitCriteria.quantiteRecue();
        ligneReceptionProduitCriteria.ecart();
        ligneReceptionProduitCriteria.codeBarresScanne();
        ligneReceptionProduitCriteria.receptionId();
        ligneReceptionProduitCriteria.produitId();
        ligneReceptionProduitCriteria.distinct();
    }

    private static Condition<LigneReceptionProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantiteAttendue()) &&
                condition.apply(criteria.getQuantiteRecue()) &&
                condition.apply(criteria.getEcart()) &&
                condition.apply(criteria.getCodeBarresScanne()) &&
                condition.apply(criteria.getReceptionId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneReceptionProduitCriteria> copyFiltersAre(
        LigneReceptionProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantiteAttendue(), copy.getQuantiteAttendue()) &&
                condition.apply(criteria.getQuantiteRecue(), copy.getQuantiteRecue()) &&
                condition.apply(criteria.getEcart(), copy.getEcart()) &&
                condition.apply(criteria.getCodeBarresScanne(), copy.getCodeBarresScanne()) &&
                condition.apply(criteria.getReceptionId(), copy.getReceptionId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
