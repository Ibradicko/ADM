package com.adm.supervision.repository;

import com.adm.supervision.domain.InventaireStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventaireStock entity.
 */
@Repository
public interface InventaireStockRepository extends JpaRepository<InventaireStock, Long>, JpaSpecificationExecutor<InventaireStock> {
    @Query("select inventaireStock from InventaireStock inventaireStock where inventaireStock.utilisateur.login = ?#{authentication.name}")
    List<InventaireStock> findByUtilisateurIsCurrentUser();

    default Optional<InventaireStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InventaireStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InventaireStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select inventaireStock from InventaireStock inventaireStock left join fetch inventaireStock.boutique left join fetch inventaireStock.depot left join fetch inventaireStock.utilisateur",
        countQuery = "select count(inventaireStock) from InventaireStock inventaireStock"
    )
    Page<InventaireStock> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select inventaireStock from InventaireStock inventaireStock left join fetch inventaireStock.boutique left join fetch inventaireStock.depot left join fetch inventaireStock.utilisateur"
    )
    List<InventaireStock> findAllWithToOneRelationships();

    @Query(
        "select inventaireStock from InventaireStock inventaireStock left join fetch inventaireStock.boutique left join fetch inventaireStock.depot left join fetch inventaireStock.utilisateur where inventaireStock.id =:id"
    )
    Optional<InventaireStock> findOneWithToOneRelationships(@Param("id") Long id);
}
