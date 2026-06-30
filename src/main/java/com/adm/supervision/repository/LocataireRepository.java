package com.adm.supervision.repository;

import com.adm.supervision.domain.Locataire;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Locataire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocataireRepository extends JpaRepository<Locataire, Long>, JpaSpecificationExecutor<Locataire> {
    @Query("SELECT l FROM Locataire l LEFT JOIN FETCH l.user WHERE l.id = :id")
    Optional<Locataire> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT l FROM Locataire l WHERE l.user.login = :login")
    Optional<Locataire> findOneByUserLogin(@Param("login") String login);
}
