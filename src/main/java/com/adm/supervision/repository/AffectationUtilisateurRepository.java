package com.adm.supervision.repository;

import com.adm.supervision.domain.AffectationUtilisateur;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AffectationUtilisateur entity.
 */
@Repository
public interface AffectationUtilisateurRepository
    extends JpaRepository<AffectationUtilisateur, Long>, JpaSpecificationExecutor<AffectationUtilisateur>
{
    @Query(
        "select affectationUtilisateur from AffectationUtilisateur affectationUtilisateur where affectationUtilisateur.user.login = ?#{authentication.name}"
    )
    List<AffectationUtilisateur> findByUserIsCurrentUser();

    boolean existsByUserIdAndBoutiqueIdAndProfilIdAndActifIsTrue(Long userId, Long boutiqueId, Long profilId);

    @Query(
        """
        select count(affectationUtilisateur)
        from AffectationUtilisateur affectationUtilisateur
        where affectationUtilisateur.user.id = :userId
            and affectationUtilisateur.actif = true
            and affectationUtilisateur.profil.code in :profilCodes
            and (:excludedId is null or affectationUtilisateur.id <> :excludedId)
        """
    )
    long countActiveAssignmentsForSingleBoutiqueProfiles(
        @Param("userId") Long userId,
        @Param("profilCodes") List<String> profilCodes,
        @Param("excludedId") Long excludedId
    );

    default Optional<AffectationUtilisateur> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AffectationUtilisateur> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AffectationUtilisateur> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select affectationUtilisateur from AffectationUtilisateur affectationUtilisateur left join fetch affectationUtilisateur.user left join fetch affectationUtilisateur.boutique left join fetch affectationUtilisateur.profil",
        countQuery = "select count(affectationUtilisateur) from AffectationUtilisateur affectationUtilisateur"
    )
    Page<AffectationUtilisateur> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select affectationUtilisateur from AffectationUtilisateur affectationUtilisateur left join fetch affectationUtilisateur.user left join fetch affectationUtilisateur.boutique left join fetch affectationUtilisateur.profil"
    )
    List<AffectationUtilisateur> findAllWithToOneRelationships();

    @Query(
        "select affectationUtilisateur from AffectationUtilisateur affectationUtilisateur left join fetch affectationUtilisateur.user left join fetch affectationUtilisateur.boutique left join fetch affectationUtilisateur.profil where affectationUtilisateur.id =:id"
    )
    Optional<AffectationUtilisateur> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        """
        select distinct affectationUtilisateur
        from AffectationUtilisateur affectationUtilisateur
        left join fetch affectationUtilisateur.user
        left join fetch affectationUtilisateur.boutique
        left join fetch affectationUtilisateur.profil profil
        left join fetch profil.permissionses permissions
        where affectationUtilisateur.user.login = :login
            and affectationUtilisateur.actif = true
            and affectationUtilisateur.dateDebut <= :currentDate
            and (affectationUtilisateur.dateFin is null or affectationUtilisateur.dateFin >= :currentDate)
        """
    )
    List<AffectationUtilisateur> findActiveAssignmentsForSecurity(
        @Param("login") String login,
        @Param("currentDate") LocalDate currentDate
    );
}
