package com.adm.supervision.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ScanInconnuCriteriaTest {

    @Test
    void newScanInconnuCriteriaHasAllFiltersNullTest() {
        var scanInconnuCriteria = new ScanInconnuCriteria();
        assertThat(scanInconnuCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void scanInconnuCriteriaFluentMethodsCreatesFiltersTest() {
        var scanInconnuCriteria = new ScanInconnuCriteria();

        setAllFilters(scanInconnuCriteria);

        assertThat(scanInconnuCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void scanInconnuCriteriaCopyCreatesNullFilterTest() {
        var scanInconnuCriteria = new ScanInconnuCriteria();
        var copy = scanInconnuCriteria.copy();

        assertThat(scanInconnuCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(scanInconnuCriteria)
        );
    }

    @Test
    void scanInconnuCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var scanInconnuCriteria = new ScanInconnuCriteria();
        setAllFilters(scanInconnuCriteria);

        var copy = scanInconnuCriteria.copy();

        assertThat(scanInconnuCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(scanInconnuCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var scanInconnuCriteria = new ScanInconnuCriteria();

        assertThat(scanInconnuCriteria).hasToString("ScanInconnuCriteria{}");
    }

    private static void setAllFilters(ScanInconnuCriteria scanInconnuCriteria) {
        scanInconnuCriteria.id();
        scanInconnuCriteria.codeScanne();
        scanInconnuCriteria.ecranOrigine();
        scanInconnuCriteria.dateScan();
        scanInconnuCriteria.commentaire();
        scanInconnuCriteria.resolu();
        scanInconnuCriteria.boutiqueId();
        scanInconnuCriteria.produitAffecteId();
        scanInconnuCriteria.distinct();
    }

    private static Condition<ScanInconnuCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCodeScanne()) &&
                condition.apply(criteria.getEcranOrigine()) &&
                condition.apply(criteria.getDateScan()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getResolu()) &&
                condition.apply(criteria.getBoutiqueId()) &&
                condition.apply(criteria.getProduitAffecteId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ScanInconnuCriteria> copyFiltersAre(ScanInconnuCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCodeScanne(), copy.getCodeScanne()) &&
                condition.apply(criteria.getEcranOrigine(), copy.getEcranOrigine()) &&
                condition.apply(criteria.getDateScan(), copy.getDateScan()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getResolu(), copy.getResolu()) &&
                condition.apply(criteria.getBoutiqueId(), copy.getBoutiqueId()) &&
                condition.apply(criteria.getProduitAffecteId(), copy.getProduitAffecteId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
