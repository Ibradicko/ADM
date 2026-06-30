package com.adm.supervision.repository;

import com.adm.supervision.domain.PaiementVente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PaiementVente entity.
 */
@Repository
public interface PaiementVenteRepository extends JpaRepository<PaiementVente, Long>, JpaSpecificationExecutor<PaiementVente> {
    List<PaiementVente> findAllByVenteId(Long venteId);

    default Optional<PaiementVente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PaiementVente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PaiementVente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select paiementVente from PaiementVente paiementVente left join fetch paiementVente.vente left join fetch paiementVente.modePaiement",
        countQuery = "select count(paiementVente) from PaiementVente paiementVente"
    )
    Page<PaiementVente> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select paiementVente from PaiementVente paiementVente left join fetch paiementVente.vente left join fetch paiementVente.modePaiement"
    )
    List<PaiementVente> findAllWithToOneRelationships();

    @Query(
        "select paiementVente from PaiementVente paiementVente left join fetch paiementVente.vente left join fetch paiementVente.modePaiement where paiementVente.id =:id"
    )
    Optional<PaiementVente> findOneWithToOneRelationships(@Param("id") Long id);
}
