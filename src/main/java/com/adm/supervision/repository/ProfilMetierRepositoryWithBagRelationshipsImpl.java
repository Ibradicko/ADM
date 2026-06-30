package com.adm.supervision.repository;

import com.adm.supervision.domain.ProfilMetier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProfilMetierRepositoryWithBagRelationshipsImpl implements ProfilMetierRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROFILMETIERS_PARAMETER = "profilMetiers";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ProfilMetier> fetchBagRelationships(Optional<ProfilMetier> profilMetier) {
        return profilMetier.map(this::fetchPermissionses);
    }

    @Override
    public Page<ProfilMetier> fetchBagRelationships(Page<ProfilMetier> profilMetiers) {
        return new PageImpl<>(
            fetchBagRelationships(profilMetiers.getContent()),
            profilMetiers.getPageable(),
            profilMetiers.getTotalElements()
        );
    }

    @Override
    public List<ProfilMetier> fetchBagRelationships(List<ProfilMetier> profilMetiers) {
        return Optional.of(profilMetiers).map(this::fetchPermissionses).orElse(Collections.emptyList());
    }

    ProfilMetier fetchPermissionses(ProfilMetier result) {
        return entityManager
            .createQuery(
                "select profilMetier from ProfilMetier profilMetier left join fetch profilMetier.permissionses where profilMetier.id = :id",
                ProfilMetier.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProfilMetier> fetchPermissionses(List<ProfilMetier> profilMetiers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, profilMetiers.size()).forEach(index -> order.put(profilMetiers.get(index).getId(), index));
        List<ProfilMetier> result = entityManager
            .createQuery(
                "select profilMetier from ProfilMetier profilMetier left join fetch profilMetier.permissionses where profilMetier in :profilMetiers",
                ProfilMetier.class
            )
            .setParameter(PROFILMETIERS_PARAMETER, profilMetiers)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
