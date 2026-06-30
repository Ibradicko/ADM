package com.adm.supervision.repository;

import com.adm.supervision.domain.EtiquetteProduit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EtiquetteProduit entity.
 */
@Repository
public interface EtiquetteProduitRepository extends JpaRepository<EtiquetteProduit, Long>, JpaSpecificationExecutor<EtiquetteProduit> {
    default Optional<EtiquetteProduit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EtiquetteProduit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EtiquetteProduit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select etiquetteProduit from EtiquetteProduit etiquetteProduit left join fetch etiquetteProduit.produit left join fetch etiquetteProduit.lot",
        countQuery = "select count(etiquetteProduit) from EtiquetteProduit etiquetteProduit"
    )
    Page<EtiquetteProduit> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select etiquetteProduit from EtiquetteProduit etiquetteProduit left join fetch etiquetteProduit.produit left join fetch etiquetteProduit.lot"
    )
    List<EtiquetteProduit> findAllWithToOneRelationships();

    @Query(
        "select etiquetteProduit from EtiquetteProduit etiquetteProduit left join fetch etiquetteProduit.produit left join fetch etiquetteProduit.lot where etiquetteProduit.id =:id"
    )
    Optional<EtiquetteProduit> findOneWithToOneRelationships(@Param("id") Long id);
}
