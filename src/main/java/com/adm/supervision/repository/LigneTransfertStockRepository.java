package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneTransfertStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneTransfertStock entity.
 */
@Repository
public interface LigneTransfertStockRepository
    extends JpaRepository<LigneTransfertStock, Long>, JpaSpecificationExecutor<LigneTransfertStock>
{
    List<LigneTransfertStock> findAllByTransfert_Id(Long transfertId);

    Optional<LigneTransfertStock> findByTransfert_IdAndProduit_Id(Long transfertId, Long produitId);

    default Optional<LigneTransfertStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneTransfertStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneTransfertStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneTransfertStock from LigneTransfertStock ligneTransfertStock left join fetch ligneTransfertStock.transfert left join fetch ligneTransfertStock.produit",
        countQuery = "select count(ligneTransfertStock) from LigneTransfertStock ligneTransfertStock"
    )
    Page<LigneTransfertStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ligneTransfertStock from LigneTransfertStock ligneTransfertStock left join fetch ligneTransfertStock.transfert left join fetch ligneTransfertStock.produit"
    )
    List<LigneTransfertStock> findAllWithToOneRelationships();

    @Query(
        "select ligneTransfertStock from LigneTransfertStock ligneTransfertStock left join fetch ligneTransfertStock.transfert left join fetch ligneTransfertStock.produit where ligneTransfertStock.id =:id"
    )
    Optional<LigneTransfertStock> findOneWithToOneRelationships(@Param("id") Long id);
}
