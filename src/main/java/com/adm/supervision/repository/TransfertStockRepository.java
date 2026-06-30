package com.adm.supervision.repository;

import com.adm.supervision.domain.TransfertStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransfertStock entity.
 */
@Repository
public interface TransfertStockRepository extends JpaRepository<TransfertStock, Long>, JpaSpecificationExecutor<TransfertStock> {
    @Query("select transfertStock from TransfertStock transfertStock where transfertStock.utilisateur.login = ?#{authentication.name}")
    List<TransfertStock> findByUtilisateurIsCurrentUser();

    default Optional<TransfertStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TransfertStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TransfertStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select transfertStock from TransfertStock transfertStock left join fetch transfertStock.boutiqueOrigine left join fetch transfertStock.boutiqueDestination left join fetch transfertStock.utilisateur",
        countQuery = "select count(transfertStock) from TransfertStock transfertStock"
    )
    Page<TransfertStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select transfertStock from TransfertStock transfertStock left join fetch transfertStock.boutiqueOrigine left join fetch transfertStock.boutiqueDestination left join fetch transfertStock.utilisateur"
    )
    List<TransfertStock> findAllWithToOneRelationships();

    @Query(
        "select transfertStock from TransfertStock transfertStock left join fetch transfertStock.boutiqueOrigine left join fetch transfertStock.boutiqueDestination left join fetch transfertStock.utilisateur where transfertStock.id =:id"
    )
    Optional<TransfertStock> findOneWithToOneRelationships(@Param("id") Long id);
}
