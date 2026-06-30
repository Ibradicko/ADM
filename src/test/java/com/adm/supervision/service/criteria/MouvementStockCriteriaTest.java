package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MouvementStockCriteriaTest {

    @Test
    void newMouvementStockCriteriaHasAllFiltersNullTest() {
        var mouvementStockCriteria = new MouvementStockCriteria();
        assertThat(mouvementStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void mouvementStockCriteriaFluentMethodsCreatesFiltersTest() {
        var mouvementStockCriteria = new MouvementStockCriteria();

        setAllFilters(mouvementStockCriteria);

        assertThat(mouvementStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void mouvementStockCriteriaCopyCreatesNullFilterTest() {
        var mouvementStockCriteria = new MouvementStockCriteria();
        var copy = mouvementStockCriteria.copy();

        assertThat(mouvementStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(mouvementStockCriteria)
        );
    }

    @Test
    void mouvementStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var mouvementStockCriteria = new MouvementStockCriteria();
        setAllFilters(mouvementStockCriteria);

        var copy = mouvementStockCriteria.copy();

        assertThat(mouvementStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(mouvementStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var mouvementStockCriteria = new MouvementStockCriteria();

        assertThat(mouvementStockCriteria).hasToString("MouvementStockCriteria{}");
    }

    private static void setAllFilters(MouvementStockCriteria mouvementStockCriteria) {
        mouvementStockCriteria.id();
        mouvementStockCriteria.reference();
        mouvementStockCriteria.typeMouvement();
        mouvementStockCriteria.statut();
        mouvementStockCriteria.dateMouvement();
        mouvementStockCriteria.motif();
        mouvementStockCriteria.boutiqueId();
        mouvementStockCriteria.utilisateurId();
        mouvementStockCriteria.distinct();
    }

    private static Condition<MouvementStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTypeMouvement()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateMouvement()) &&
                condition.apply(criteria.getMotif()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MouvementStockCriteria> copyFiltersAre(
        MouvementStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTypeMouvement(), copy.getTypeMouvement()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateMouvement(), copy.getDateMouvement()) &&
                condition.apply(criteria.getMotif(), copy.getMotif()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
