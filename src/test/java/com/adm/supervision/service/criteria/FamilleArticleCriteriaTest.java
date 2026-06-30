package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class FamilleArticleCriteriaTest {

    @Test
    void newFamilleArticleCriteriaHasAllFiltersNullTest() {
        var familleArticleCriteria = new FamilleArticleCriteria();
        assertThat(familleArticleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void familleArticleCriteriaFluentMethodsCreatesFiltersTest() {
        var familleArticleCriteria = new FamilleArticleCriteria();

        setAllFilters(familleArticleCriteria);

        assertThat(familleArticleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void familleArticleCriteriaCopyCreatesNullFilterTest() {
        var familleArticleCriteria = new FamilleArticleCriteria();
        var copy = familleArticleCriteria.copy();

        assertThat(familleArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(familleArticleCriteria)
        );
    }

    @Test
    void familleArticleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var familleArticleCriteria = new FamilleArticleCriteria();
        setAllFilters(familleArticleCriteria);

        var copy = familleArticleCriteria.copy();

        assertThat(familleArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(familleArticleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var familleArticleCriteria = new FamilleArticleCriteria();

        assertThat(familleArticleCriteria).hasToString("FamilleArticleCriteria{}");
    }

    private static void setAllFilters(FamilleArticleCriteria familleArticleCriteria) {
        familleArticleCriteria.id();
        familleArticleCriteria.code();
        familleArticleCriteria.libelle();
        familleArticleCriteria.statut();
        familleArticleCriteria.groupeArticleId();
        familleArticleCriteria.distinct();
    }

    private static Condition<FamilleArticleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getGroupeArticleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<FamilleArticleCriteria> copyFiltersAre(
        FamilleArticleCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getGroupeArticleId(), copy.getGroupeArticleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
