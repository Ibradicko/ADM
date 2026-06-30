package com.adm.supervision.repository;

import com.adm.supervision.domain.CodeBarresProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CodeBarresProduit entity.
 */
@Repository
public interface CodeBarresProduitRepository extends JpaRepository<CodeBarresProduit, Long>, JpaSpecificationExecutor<CodeBarresProduit> {
    @Query(
        "select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit produit left join fetch produit.boutique where codeBarresProduit.code = :code and codeBarresProduit.actif = true"
    )
    Optional<CodeBarresProduit> findActiveByCode(@Param("code") String code);

    @Query(
        "select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit produit left join fetch produit.boutique where codeBarresProduit.code = :code and codeBarresProduit.actif = true"
    )
    List<CodeBarresProduit> findAllActiveByCode(@Param("code") String code);

    @Query(
        "select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit produit left join fetch produit.boutique boutique where codeBarresProduit.code = :code and codeBarresProduit.actif = true and boutique.id = :boutiqueId"
    )
    List<CodeBarresProduit> findAllActiveByCodeAndBoutiqueId(@Param("code") String code, @Param("boutiqueId") Long boutiqueId);

    @Query(
        "select codeBarresProduit from CodeBarresProduit codeBarresProduit where codeBarresProduit.produit.id = :produitId and codeBarresProduit.principal = true and codeBarresProduit.actif = true"
    )
    List<CodeBarresProduit> findAllActivePrincipalByProduitId(@Param("produitId") Long produitId);

    default Optional<CodeBarresProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CodeBarresProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CodeBarresProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit",
        countQuery = "select count(codeBarresProduit) from CodeBarresProduit codeBarresProduit"
    )
    Page<CodeBarresProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query("select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit")
    List<CodeBarresProduit> findAllWithToOneRelationships();

    @Query(
        "select codeBarresProduit from CodeBarresProduit codeBarresProduit left join fetch codeBarresProduit.produit where codeBarresProduit.id =:id"
    )
    Optional<CodeBarresProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
