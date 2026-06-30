package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class LigneVenteCriteriaTest {

    @Test
    void newLigneVenteCriteriaHasAllFiltersNullTest() {
        var ligneVenteCriteria = new LigneVenteCriteria();
        assertThat(ligneVenteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ligneVenteCriteriaFluentMethodsCreatesFiltersTest() {
        var ligneVenteCriteria = new LigneVenteCriteria();

        setAllFilters(ligneVenteCriteria);

        assertThat(ligneVenteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ligneVenteCriteriaCopyCreatesNullFilterTest() {
        var ligneVenteCriteria = new LigneVenteCriteria();
        var copy = ligneVenteCriteria.copy();

        assertThat(ligneVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneVenteCriteria)
        );
    }

    @Test
    void ligneVenteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ligneVenteCriteria = new LigneVenteCriteria();
        setAllFilters(ligneVenteCriteria);

        var copy = ligneVenteCriteria.copy();

        assertThat(ligneVenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ligneVenteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ligneVenteCriteria = new LigneVenteCriteria();

        assertThat(ligneVenteCriteria).hasToString("LigneVenteCriteria{}");
    }

    private static void setAllFilters(LigneVenteCriteria ligneVenteCriteria) {
        ligneVenteCriteria.id();
        ligneVenteCriteria.quantite();
        ligneVenteCriteria.prixUnitaire();
        ligneVenteCriteria.remise();
        ligneVenteCriteria.montantLigne();
        ligneVenteCriteria.codeBarresScanne();
        ligneVenteCriteria.venteId();
        ligneVenteCriteria.produitId();
        ligneVenteCriteria.distinct();
    }

    private static Condition<LigneVenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getQuantite()) &&
                condition.apply(criteria.getPrixUnitaire()) &&
                condition.apply(criteria.getRemise()) &&
                condition.apply(criteria.getMontantLigne()) &&
                condition.apply(criteria.getCodeBarresScanne()) &&
                condition.apply(criteria.getVenteId()) &&
                condition.apply(criteria.getProduitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<LigneVenteCriteria> copyFiltersAre(LigneVenteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getQuantite(), copy.getQuantite()) &&
                condition.apply(criteria.getPrixUnitaire(), copy.getPrixUnitaire()) &&
                condition.apply(criteria.getRemise(), copy.getRemise()) &&
                condition.apply(criteria.getMontantLigne(), copy.getMontantLigne()) &&
                condition.apply(criteria.getCodeBarresScanne(), copy.getCodeBarresScanne()) &&
                condition.apply(criteria.getVenteId(), copy.getVenteId()) &&
                condition.apply(criteria.getProduitId(), copy.getProduitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
