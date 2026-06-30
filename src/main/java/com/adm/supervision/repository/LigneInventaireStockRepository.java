package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneInventaireStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneInventaireStock entity.
 */
@Repository
public interface LigneInventaireStockRepository
    extends JpaRepository<LigneInventaireStock, Long>, JpaSpecificationExecutor<LigneInventaireStock>
{
    List<LigneInventaireStock> findAllByInventaire_Id(Long inventaireId);

    Optional<LigneInventaireStock> findByInventaire_IdAndProduit_Id(Long inventaireId, Long produitId);

    default Optional<LigneInventaireStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneInventaireStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneInventaireStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneInventaireStock from LigneInventaireStock ligneInventaireStock left join fetch ligneInventaireStock.inventaire left join fetch ligneInventaireStock.produit",
        countQuery = "select count(ligneInventaireStock) from LigneInventaireStock ligneInventaireStock"
    )
    Page<LigneInventaireStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ligneInventaireStock from LigneInventaireStock ligneInventaireStock left join fetch ligneInventaireStock.inventaire left join fetch ligneInventaireStock.produit"
    )
    List<LigneInventaireStock> findAllWithToOneRelationships();

    @Query(
        "select ligneInventaireStock from LigneInventaireStock ligneInventaireStock left join fetch ligneInventaireStock.inventaire left join fetch ligneInventaireStock.produit where ligneInventaireStock.id =:id"
    )
    Optional<LigneInventaireStock> findOneWithToOneRelationships(@Param("id") Long id);
}
