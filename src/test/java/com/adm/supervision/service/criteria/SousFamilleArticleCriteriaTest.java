package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SousFamilleArticleCriteriaTest {

    @Test
    void newSousFamilleArticleCriteriaHasAllFiltersNullTest() {
        var sousFamilleArticleCriteria = new SousFamilleArticleCriteria();
        assertThat(sousFamilleArticleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void sousFamilleArticleCriteriaFluentMethodsCreatesFiltersTest() {
        var sousFamilleArticleCriteria = new SousFamilleArticleCriteria();

        setAllFilters(sousFamilleArticleCriteria);

        assertThat(sousFamilleArticleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void sousFamilleArticleCriteriaCopyCreatesNullFilterTest() {
        var sousFamilleArticleCriteria = new SousFamilleArticleCriteria();
        var copy = sousFamilleArticleCriteria.copy();

        assertThat(sousFamilleArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(sousFamilleArticleCriteria)
        );
    }

    @Test
    void sousFamilleArticleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var sousFamilleArticleCriteria = new SousFamilleArticleCriteria();
        setAllFilters(sousFamilleArticleCriteria);

        var copy = sousFamilleArticleCriteria.copy();

        assertThat(sousFamilleArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(sousFamilleArticleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var sousFamilleArticleCriteria = new SousFamilleArticleCriteria();

        assertThat(sousFamilleArticleCriteria).hasToString("SousFamilleArticleCriteria{}");
    }

    private static void setAllFilters(SousFamilleArticleCriteria sousFamilleArticleCriteria) {
        sousFamilleArticleCriteria.id();
        sousFamilleArticleCriteria.code();
        sousFamilleArticleCriteria.libelle();
        sousFamilleArticleCriteria.statut();
        sousFamilleArticleCriteria.familleArticleId();
        sousFamilleArticleCriteria.distinct();
    }

    private static Condition<SousFamilleArticleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getFamilleArticleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SousFamilleArticleCriteria> copyFiltersAre(
        SousFamilleArticleCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getFamilleArticleId(), copy.getFamilleArticleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
