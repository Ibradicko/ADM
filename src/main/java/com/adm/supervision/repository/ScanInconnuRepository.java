package com.adm.supervision.repository;

import com.adm.supervision.domain.ScanInconnu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScanInconnu entity.
 */
@Repository
public interface ScanInconnuRepository extends JpaRepository<ScanInconnu, Long>, JpaSpecificationExecutor<ScanInconnu> {
    long countByResoluFalse();

    long countByBoutique_IdAndResoluFalse(Long boutiqueId);

    default Optional<ScanInconnu> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ScanInconnu> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ScanInconnu> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select scanInconnu from ScanInconnu scanInconnu left join fetch scanInconnu.boutique left join fetch scanInconnu.produitAffecte",
        countQuery = "select count(scanInconnu) from ScanInconnu scanInconnu"
    )
    Page<ScanInconnu> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select scanInconnu from ScanInconnu scanInconnu left join fetch scanInconnu.boutique left join fetch scanInconnu.produitAffecte"
    )
    List<ScanInconnu> findAllWithToOneRelationships();

    @Query(
        "select scanInconnu from ScanInconnu scanInconnu left join fetch scanInconnu.boutique left join fetch scanInconnu.produitAffecte where scanInconnu.id =:id"
    )
    Optional<ScanInconnu> findOneWithToOneRelationships(@Param("id") Long id);
}
