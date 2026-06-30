package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class GroupeArticleCriteriaTest {

    @Test
    void newGroupeArticleCriteriaHasAllFiltersNullTest() {
        var groupeArticleCriteria = new GroupeArticleCriteria();
        assertThat(groupeArticleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void groupeArticleCriteriaFluentMethodsCreatesFiltersTest() {
        var groupeArticleCriteria = new GroupeArticleCriteria();

        setAllFilters(groupeArticleCriteria);

        assertThat(groupeArticleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void groupeArticleCriteriaCopyCreatesNullFilterTest() {
        var groupeArticleCriteria = new GroupeArticleCriteria();
        var copy = groupeArticleCriteria.copy();

        assertThat(groupeArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(groupeArticleCriteria)
        );
    }

    @Test
    void groupeArticleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var groupeArticleCriteria = new GroupeArticleCriteria();
        setAllFilters(groupeArticleCriteria);

        var copy = groupeArticleCriteria.copy();

        assertThat(groupeArticleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(groupeArticleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var groupeArticleCriteria = new GroupeArticleCriteria();

        assertThat(groupeArticleCriteria).hasToString("GroupeArticleCriteria{}");
    }

    private static void setAllFilters(GroupeArticleCriteria groupeArticleCriteria) {
        groupeArticleCriteria.id();
        groupeArticleCriteria.code();
        groupeArticleCriteria.libelle();
        groupeArticleCriteria.statut();
        groupeArticleCriteria.boutiqueId();
        groupeArticleCriteria.distinct();
    }

    private static Condition<GroupeArticleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<GroupeArticleCriteria> copyFiltersAre(
        GroupeArticleCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
