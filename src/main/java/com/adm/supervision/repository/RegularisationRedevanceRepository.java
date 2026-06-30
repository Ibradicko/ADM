package com.adm.supervision.repository;

import com.adm.supervision.domain.RegularisationRedevance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RegularisationRedevance entity.
 */
@Repository
public interface RegularisationRedevanceRepository
    extends JpaRepository<RegularisationRedevance, Long>, JpaSpecificationExecutor<RegularisationRedevance>
{
    default Optional<RegularisationRedevance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RegularisationRedevance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RegularisationRedevance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select regularisationRedevance from RegularisationRedevance regularisationRedevance left join fetch regularisationRedevance.calcul",
        countQuery = "select count(regularisationRedevance) from RegularisationRedevance regularisationRedevance"
    )
    Page<RegularisationRedevance> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select regularisationRedevance from RegularisationRedevance regularisationRedevance left join fetch regularisationRedevance.calcul"
    )
    List<RegularisationRedevance> findAllWithToOneRelationships();

    @Query(
        "select regularisationRedevance from RegularisationRedevance regularisationRedevance left join fetch regularisationRedevance.calcul where regularisationRedevance.id =:id"
    )
    Optional<RegularisationRedevance> findOneWithToOneRelationships(@Param("id") Long id);
}
