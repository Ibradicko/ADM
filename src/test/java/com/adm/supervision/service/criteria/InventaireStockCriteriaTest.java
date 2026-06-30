package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InventaireStockCriteriaTest {

    @Test
    void newInventaireStockCriteriaHasAllFiltersNullTest() {
        var inventaireStockCriteria = new InventaireStockCriteria();
        assertThat(inventaireStockCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void inventaireStockCriteriaFluentMethodsCreatesFiltersTest() {
        var inventaireStockCriteria = new InventaireStockCriteria();

        setAllFilters(inventaireStockCriteria);

        assertThat(inventaireStockCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void inventaireStockCriteriaCopyCreatesNullFilterTest() {
        var inventaireStockCriteria = new InventaireStockCriteria();
        var copy = inventaireStockCriteria.copy();

        assertThat(inventaireStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(inventaireStockCriteria)
        );
    }

    @Test
    void inventaireStockCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var inventaireStockCriteria = new InventaireStockCriteria();
        setAllFilters(inventaireStockCriteria);

        var copy = inventaireStockCriteria.copy();

        assertThat(inventaireStockCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(inventaireStockCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var inventaireStockCriteria = new InventaireStockCriteria();

        assertThat(inventaireStockCriteria).hasToString("InventaireStockCriteria{}");
    }

    private static void setAllFilters(InventaireStockCriteria inventaireStockCriteria) {
        inventaireStockCriteria.id();
        inventaireStockCriteria.reference();
        inventaireStockCriteria.typeInventaire();
        inventaireStockCriteria.statut();
        inventaireStockCriteria.dateDebut();
        inventaireStockCriteria.dateFin();
        inventaireStockCriteria.boutiqueId();
        inventaireStockCriteria.depotId();
        inventaireStockCriteria.utilisateurId();
        inventaireStockCriteria.distinct();
    }

    private static Condition<InventaireStockCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTypeInventaire()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateDebut()) &&
                condition.apply(criteria.getDateFin()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getDepotId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InventaireStockCriteria> copyFiltersAre(
        InventaireStockCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTypeInventaire(), copy.getTypeInventaire()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateDebut(), copy.getDateDebut()) &&
                condition.apply(criteria.getDateFin(), copy.getDateFin()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getDepotId(), copy.getDepotId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
