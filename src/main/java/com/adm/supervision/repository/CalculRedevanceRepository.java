package com.adm.supervision.repository;

import com.adm.supervision.domain.CalculRedevance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CalculRedevance entity.
 */
@Repository
public interface CalculRedevanceRepository extends JpaRepository<CalculRedevance, Long>, JpaSpecificationExecutor<CalculRedevance> {
    List<CalculRedevance> findAllByBoutique_IdAndLocataire_IdAndPeriodeDebutAndPeriodeFin(
        Long boutiqueId,
        Long locataireId,
        LocalDate periodeDebut,
        LocalDate periodeFin
    );

    default Optional<CalculRedevance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CalculRedevance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CalculRedevance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select calculRedevance from CalculRedevance calculRedevance left join fetch calculRedevance.boutique left join fetch calculRedevance.locataire",
        countQuery = "select count(calculRedevance) from CalculRedevance calculRedevance"
    )
    Page<CalculRedevance> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select calculRedevance from CalculRedevance calculRedevance left join fetch calculRedevance.boutique left join fetch calculRedevance.locataire"
    )
    List<CalculRedevance> findAllWithToOneRelationships();

    @Query(
        "select calculRedevance from CalculRedevance calculRedevance left join fetch calculRedevance.boutique left join fetch calculRedevance.locataire where calculRedevance.id =:id"
    )
    Optional<CalculRedevance> findOneWithToOneRelationships(@Param("id") Long id);
}
