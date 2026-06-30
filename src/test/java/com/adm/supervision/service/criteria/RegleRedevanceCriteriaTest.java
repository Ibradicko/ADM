package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RegleRedevanceCriteriaTest {

    @Test
    void newRegleRedevanceCriteriaHasAllFiltersNullTest() {
        var regleRedevanceCriteria = new RegleRedevanceCriteria();
        assertThat(regleRedevanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void regleRedevanceCriteriaFluentMethodsCreatesFiltersTest() {
        var regleRedevanceCriteria = new RegleRedevanceCriteria();

        setAllFilters(regleRedevanceCriteria);

        assertThat(regleRedevanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void regleRedevanceCriteriaCopyCreatesNullFilterTest() {
        var regleRedevanceCriteria = new RegleRedevanceCriteria();
        var copy = regleRedevanceCriteria.copy();

        assertThat(regleRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(regleRedevanceCriteria)
        );
    }

    @Test
    void regleRedevanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var regleRedevanceCriteria = new RegleRedevanceCriteria();
        setAllFilters(regleRedevanceCriteria);

        var copy = regleRedevanceCriteria.copy();

        assertThat(regleRedevanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(regleRedevanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var regleRedevanceCriteria = new RegleRedevanceCriteria();

        assertThat(regleRedevanceCriteria).hasToString("RegleRedevanceCriteria{}");
    }

    private static void setAllFilters(RegleRedevanceCriteria regleRedevanceCriteria) {
        regleRedevanceCriteria.id();
        regleRedevanceCriteria.code();
        regleRedevanceCriteria.typeRegle();
        regleRedevanceCriteria.taux();
        regleRedevanceCriteria.dateDebut();
        regleRedevanceCriteria.dateFin();
        regleRedevanceCriteria.priorite();
        regleRedevanceCriteria.actif();
        regleRedevanceCriteria.boutiqueId();
        regleRedevanceCriteria.locataireId();
        regleRedevanceCriteria.groupeArticleId();
        regleRedevanceCriteria.produitId();
        regleRedevanceCriteria.distinct();
    }

    private static Condition<RegleRedevanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getTypeRegle()) &&
                condition.apply(criteria.getTaux()) &&
                condition.apply(criteria.getDateDebut()) &&
                condition.apply(criteria.getDateFin()) &&
                condition.apply(criteria.getPriorite()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId()) &&
                condition.apply(criteria.getGroupeArticleId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RegleRedevanceCriteria> copyFiltersAre(
        RegleRedevanceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getTypeRegle(), copy.getTypeRegle()) &&
                condition.apply(criteria.getTaux(), copy.getTaux()) &&
                condition.apply(criteria.getDateDebut(), copy.getDateDebut()) &&
                condition.apply(criteria.getDateFin(), copy.getDateFin()) &&
                condition.apply(criteria.getPriorite(), copy.getPriorite()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId(), copy.getLocataireId()) &&
                condition.apply(criteria.getGroupeArticleId(), copy.getGroupeArticleId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
