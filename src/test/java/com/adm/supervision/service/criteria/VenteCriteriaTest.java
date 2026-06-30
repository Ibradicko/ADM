package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VenteCriteriaTest {

    @Test
    void newVenteCriteriaHasAllFiltersNullTest() {
        var venteCriteria = new VenteCriteria();
        assertThat(venteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void venteCriteriaFluentMethodsCreatesFiltersTest() {
        var venteCriteria = new VenteCriteria();

        setAllFilters(venteCriteria);

        assertThat(venteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void venteCriteriaCopyCreatesNullFilterTest() {
        var venteCriteria = new VenteCriteria();
        var copy = venteCriteria.copy();

        assertThat(venteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(venteCriteria)
        );
    }

    @Test
    void venteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var venteCriteria = new VenteCriteria();
        setAllFilters(venteCriteria);

        var copy = venteCriteria.copy();

        assertThat(venteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(venteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var venteCriteria = new VenteCriteria();

        assertThat(venteCriteria).hasToString("VenteCriteria{}");
    }

    private static void setAllFilters(VenteCriteria venteCriteria) {
        venteCriteria.id();
        venteCriteria.numeroTicket();
        venteCriteria.dateHeure();
        venteCriteria.statut();
        venteCriteria.referencePassager();
        venteCriteria.referenceCarteEmbarquement();
        venteCriteria.montantBrut();
        venteCriteria.montantRemise();
        venteCriteria.montantNet();
        venteCriteria.commentaire();
        venteCriteria.boutiqueId();
        venteCriteria.locataireId();
        venteCriteria.vendeurId();
        venteCriteria.distinct();
    }

    private static Condition<VenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNumeroTicket()) &&
                condition.apply(criteria.getDateHeure()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getReferencePassager()) &&
                condition.apply(criteria.getReferenceCarteEmbarquement()) &&
                condition.apply(criteria.getMontantBrut()) &&
                condition.apply(criteria.getMontantRemise()) &&
                condition.apply(criteria.getMontantNet()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId()) &&
                condition.apply(criteria.getVendeurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VenteCriteria> copyFiltersAre(VenteCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNumeroTicket(), copy.getNumeroTicket()) &&
                condition.apply(criteria.getDateHeure(), copy.getDateHeure()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getReferencePassager(), copy.getReferencePassager()) &&
                condition.apply(criteria.getReferenceCarteEmbarquement(), copy.getReferenceCarteEmbarquement()) &&
                condition.apply(criteria.getMontantBrut(), copy.getMontantBrut()) &&
                condition.apply(criteria.getMontantRemise(), copy.getMontantRemise()) &&
                condition.apply(criteria.getMontantNet(), copy.getMontantNet()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId(), copy.getLocataireId()) &&
                condition.apply(criteria.getVendeurId(), copy.getVendeurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
