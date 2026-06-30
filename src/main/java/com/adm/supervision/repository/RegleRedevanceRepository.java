package com.adm.supervision.repository;

import com.adm.supervision.domain.RegleRedevance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RegleRedevance entity.
 */
@Repository
public interface RegleRedevanceRepository extends JpaRepository<RegleRedevance, Long>, JpaSpecificationExecutor<RegleRedevance> {
    default Optional<RegleRedevance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RegleRedevance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RegleRedevance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select regleRedevance from RegleRedevance regleRedevance left join fetch regleRedevance.boutique left join fetch regleRedevance.locataire left join fetch regleRedevance.groupeArticle left join fetch regleRedevance.produit",
        countQuery = "select count(regleRedevance) from RegleRedevance regleRedevance"
    )
    Page<RegleRedevance> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select regleRedevance from RegleRedevance regleRedevance left join fetch regleRedevance.boutique left join fetch regleRedevance.locataire left join fetch regleRedevance.groupeArticle left join fetch regleRedevance.produit"
    )
    List<RegleRedevance> findAllWithToOneRelationships();

    @Query(
        "select regleRedevance from RegleRedevance regleRedevance left join fetch regleRedevance.boutique left join fetch regleRedevance.locataire left join fetch regleRedevance.groupeArticle left join fetch regleRedevance.produit where regleRedevance.id =:id"
    )
    Optional<RegleRedevance> findOneWithToOneRelationships(@Param("id") Long id);
}
