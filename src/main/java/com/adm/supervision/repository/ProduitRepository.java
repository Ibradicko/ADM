package com.adm.supervision.repository;

import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Produit entity.
 */
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {
    Optional<Produit> findByCodeInterneAndBoutique_Id(String codeInterne, Long boutiqueId);

    @Query(
        "select produit from Produit produit left join fetch produit.groupeArticle where produit.boutique.id = :boutiqueId and produit.statut = :statut"
    )
    List<Produit> findByBoutique_IdAndStatut(@Param("boutiqueId") Long boutiqueId, @Param("statut") StatutGeneral statut, Sort sort);

    boolean existsByBoutique_IdAndStatut(Long boutiqueId, StatutGeneral statut);

    default Optional<Produit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Produit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Produit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select produit from Produit produit left join fetch produit.boutique left join fetch produit.groupeArticle left join fetch produit.familleArticle left join fetch produit.sousFamilleArticle left join fetch produit.uniteMesure",
        countQuery = "select count(produit) from Produit produit"
    )
    Page<Produit> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select produit from Produit produit left join fetch produit.boutique left join fetch produit.groupeArticle left join fetch produit.familleArticle left join fetch produit.sousFamilleArticle left join fetch produit.uniteMesure"
    )
    List<Produit> findAllWithToOneRelationships();

    @Query(
        "select produit from Produit produit left join fetch produit.boutique left join fetch produit.groupeArticle left join fetch produit.familleArticle left join fetch produit.sousFamilleArticle left join fetch produit.uniteMesure where produit.id =:id"
    )
    Optional<Produit> findOneWithToOneRelationships(@Param("id") Long id);
}
