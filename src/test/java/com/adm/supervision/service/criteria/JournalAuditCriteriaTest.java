package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class JournalAuditCriteriaTest {

    @Test
    void newJournalAuditCriteriaHasAllFiltersNullTest() {
        var journalAuditCriteria = new JournalAuditCriteria();
        assertThat(journalAuditCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void journalAuditCriteriaFluentMethodsCreatesFiltersTest() {
        var journalAuditCriteria = new JournalAuditCriteria();

        setAllFilters(journalAuditCriteria);

        assertThat(journalAuditCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void journalAuditCriteriaCopyCreatesNullFilterTest() {
        var journalAuditCriteria = new JournalAuditCriteria();
        var copy = journalAuditCriteria.copy();

        assertThat(journalAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(journalAuditCriteria)
        );
    }

    @Test
    void journalAuditCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var journalAuditCriteria = new JournalAuditCriteria();
        setAllFilters(journalAuditCriteria);

        var copy = journalAuditCriteria.copy();

        assertThat(journalAuditCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(journalAuditCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var journalAuditCriteria = new JournalAuditCriteria();

        assertThat(journalAuditCriteria).hasToString("JournalAuditCriteria{}");
    }

    private static void setAllFilters(JournalAuditCriteria journalAuditCriteria) {
        journalAuditCriteria.id();
        journalAuditCriteria.typeAction();
        journalAuditCriteria.entiteConcernee();
        journalAuditCriteria.identifiantEntite();
        journalAuditCriteria.adresseIp();
        journalAuditCriteria.dateAction();
        journalAuditCriteria.boutiqueId();
        journalAuditCriteria.utilisateurId();
        journalAuditCriteria.distinct();
    }

    private static Condition<JournalAuditCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTypeAction()) &&
                condition.apply(criteria.getEntiteConcernee()) &&
                condition.apply(criteria.getIdentifiantEntite()) &&
                condition.apply(criteria.getAdresseIp()) &&
                condition.apply(criteria.getDateAction()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<JournalAuditCriteria> copyFiltersAre(
        JournalAuditCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTypeAction(), copy.getTypeAction()) &&
                condition.apply(criteria.getEntiteConcernee(), copy.getEntiteConcernee()) &&
                condition.apply(criteria.getIdentifiantEntite(), copy.getIdentifiantEntite()) &&
                condition.apply(criteria.getAdresseIp(), copy.getAdresseIp()) &&
                condition.apply(criteria.getDateAction(), copy.getDateAction()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
