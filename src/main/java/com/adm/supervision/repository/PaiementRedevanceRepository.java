package com.adm.supervision.repository;

import com.adm.supervision.domain.PaiementRedevance;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaiementRedevance entity.
 */
@Repository
public interface PaiementRedevanceRepository extends JpaRepository<PaiementRedevance, Long>, JpaSpecificationExecutor<PaiementRedevance> {
    List<PaiementRedevance> findAllByCalcul_Id(Long calculId);

    @Query("select coalesce(sum(p.montant), 0) from PaiementRedevance p where p.calcul.id = :calculId")
    BigDecimal sumMontantByCalculId(@Param("calculId") Long calculId);

    default Optional<PaiementRedevance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PaiementRedevance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PaiementRedevance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select paiementRedevance from PaiementRedevance paiementRedevance left join fetch paiementRedevance.calcul",
        countQuery = "select count(paiementRedevance) from PaiementRedevance paiementRedevance"
    )
    Page<PaiementRedevance> findAllWithToOneRelationships(Pageable pageable);

    @Query("select paiementRedevance from PaiementRedevance paiementRedevance left join fetch paiementRedevance.calcul")
    List<PaiementRedevance> findAllWithToOneRelationships();

    @Query(
        "select paiementRedevance from PaiementRedevance paiementRedevance left join fetch paiementRedevance.calcul where paiementRedevance.id =:id"
    )
    Optional<PaiementRedevance> findOneWithToOneRelationships(@Param("id") Long id);
}
