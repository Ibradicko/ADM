package com.adm.supervision.repository;

import com.adm.supervision.domain.StockProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockProduit entity.
 */
@Repository
public interface StockProduitRepository extends JpaRepository<StockProduit, Long>, JpaSpecificationExecutor<StockProduit> {
    Optional<StockProduit> findByProduit_IdAndDepot_Id(Long produitId, Long depotId);

    List<StockProduit> findAllByProduit_IdAndDepot_Id(Long produitId, Long depotId);

    @Query(
        "select stockProduit from StockProduit stockProduit join fetch stockProduit.produit join fetch stockProduit.depot depot where stockProduit.produit.id = :produitId and depot.boutique.id = :boutiqueId and depot.actif = true"
    )
    List<StockProduit> findByProduitIdAndBoutiqueId(@Param("produitId") Long produitId, @Param("boutiqueId") Long boutiqueId);

    @Query(
        "select stockProduit from StockProduit stockProduit join fetch stockProduit.produit join fetch stockProduit.depot depot where depot.boutique.id = :boutiqueId and depot.actif = true"
    )
    List<StockProduit> findByBoutiqueId(@Param("boutiqueId") Long boutiqueId);

    @Query(
        "select stockProduit from StockProduit stockProduit join fetch stockProduit.produit join fetch stockProduit.depot depot where depot.id = :depotId"
    )
    List<StockProduit> findByDepotIdWithRelationships(@Param("depotId") Long depotId);

    @Query(
        "select stockProduit from StockProduit stockProduit join fetch stockProduit.produit join fetch stockProduit.depot depot join fetch depot.boutique boutique where stockProduit.stockAlerte is not null and stockProduit.quantiteTheorique <= stockProduit.stockAlerte"
    )
    List<StockProduit> findStockAlerts();

    @Query(
        "select stockProduit from StockProduit stockProduit join fetch stockProduit.produit join fetch stockProduit.depot depot join fetch depot.boutique boutique where boutique.id = :boutiqueId and stockProduit.stockAlerte is not null and stockProduit.quantiteTheorique <= stockProduit.stockAlerte"
    )
    List<StockProduit> findStockAlertsByBoutiqueId(@Param("boutiqueId") Long boutiqueId);

    default Optional<StockProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select stockProduit from StockProduit stockProduit left join fetch stockProduit.produit left join fetch stockProduit.depot",
        countQuery = "select count(stockProduit) from StockProduit stockProduit"
    )
    Page<StockProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query("select stockProduit from StockProduit stockProduit left join fetch stockProduit.produit left join fetch stockProduit.depot")
    List<StockProduit> findAllWithToOneRelationships();

    @Query(
        "select stockProduit from StockProduit stockProduit left join fetch stockProduit.produit left join fetch stockProduit.depot where stockProduit.id =:id"
    )
    Optional<StockProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
