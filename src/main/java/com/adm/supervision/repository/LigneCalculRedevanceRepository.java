package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneCalculRedevance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneCalculRedevance entity.
 */
@Repository
public interface LigneCalculRedevanceRepository
    extends JpaRepository<LigneCalculRedevance, Long>, JpaSpecificationExecutor<LigneCalculRedevance>
{
    default Optional<LigneCalculRedevance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneCalculRedevance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneCalculRedevance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneCalculRedevance from LigneCalculRedevance ligneCalculRedevance left join fetch ligneCalculRedevance.calcul left join fetch ligneCalculRedevance.vente",
        countQuery = "select count(ligneCalculRedevance) from LigneCalculRedevance ligneCalculRedevance"
    )
    Page<LigneCalculRedevance> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ligneCalculRedevance from LigneCalculRedevance ligneCalculRedevance left join fetch ligneCalculRedevance.calcul left join fetch ligneCalculRedevance.vente"
    )
    List<LigneCalculRedevance> findAllWithToOneRelationships();

    @Query(
        "select ligneCalculRedevance from LigneCalculRedevance ligneCalculRedevance left join fetch ligneCalculRedevance.calcul left join fetch ligneCalculRedevance.vente where ligneCalculRedevance.id =:id"
    )
    Optional<LigneCalculRedevance> findOneWithToOneRelationships(@Param("id") Long id);
}
