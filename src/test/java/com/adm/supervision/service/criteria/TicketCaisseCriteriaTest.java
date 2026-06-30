package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TicketCaisseCriteriaTest {

    @Test
    void newTicketCaisseCriteriaHasAllFiltersNullTest() {
        var ticketCaisseCriteria = new TicketCaisseCriteria();
        assertThat(ticketCaisseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ticketCaisseCriteriaFluentMethodsCreatesFiltersTest() {
        var ticketCaisseCriteria = new TicketCaisseCriteria();

        setAllFilters(ticketCaisseCriteria);

        assertThat(ticketCaisseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ticketCaisseCriteriaCopyCreatesNullFilterTest() {
        var ticketCaisseCriteria = new TicketCaisseCriteria();
        var copy = ticketCaisseCriteria.copy();

        assertThat(ticketCaisseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCaisseCriteria)
        );
    }

    @Test
    void ticketCaisseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ticketCaisseCriteria = new TicketCaisseCriteria();
        setAllFilters(ticketCaisseCriteria);

        var copy = ticketCaisseCriteria.copy();

        assertThat(ticketCaisseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ticketCaisseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ticketCaisseCriteria = new TicketCaisseCriteria();

        assertThat(ticketCaisseCriteria).hasToString("TicketCaisseCriteria{}");
    }

    private static void setAllFilters(TicketCaisseCriteria ticketCaisseCriteria) {
        ticketCaisseCriteria.id();
        ticketCaisseCriteria.numero();
        ticketCaisseCriteria.dateEmission();
        ticketCaisseCriteria.nombreImpressions();
        ticketCaisseCriteria.venteId();
        ticketCaisseCriteria.distinct();
    }

    private static Condition<TicketCaisseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNumero()) &&
                condition.apply(criteria.getDateEmission()) &&
                condition.apply(criteria.getNombreImpressions()) &&
                condition.apply(criteria.getVenteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TicketCaisseCriteria> copyFiltersAre(
        TicketCaisseCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNumero(), copy.getNumero()) &&
                condition.apply(criteria.getDateEmission(), copy.getDateEmission()) &&
                condition.apply(criteria.getNombreImpressions(), copy.getNombreImpressions()) &&
                condition.apply(criteria.getVenteId(), copy.getVenteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
