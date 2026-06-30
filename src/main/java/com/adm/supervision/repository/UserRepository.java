package com.adm.supervision.repository;

import com.adm.supervision.domain.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    Optional<User> findOneByActivationKey(String activationKey);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<User> findOneByResetKey(String resetKey);
    Optional<User> findOneByEmailIgnoreCase(String email);
    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE, unless = "#result == null")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE, unless = "#result == null")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query(
        value = """
        select distinct user
        from User user
        join AffectationUtilisateur affectation on affectation.user.id = user.id
        where affectation.actif = true
            and affectation.boutique.id in :boutiqueIds
            and affectation.dateDebut <= :currentDate
            and (affectation.dateFin is null or affectation.dateFin >= :currentDate)
        """,
        countQuery = """
        select count(distinct user.id)
        from User user
        join AffectationUtilisateur affectation on affectation.user.id = user.id
        where affectation.actif = true
            and affectation.boutique.id in :boutiqueIds
            and affectation.dateDebut <= :currentDate
            and (affectation.dateFin is null or affectation.dateFin >= :currentDate)
        """
    )
    Page<User> findAllManagedByBoutiqueIds(
        @Param("boutiqueIds") Collection<Long> boutiqueIds,
        @Param("currentDate") LocalDate currentDate,
        Pageable pageable
    );

    @Query(
        """
        select count(distinct user.id)
        from User user
        join AffectationUtilisateur affectation on affectation.user.id = user.id
        where lower(user.login) = lower(:login)
            and affectation.actif = true
            and affectation.boutique.id in :boutiqueIds
            and affectation.dateDebut <= :currentDate
            and (affectation.dateFin is null or affectation.dateFin >= :currentDate)
        """
    )
    long countManagedUsersByLoginAndBoutiqueIds(
        @Param("login") String login,
        @Param("boutiqueIds") Collection<Long> boutiqueIds,
        @Param("currentDate") LocalDate currentDate
    );

    @Query(
        """
        select count(distinct user.id)
        from User user
        join AffectationUtilisateur affectation on affectation.user.id = user.id
        where user.id = :userId
            and affectation.actif = true
            and affectation.boutique.id in :boutiqueIds
            and affectation.dateDebut <= :currentDate
            and (affectation.dateFin is null or affectation.dateFin >= :currentDate)
        """
    )
    long countManagedUsersByIdAndBoutiqueIds(
        @Param("userId") Long userId,
        @Param("boutiqueIds") Collection<Long> boutiqueIds,
        @Param("currentDate") LocalDate currentDate
    );
}
