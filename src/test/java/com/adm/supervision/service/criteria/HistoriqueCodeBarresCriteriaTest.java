package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HistoriqueCodeBarresCriteriaTest {

    @Test
    void newHistoriqueCodeBarresCriteriaHasAllFiltersNullTest() {
        var historiqueCodeBarresCriteria = new HistoriqueCodeBarresCriteria();
        assertThat(historiqueCodeBarresCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void historiqueCodeBarresCriteriaFluentMethodsCreatesFiltersTest() {
        var historiqueCodeBarresCriteria = new HistoriqueCodeBarresCriteria();

        setAllFilters(historiqueCodeBarresCriteria);

        assertThat(historiqueCodeBarresCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void historiqueCodeBarresCriteriaCopyCreatesNullFilterTest() {
        var historiqueCodeBarresCriteria = new HistoriqueCodeBarresCriteria();
        var copy = historiqueCodeBarresCriteria.copy();

        assertThat(historiqueCodeBarresCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(historiqueCodeBarresCriteria)
        );
    }

    @Test
    void historiqueCodeBarresCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var historiqueCodeBarresCriteria = new HistoriqueCodeBarresCriteria();
        setAllFilters(historiqueCodeBarresCriteria);

        var copy = historiqueCodeBarresCriteria.copy();

        assertThat(historiqueCodeBarresCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(historiqueCodeBarresCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var historiqueCodeBarresCriteria = new HistoriqueCodeBarresCriteria();

        assertThat(historiqueCodeBarresCriteria).hasToString("HistoriqueCodeBarresCriteria{}");
    }

    private static void setAllFilters(HistoriqueCodeBarresCriteria historiqueCodeBarresCriteria) {
        historiqueCodeBarresCriteria.id();
        historiqueCodeBarresCriteria.ancienCode();
        historiqueCodeBarresCriteria.nouveauCode();
        historiqueCodeBarresCriteria.motif();
        historiqueCodeBarresCriteria.dateChangement();
        historiqueCodeBarresCriteria.produitId();
        historiqueCodeBarresCriteria.utilisateurId();
        historiqueCodeBarresCriteria.distinct();
    }

    private static Condition<HistoriqueCodeBarresCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAncienCode()) &&
                condition.apply(criteria.getNouveauCode()) &&
                condition.apply(criteria.getMotif()) &&
                condition.apply(criteria.getDateChangement()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HistoriqueCodeBarresCriteria> copyFiltersAre(
        HistoriqueCodeBarresCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAncienCode(), copy.getAncienCode()) &&
                condition.apply(criteria.getNouveauCode(), copy.getNouveauCode()) &&
                condition.apply(criteria.getMotif(), copy.getMotif()) &&
                condition.apply(criteria.getDateChangement(), copy.getDateChangement()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
