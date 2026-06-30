package com.adm.supervision.repository;

import com.adm.supervision.domain.TarifProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TarifProduit entity.
 */
@Repository
public interface TarifProduitRepository extends JpaRepository<TarifProduit, Long>, JpaSpecificationExecutor<TarifProduit> {
    default Optional<TarifProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TarifProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TarifProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select tarifProduit from TarifProduit tarifProduit left join fetch tarifProduit.produit",
        countQuery = "select count(tarifProduit) from TarifProduit tarifProduit"
    )
    Page<TarifProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query("select tarifProduit from TarifProduit tarifProduit left join fetch tarifProduit.produit")
    List<TarifProduit> findAllWithToOneRelationships();

    @Query("select tarifProduit from TarifProduit tarifProduit left join fetch tarifProduit.produit where tarifProduit.id =:id")
    Optional<TarifProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
