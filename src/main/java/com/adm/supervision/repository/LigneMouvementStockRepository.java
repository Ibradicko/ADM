package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneMouvementStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneMouvementStock entity.
 */
@Repository
public interface LigneMouvementStockRepository
    extends JpaRepository<LigneMouvementStock, Long>, JpaSpecificationExecutor<LigneMouvementStock>
{
    List<LigneMouvementStock> findAllByMouvement_Id(Long mouvementId);

    default Optional<LigneMouvementStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneMouvementStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneMouvementStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneMouvementStock from LigneMouvementStock ligneMouvementStock left join fetch ligneMouvementStock.mouvement left join fetch ligneMouvementStock.produit left join fetch ligneMouvementStock.depot",
        countQuery = "select count(ligneMouvementStock) from LigneMouvementStock ligneMouvementStock"
    )
    Page<LigneMouvementStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ligneMouvementStock from LigneMouvementStock ligneMouvementStock left join fetch ligneMouvementStock.mouvement left join fetch ligneMouvementStock.produit left join fetch ligneMouvementStock.depot"
    )
    List<LigneMouvementStock> findAllWithToOneRelationships();

    @Query(
        "select ligneMouvementStock from LigneMouvementStock ligneMouvementStock left join fetch ligneMouvementStock.mouvement left join fetch ligneMouvementStock.produit left join fetch ligneMouvementStock.depot where ligneMouvementStock.id =:id"
    )
    Optional<LigneMouvementStock> findOneWithToOneRelationships(@Param("id") Long id);
}
