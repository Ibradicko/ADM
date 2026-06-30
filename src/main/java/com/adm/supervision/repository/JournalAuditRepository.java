package com.adm.supervision.repository;

import com.adm.supervision.domain.JournalAudit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the JournalAudit entity.
 */
@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long>, JpaSpecificationExecutor<JournalAudit> {
    @Query("select journalAudit from JournalAudit journalAudit where journalAudit.utilisateur.login = ?#{authentication.name}")
    List<JournalAudit> findByUtilisateurIsCurrentUser();

    default Optional<JournalAudit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<JournalAudit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<JournalAudit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select journalAudit from JournalAudit journalAudit left join fetch journalAudit.boutique left join fetch journalAudit.utilisateur",
        countQuery = "select count(journalAudit) from JournalAudit journalAudit"
    )
    Page<JournalAudit> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select journalAudit from JournalAudit journalAudit left join fetch journalAudit.boutique left join fetch journalAudit.utilisateur"
    )
    List<JournalAudit> findAllWithToOneRelationships();

    @Query(
        "select journalAudit from JournalAudit journalAudit left join fetch journalAudit.boutique left join fetch journalAudit.utilisateur where journalAudit.id =:id"
    )
    Optional<JournalAudit> findOneWithToOneRelationships(@Param("id") Long id);
}
