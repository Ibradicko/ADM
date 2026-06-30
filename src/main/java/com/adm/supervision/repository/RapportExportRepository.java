package com.adm.supervision.repository;

import com.adm.supervision.domain.RapportExport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RapportExport entity.
 */
@Repository
public interface RapportExportRepository extends JpaRepository<RapportExport, Long>, JpaSpecificationExecutor<RapportExport> {
    @Query("select rapportExport from RapportExport rapportExport where rapportExport.utilisateur.login = ?#{authentication.name}")
    List<RapportExport> findByUtilisateurIsCurrentUser();

    default Optional<RapportExport> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RapportExport> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RapportExport> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select rapportExport from RapportExport rapportExport left join fetch rapportExport.boutique left join fetch rapportExport.locataire left join fetch rapportExport.utilisateur",
        countQuery = "select count(rapportExport) from RapportExport rapportExport"
    )
    Page<RapportExport> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select rapportExport from RapportExport rapportExport left join fetch rapportExport.boutique left join fetch rapportExport.locataire left join fetch rapportExport.utilisateur"
    )
    List<RapportExport> findAllWithToOneRelationships();

    @Query(
        "select rapportExport from RapportExport rapportExport left join fetch rapportExport.boutique left join fetch rapportExport.locataire left join fetch rapportExport.utilisateur where rapportExport.id =:id"
    )
    Optional<RapportExport> findOneWithToOneRelationships(@Param("id") Long id);
}
