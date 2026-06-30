package com.adm.supervision.repository;

import com.adm.supervision.domain.ProfilMetier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProfilMetierRepositoryWithBagRelationships {
    Optional<ProfilMetier> fetchBagRelationships(Optional<ProfilMetier> profilMetier);

    List<ProfilMetier> fetchBagRelationships(List<ProfilMetier> profilMetiers);

    Page<ProfilMetier> fetchBagRelationships(Page<ProfilMetier> profilMetiers);
}
