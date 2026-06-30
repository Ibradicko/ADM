package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.service.dto.BarcodeScanRequest;
import com.adm.supervision.service.dto.BarcodeScanResultDTO;
import com.adm.supervision.service.mapper.ProduitMapper;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BarcodeScanService {

    private final CodeBarresProduitRepository codeBarresProduitRepository;
    private final ScanInconnuRepository scanInconnuRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final BusinessAuthorizationService businessAuthorizationService;
    private final ProduitMapper produitMapper;

    public BarcodeScanService(
        CodeBarresProduitRepository codeBarresProduitRepository,
        ScanInconnuRepository scanInconnuRepository,
        BoutiqueRepository boutiqueRepository,
        BusinessAuthorizationService businessAuthorizationService,
        ProduitMapper produitMapper
    ) {
        this.codeBarresProduitRepository = codeBarresProduitRepository;
        this.scanInconnuRepository = scanInconnuRepository;
        this.boutiqueRepository = boutiqueRepository;
        this.businessAuthorizationService = businessAuthorizationService;
        this.produitMapper = produitMapper;
    }

    public BarcodeScanResultDTO scan(BarcodeScanRequest request) {
        Long boutiqueId = request.getBoutiqueId();
        if (!businessAuthorizationService.canAccessBoutique(boutiqueId)) {
            throw new BusinessValidationException("boutique", "accessDenied", "Acces refuse a cette boutique");
        }

        String code = request.getCode().trim();
        List<CodeBarresProduit> matches = codeBarresProduitRepository.findAllActiveByCodeAndBoutiqueId(code, boutiqueId);
        if (matches.size() > 1) {
            throw new BusinessValidationException(
                "codeBarresProduit",
                "ambiguousBarcode",
                "Plusieurs produits actifs partagent ce code-barres dans cette boutique"
            );
        }

        BarcodeScanResultDTO result = new BarcodeScanResultDTO();
        result.setAffectationAutorisee(businessAuthorizationService.canAssignBarcode());
        if (matches.size() == 1) {
            result.setTrouve(true);
            result.setMessage("Produit trouve");
            result.setProduit(produitMapper.toDto(matches.get(0).getProduit()));
            return result;
        }

        Boutique boutique = boutiqueRepository
            .findById(boutiqueId)
            .orElseThrow(() -> new BusinessValidationException("boutique", "notFound", "Boutique introuvable"));
        ScanInconnu scanInconnu = new ScanInconnu()
            .codeScanne(code)
            .ecranOrigine(request.getEcranOrigine() == null ? "SCAN" : request.getEcranOrigine().trim())
            .dateScan(Instant.now())
            .commentaire("Code-barres inconnu")
            .resolu(false)
            .boutique(boutique);
        scanInconnu = scanInconnuRepository.save(scanInconnu);

        result.setTrouve(false);
        result.setMessage(
            result.isAffectationAutorisee()
                ? "Code-barres inconnu. Vous pouvez l'affecter a un produit."
                : "Code-barres inconnu. Contactez un responsable autorise pour l'affecter."
        );
        result.setScanInconnuId(scanInconnu.getId());
        return result;
    }
}
