package com.adm.supervision.repository;

import com.adm.supervision.domain.OperationCorrectiveVente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OperationCorrectiveVente entity.
 */
@Repository
public interface OperationCorrectiveVenteRepository
    extends JpaRepository<OperationCorrectiveVente, Long>, JpaSpecificationExecutor<OperationCorrectiveVente>
{
    @Query(
        "select operationCorrectiveVente from OperationCorrectiveVente operationCorrectiveVente where operationCorrectiveVente.utilisateur.login = ?#{authentication.name}"
    )
    List<OperationCorrectiveVente> findByUtilisateurIsCurrentUser();

    default Optional<OperationCorrectiveVente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OperationCorrectiveVente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OperationCorrectiveVente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select operationCorrectiveVente from OperationCorrectiveVente operationCorrectiveVente left join fetch operationCorrectiveVente.vente left join fetch operationCorrectiveVente.utilisateur",
        countQuery = "select count(operationCorrectiveVente) from OperationCorrectiveVente operationCorrectiveVente"
    )
    Page<OperationCorrectiveVente> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select operationCorrectiveVente from OperationCorrectiveVente operationCorrectiveVente left join fetch operationCorrectiveVente.vente left join fetch operationCorrectiveVente.utilisateur"
    )
    List<OperationCorrectiveVente> findAllWithToOneRelationships();

    @Query(
        "select operationCorrectiveVente from OperationCorrectiveVente operationCorrectiveVente left join fetch operationCorrectiveVente.vente left join fetch operationCorrectiveVente.utilisateur where operationCorrectiveVente.id =:id"
    )
    Optional<OperationCorrectiveVente> findOneWithToOneRelationships(@Param("id") Long id);
}
