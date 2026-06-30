package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneReceptionProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneReceptionProduit entity.
 */
@Repository
public interface LigneReceptionProduitRepository
    extends JpaRepository<LigneReceptionProduit, Long>, JpaSpecificationExecutor<LigneReceptionProduit>
{
    List<LigneReceptionProduit> findAllByReception_Id(Long receptionId);

    Optional<LigneReceptionProduit> findByReception_IdAndProduit_Id(Long receptionId, Long produitId);

    default Optional<LigneReceptionProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneReceptionProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneReceptionProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneReceptionProduit from LigneReceptionProduit ligneReceptionProduit left join fetch ligneReceptionProduit.reception left join fetch ligneReceptionProduit.produit",
        countQuery = "select count(ligneReceptionProduit) from LigneReceptionProduit ligneReceptionProduit"
    )
    Page<LigneReceptionProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ligneReceptionProduit from LigneReceptionProduit ligneReceptionProduit left join fetch ligneReceptionProduit.reception left join fetch ligneReceptionProduit.produit"
    )
    List<LigneReceptionProduit> findAllWithToOneRelationships();

    @Query(
        "select ligneReceptionProduit from LigneReceptionProduit ligneReceptionProduit left join fetch ligneReceptionProduit.reception left join fetch ligneReceptionProduit.produit where ligneReceptionProduit.id =:id"
    )
    Optional<LigneReceptionProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
