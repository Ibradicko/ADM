package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PermissionMetierCriteriaTest {

    @Test
    void newPermissionMetierCriteriaHasAllFiltersNullTest() {
        var permissionMetierCriteria = new PermissionMetierCriteria();
        assertThat(permissionMetierCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void permissionMetierCriteriaFluentMethodsCreatesFiltersTest() {
        var permissionMetierCriteria = new PermissionMetierCriteria();

        setAllFilters(permissionMetierCriteria);

        assertThat(permissionMetierCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void permissionMetierCriteriaCopyCreatesNullFilterTest() {
        var permissionMetierCriteria = new PermissionMetierCriteria();
        var copy = permissionMetierCriteria.copy();

        assertThat(permissionMetierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(permissionMetierCriteria)
        );
    }

    @Test
    void permissionMetierCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var permissionMetierCriteria = new PermissionMetierCriteria();
        setAllFilters(permissionMetierCriteria);

        var copy = permissionMetierCriteria.copy();

        assertThat(permissionMetierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(permissionMetierCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var permissionMetierCriteria = new PermissionMetierCriteria();

        assertThat(permissionMetierCriteria).hasToString("PermissionMetierCriteria{}");
    }

    private static void setAllFilters(PermissionMetierCriteria permissionMetierCriteria) {
        permissionMetierCriteria.id();
        permissionMetierCriteria.code();
        permissionMetierCriteria.libelle();
        permissionMetierCriteria.module();
        permissionMetierCriteria.profilsId();
        permissionMetierCriteria.distinct();
    }

    private static Condition<PermissionMetierCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getLibelle()) &&
                condition.apply(criteria.getModule()) &&
                condition.apply(criteria.getProfilsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PermissionMetierCriteria> copyFiltersAre(
        PermissionMetierCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getLibelle(), copy.getLibelle()) &&
                condition.apply(criteria.getModule(), copy.getModule()) &&
                condition.apply(criteria.getProfilsId(), copy.getProfilsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
