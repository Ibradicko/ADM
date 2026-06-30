package com.adm.supervision.repository;

import com.adm.supervision.domain.LigneVente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LigneVente entity.
 */
@Repository
public interface LigneVenteRepository extends JpaRepository<LigneVente, Long>, JpaSpecificationExecutor<LigneVente> {
    List<LigneVente> findAllByVente_Id(Long venteId);

    default Optional<LigneVente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LigneVente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LigneVente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ligneVente from LigneVente ligneVente left join fetch ligneVente.vente left join fetch ligneVente.produit",
        countQuery = "select count(ligneVente) from LigneVente ligneVente"
    )
    Page<LigneVente> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ligneVente from LigneVente ligneVente left join fetch ligneVente.vente left join fetch ligneVente.produit")
    List<LigneVente> findAllWithToOneRelationships();

    @Query(
        "select ligneVente from LigneVente ligneVente left join fetch ligneVente.vente left join fetch ligneVente.produit where ligneVente.id =:id"
    )
    Optional<LigneVente> findOneWithToOneRelationships(@Param("id") Long id);
}
