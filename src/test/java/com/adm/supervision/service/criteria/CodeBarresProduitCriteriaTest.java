package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CodeBarresProduitCriteriaTest {

    @Test
    void newCodeBarresProduitCriteriaHasAllFiltersNullTest() {
        var codeBarresProduitCriteria = new CodeBarresProduitCriteria();
        assertThat(codeBarresProduitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void codeBarresProduitCriteriaFluentMethodsCreatesFiltersTest() {
        var codeBarresProduitCriteria = new CodeBarresProduitCriteria();

        setAllFilters(codeBarresProduitCriteria);

        assertThat(codeBarresProduitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void codeBarresProduitCriteriaCopyCreatesNullFilterTest() {
        var codeBarresProduitCriteria = new CodeBarresProduitCriteria();
        var copy = codeBarresProduitCriteria.copy();

        assertThat(codeBarresProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(codeBarresProduitCriteria)
        );
    }

    @Test
    void codeBarresProduitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var codeBarresProduitCriteria = new CodeBarresProduitCriteria();
        setAllFilters(codeBarresProduitCriteria);

        var copy = codeBarresProduitCriteria.copy();

        assertThat(codeBarresProduitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(codeBarresProduitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var codeBarresProduitCriteria = new CodeBarresProduitCriteria();

        assertThat(codeBarresProduitCriteria).hasToString("CodeBarresProduitCriteria{}");
    }

    private static void setAllFilters(CodeBarresProduitCriteria codeBarresProduitCriteria) {
        codeBarresProduitCriteria.id();
        codeBarresProduitCriteria.code();
        codeBarresProduitCriteria.type();
        codeBarresProduitCriteria.principal();
        codeBarresProduitCriteria.genereParSysteme();
        codeBarresProduitCriteria.actif();
        codeBarresProduitCriteria.dateAffectation();
        codeBarresProduitCriteria.produitId();
        codeBarresProduitCriteria.distinct();
    }

    private static Condition<CodeBarresProduitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getPrincipal()) &&
                condition.apply(criteria.getGenereParSysteme()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getDateAffectation()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CodeBarresProduitCriteria> copyFiltersAre(
        CodeBarresProduitCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getPrincipal(), copy.getPrincipal()) &&
                condition.apply(criteria.getGenereParSysteme(), copy.getGenereParSysteme()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getDateAffectation(), copy.getDateAffectation()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
