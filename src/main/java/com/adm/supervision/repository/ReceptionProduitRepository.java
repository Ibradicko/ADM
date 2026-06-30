package com.adm.supervision.repository;

import com.adm.supervision.domain.ReceptionProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReceptionProduit entity.
 */
@Repository
public interface ReceptionProduitRepository extends JpaRepository<ReceptionProduit, Long>, JpaSpecificationExecutor<ReceptionProduit> {
    @Query(
        "select receptionProduit from ReceptionProduit receptionProduit where receptionProduit.utilisateur.login = ?#{authentication.name}"
    )
    List<ReceptionProduit> findByUtilisateurIsCurrentUser();

    default Optional<ReceptionProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReceptionProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReceptionProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select receptionProduit from ReceptionProduit receptionProduit left join fetch receptionProduit.boutique left join fetch receptionProduit.utilisateur",
        countQuery = "select count(receptionProduit) from ReceptionProduit receptionProduit"
    )
    Page<ReceptionProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select receptionProduit from ReceptionProduit receptionProduit left join fetch receptionProduit.boutique left join fetch receptionProduit.utilisateur"
    )
    List<ReceptionProduit> findAllWithToOneRelationships();

    @Query(
        "select receptionProduit from ReceptionProduit receptionProduit left join fetch receptionProduit.boutique left join fetch receptionProduit.utilisateur where receptionProduit.id =:id"
    )
    Optional<ReceptionProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
