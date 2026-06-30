package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RapportExportCriteriaTest {

    @Test
    void newRapportExportCriteriaHasAllFiltersNullTest() {
        var rapportExportCriteria = new RapportExportCriteria();
        assertThat(rapportExportCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void rapportExportCriteriaFluentMethodsCreatesFiltersTest() {
        var rapportExportCriteria = new RapportExportCriteria();

        setAllFilters(rapportExportCriteria);

        assertThat(rapportExportCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void rapportExportCriteriaCopyCreatesNullFilterTest() {
        var rapportExportCriteria = new RapportExportCriteria();
        var copy = rapportExportCriteria.copy();

        assertThat(rapportExportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(rapportExportCriteria)
        );
    }

    @Test
    void rapportExportCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var rapportExportCriteria = new RapportExportCriteria();
        setAllFilters(rapportExportCriteria);

        var copy = rapportExportCriteria.copy();

        assertThat(rapportExportCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(rapportExportCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var rapportExportCriteria = new RapportExportCriteria();

        assertThat(rapportExportCriteria).hasToString("RapportExportCriteria{}");
    }

    private static void setAllFilters(RapportExportCriteria rapportExportCriteria) {
        rapportExportCriteria.id();
        rapportExportCriteria.reference();
        rapportExportCriteria.typeRapport();
        rapportExportCriteria.format();
        rapportExportCriteria.periodeDebut();
        rapportExportCriteria.periodeFin();
        rapportExportCriteria.cheminFichier();
        rapportExportCriteria.dateGeneration();
        rapportExportCriteria.boutiqueId();
        rapportExportCriteria.locataireId();
        rapportExportCriteria.utilisateurId();
        rapportExportCriteria.distinct();
    }

    private static Condition<RapportExportCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTypeRapport()) &&
                condition.apply(criteria.getFormat()) &&
                condition.apply(criteria.getPeriodeDebut()) &&
                condition.apply(criteria.getPeriodeFin()) &&
                condition.apply(criteria.getCheminFichier()) &&
                condition.apply(criteria.getDateGeneration()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RapportExportCriteria> copyFiltersAre(
        RapportExportCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTypeRapport(), copy.getTypeRapport()) &&
                condition.apply(criteria.getFormat(), copy.getFormat()) &&
                condition.apply(criteria.getPeriodeDebut(), copy.getPeriodeDebut()) &&
                condition.apply(criteria.getPeriodeFin(), copy.getPeriodeFin()) &&
                condition.apply(criteria.getCheminFichier(), copy.getCheminFichier()) &&
                condition.apply(criteria.getDateGeneration(), copy.getDateGeneration()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getLocataireId(), copy.getLocataireId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
