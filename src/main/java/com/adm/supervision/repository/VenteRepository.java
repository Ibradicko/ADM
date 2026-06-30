package com.adm.supervision.repository;

import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutVente;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Vente entity.
 */
@Repository
public interface VenteRepository extends JpaRepository<Vente, Long>, JpaSpecificationExecutor<Vente> {
    @Query("select vente from Vente vente where vente.vendeur.login = ?#{authentication.name}")
    List<Vente> findByVendeurIsCurrentUser();

    List<Vente> findAllByDateHeureBetween(java.time.Instant start, java.time.Instant end);

    List<Vente> findAllByDateHeureBetweenAndBoutique_Id(java.time.Instant start, java.time.Instant end, Long boutiqueId);

    @Query("select vente.id from Vente vente where vente.boutique.id in :boutiqueIds")
    List<Long> findIdsByBoutiqueIds(@Param("boutiqueIds") Collection<Long> boutiqueIds);

    List<Vente> findAllByDateHeureBetweenAndBoutique_IdAndLocataire_IdAndStatut(
        Instant start,
        Instant end,
        Long boutiqueId,
        Long locataireId,
        StatutVente statut
    );

    default Optional<Vente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Vente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Vente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select vente from Vente vente left join fetch vente.boutique left join fetch vente.locataire left join fetch vente.vendeur",
        countQuery = "select count(vente) from Vente vente"
    )
    Page<Vente> findAllWithToOneRelationships(Pageable pageable);

    @Query("select vente from Vente vente left join fetch vente.boutique left join fetch vente.locataire left join fetch vente.vendeur")
    List<Vente> findAllWithToOneRelationships();

    @Query(
        "select vente from Vente vente left join fetch vente.boutique left join fetch vente.locataire left join fetch vente.vendeur where vente.id =:id"
    )
    Optional<Vente> findOneWithToOneRelationships(@Param("id") Long id);
}
