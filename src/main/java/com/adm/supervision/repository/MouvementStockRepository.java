package com.adm.supervision.repository;

import com.adm.supervision.domain.MouvementStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MouvementStock entity.
 */
@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long>, JpaSpecificationExecutor<MouvementStock> {
    @Query("select mouvementStock from MouvementStock mouvementStock where mouvementStock.utilisateur.login = ?#{authentication.name}")
    List<MouvementStock> findByUtilisateurIsCurrentUser();

    boolean existsByReference(String reference);

    Optional<MouvementStock> findByReference(String reference);

    default Optional<MouvementStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MouvementStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MouvementStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select mouvementStock from MouvementStock mouvementStock left join fetch mouvementStock.boutique left join fetch mouvementStock.utilisateur",
        countQuery = "select count(mouvementStock) from MouvementStock mouvementStock"
    )
    Page<MouvementStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select mouvementStock from MouvementStock mouvementStock left join fetch mouvementStock.boutique left join fetch mouvementStock.utilisateur"
    )
    List<MouvementStock> findAllWithToOneRelationships();

    @Query(
        "select mouvementStock from MouvementStock mouvementStock left join fetch mouvementStock.boutique left join fetch mouvementStock.utilisateur where mouvementStock.id =:id"
    )
    Optional<MouvementStock> findOneWithToOneRelationships(@Param("id") Long id);
}
