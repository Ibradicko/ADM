package com.adm.supervision.repository;

import com.adm.supervision.domain.DepotStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DepotStock entity.
 */
@Repository
public interface DepotStockRepository extends JpaRepository<DepotStock, Long>, JpaSpecificationExecutor<DepotStock> {
    default Optional<DepotStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DepotStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DepotStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select depotStock from DepotStock depotStock left join fetch depotStock.boutique",
        countQuery = "select count(depotStock) from DepotStock depotStock"
    )
    Page<DepotStock> findAllWithToOneRelationships(Pageable pageable);

    @Query("select depotStock from DepotStock depotStock left join fetch depotStock.boutique")
    List<DepotStock> findAllWithToOneRelationships();

    @Query("select depotStock from DepotStock depotStock left join fetch depotStock.boutique where depotStock.id =:id")
    Optional<DepotStock> findOneWithToOneRelationships(@Param("id") Long id);
}
