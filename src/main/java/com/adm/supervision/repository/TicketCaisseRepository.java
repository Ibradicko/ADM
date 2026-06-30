package com.adm.supervision.repository;

import com.adm.supervision.domain.TicketCaisse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketCaisse entity.
 */
@Repository
public interface TicketCaisseRepository extends JpaRepository<TicketCaisse, Long>, JpaSpecificationExecutor<TicketCaisse> {
    default Optional<TicketCaisse> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TicketCaisse> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TicketCaisse> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ticketCaisse from TicketCaisse ticketCaisse left join fetch ticketCaisse.vente",
        countQuery = "select count(ticketCaisse) from TicketCaisse ticketCaisse"
    )
    Page<TicketCaisse> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ticketCaisse from TicketCaisse ticketCaisse left join fetch ticketCaisse.vente")
    List<TicketCaisse> findAllWithToOneRelationships();

    @Query("select ticketCaisse from TicketCaisse ticketCaisse left join fetch ticketCaisse.vente where ticketCaisse.id =:id")
    Optional<TicketCaisse> findOneWithToOneRelationships(@Param("id") Long id);
}
