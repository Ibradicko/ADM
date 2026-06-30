package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LocataireCriteriaTest {

    @Test
    void newLocataireCriteriaHasAllFiltersNullTest() {
        var locataireCriteria = new LocataireCriteria();
        assertThat(locataireCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void locataireCriteriaFluentMethodsCreatesFiltersTest() {
        var locataireCriteria = new LocataireCriteria();

        setAllFilters(locataireCriteria);

        assertThat(locataireCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void locataireCriteriaCopyCreatesNullFilterTest() {
        var locataireCriteria = new LocataireCriteria();
        var copy = locataireCriteria.copy();

        assertThat(locataireCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(locataireCriteria)
        );
    }

    @Test
    void locataireCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var locataireCriteria = new LocataireCriteria();
        setAllFilters(locataireCriteria);

        var copy = locataireCriteria.copy();

        assertThat(locataireCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(locataireCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var locataireCriteria = new LocataireCriteria();

        assertThat(locataireCriteria).hasToString("LocataireCriteria{}");
    }

    private static void setAllFilters(LocataireCriteria locataireCriteria) {
        locataireCriteria.id();
        locataireCriteria.code();
        locataireCriteria.nom();
        locataireCriteria.typeLocataire();
        locataireCriteria.numeroIdentification();
        locataireCriteria.telephone();
        locataireCriteria.email();
        locataireCriteria.adresse();
        locataireCriteria.statut();
        locataireCriteria.dateCreation();
        locataireCriteria.distinct();
    }

    private static Condition<LocataireCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getTypeLocataire()) &&
                condition.apply(criteria.getNumeroIdentification()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getAdresse()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LocataireCriteria> copyFiltersAre(LocataireCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getTypeLocataire(), copy.getTypeLocataire()) &&
                condition.apply(criteria.getNumeroIdentification(), copy.getNumeroIdentification()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getAdresse(), copy.getAdresse()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
