package com.adm.supervision.repository;

import com.adm.supervision.domain.HistoriqueCodeBarres;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HistoriqueCodeBarres entity.
 */
@Repository
public interface HistoriqueCodeBarresRepository
    extends JpaRepository<HistoriqueCodeBarres, Long>, JpaSpecificationExecutor<HistoriqueCodeBarres>
{
    @Query(
        "select historiqueCodeBarres from HistoriqueCodeBarres historiqueCodeBarres where historiqueCodeBarres.utilisateur.login = ?#{authentication.name}"
    )
    List<HistoriqueCodeBarres> findByUtilisateurIsCurrentUser();

    default Optional<HistoriqueCodeBarres> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<HistoriqueCodeBarres> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<HistoriqueCodeBarres> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select historiqueCodeBarres from HistoriqueCodeBarres historiqueCodeBarres left join fetch historiqueCodeBarres.produit left join fetch historiqueCodeBarres.utilisateur",
        countQuery = "select count(historiqueCodeBarres) from HistoriqueCodeBarres historiqueCodeBarres"
    )
    Page<HistoriqueCodeBarres> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select historiqueCodeBarres from HistoriqueCodeBarres historiqueCodeBarres left join fetch historiqueCodeBarres.produit left join fetch historiqueCodeBarres.utilisateur"
    )
    List<HistoriqueCodeBarres> findAllWithToOneRelationships();

    @Query(
        "select historiqueCodeBarres from HistoriqueCodeBarres historiqueCodeBarres left join fetch historiqueCodeBarres.produit left join fetch historiqueCodeBarres.utilisateur where historiqueCodeBarres.id =:id"
    )
    Optional<HistoriqueCodeBarres> findOneWithToOneRelationships(@Param("id") Long id);
}
