package com.adm.supervision.service;

import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.HistoriqueCodeBarres;
import com.adm.supervision.domain.ParametreCodeBarres;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.repository.HistoriqueCodeBarresRepository;
import com.adm.supervision.repository.ParametreCodeBarresRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.security.SecurityUtils;
import com.adm.supervision.service.dto.CodeBarresProduitDTO;
import com.adm.supervision.service.mapper.CodeBarresProduitMapper;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.CodeBarresProduit}.
 */
@Service
@Transactional
public class CodeBarresProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(CodeBarresProduitService.class);

    private final CodeBarresProduitRepository codeBarresProduitRepository;

    private final ProduitRepository produitRepository;

    private final ParametreCodeBarresRepository parametreCodeBarresRepository;

    private final HistoriqueCodeBarresRepository historiqueCodeBarresRepository;

    private final UserRepository userRepository;

    private final CodeBarresProduitMapper codeBarresProduitMapper;

    public CodeBarresProduitService(
        CodeBarresProduitRepository codeBarresProduitRepository,
        ProduitRepository produitRepository,
        ParametreCodeBarresRepository parametreCodeBarresRepository,
        HistoriqueCodeBarresRepository historiqueCodeBarresRepository,
        UserRepository userRepository,
        CodeBarresProduitMapper codeBarresProduitMapper
    ) {
        this.codeBarresProduitRepository = codeBarresProduitRepository;
        this.produitRepository = produitRepository;
        this.parametreCodeBarresRepository = parametreCodeBarresRepository;
        this.historiqueCodeBarresRepository = historiqueCodeBarresRepository;
        this.userRepository = userRepository;
        this.codeBarresProduitMapper = codeBarresProduitMapper;
    }

    /**
     * Save a codeBarresProduit.
     *
     * @param codeBarresProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public CodeBarresProduitDTO save(CodeBarresProduitDTO codeBarresProduitDTO) {
        LOG.debug("Request to save CodeBarresProduit : {}", codeBarresProduitDTO);
        CodeBarresProduit codeBarresProduit = codeBarresProduitMapper.toEntity(codeBarresProduitDTO);
        normalize(codeBarresProduit);
        validateActiveBarcodeUniqueness(codeBarresProduit);
        codeBarresProduit = codeBarresProduitRepository.save(codeBarresProduit);
        return codeBarresProduitMapper.toDto(codeBarresProduit);
    }

    /**
     * Update a codeBarresProduit.
     *
     * @param codeBarresProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public CodeBarresProduitDTO update(CodeBarresProduitDTO codeBarresProduitDTO) {
        LOG.debug("Request to update CodeBarresProduit : {}", codeBarresProduitDTO);
        CodeBarresProduit previous = codeBarresProduitRepository
            .findById(codeBarresProduitDTO.getId())
            .orElseThrow(() -> new BusinessValidationException("codeBarresProduit", "notFound", "Code-barres introuvable"));
        String previousCode = previous.getCode();
        CodeBarresProduit codeBarresProduit = codeBarresProduitMapper.toEntity(codeBarresProduitDTO);
        normalize(codeBarresProduit);
        validateActiveBarcodeUniqueness(codeBarresProduit);
        codeBarresProduit = codeBarresProduitRepository.save(codeBarresProduit);
        recordRecoding(previousCode, codeBarresProduit);
        return codeBarresProduitMapper.toDto(codeBarresProduit);
    }

    /**
     * Partially update a codeBarresProduit.
     *
     * @param codeBarresProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CodeBarresProduitDTO> partialUpdate(CodeBarresProduitDTO codeBarresProduitDTO) {
        LOG.debug("Request to partially update CodeBarresProduit : {}", codeBarresProduitDTO);

        return codeBarresProduitRepository
            .findById(codeBarresProduitDTO.getId())
            .map(existingCodeBarresProduit -> {
                String previousCode = existingCodeBarresProduit.getCode();
                codeBarresProduitMapper.partialUpdate(existingCodeBarresProduit, codeBarresProduitDTO);
                normalize(existingCodeBarresProduit);
                validateActiveBarcodeUniqueness(existingCodeBarresProduit);
                recordRecoding(previousCode, existingCodeBarresProduit);
                return existingCodeBarresProduit;
            })
            .map(codeBarresProduitRepository::save)
            .map(codeBarresProduitMapper::toDto);
    }

    /**
     * Get all the codeBarresProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CodeBarresProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return codeBarresProduitRepository.findAllWithEagerRelationships(pageable).map(codeBarresProduitMapper::toDto);
    }

    /**
     * Get one codeBarresProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CodeBarresProduitDTO> findOne(Long id) {
        LOG.debug("Request to get CodeBarresProduit : {}", id);
        return codeBarresProduitRepository.findOneWithEagerRelationships(id).map(codeBarresProduitMapper::toDto);
    }

    /**
     * Delete the codeBarresProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CodeBarresProduit : {}", id);
        codeBarresProduitRepository.deleteById(id);
    }

    public CodeBarresProduitDTO generate(Long produitId) {
        Produit produit = produitRepository
            .findOneWithToOneRelationships(produitId)
            .orElseThrow(() -> new BusinessValidationException("produit", "notFound", "Produit introuvable"));
        ParametreCodeBarres parameter = parametreCodeBarresRepository.findFirstByActifTrueOrderByIdAsc().orElse(null);
        TypeCodeBarres type = parameter == null ? TypeCodeBarres.EAN13 : parameter.getFormatParDefaut();
        int length = parameter != null && parameter.getLongueur() != null ? parameter.getLongueur() : defaultLength(type);
        String prefix = parameter == null || parameter.getPrefixe() == null ? "" : parameter.getPrefixe().replaceAll("\\D", "");
        String code = generateUniqueCode(prefix, length, type, produit.getBoutique().getId());

        CodeBarresProduit barcode = new CodeBarresProduit()
            .code(code)
            .type(type)
            .principal(true)
            .genereParSysteme(true)
            .actif(true)
            .dateAffectation(Instant.now())
            .produit(produit);
        validateActiveBarcodeUniqueness(barcode);
        return codeBarresProduitMapper.toDto(codeBarresProduitRepository.save(barcode));
    }

    private void validateActiveBarcodeUniqueness(CodeBarresProduit codeBarresProduit) {
        if (
            !Boolean.TRUE.equals(codeBarresProduit.getActif()) ||
            codeBarresProduit.getCode() == null ||
            codeBarresProduit.getCode().isBlank() ||
            codeBarresProduit.getProduit() == null ||
            codeBarresProduit.getProduit().getId() == null
        ) {
            return;
        }

        Produit produit = produitRepository
            .findOneWithToOneRelationships(codeBarresProduit.getProduit().getId())
            .orElseThrow(() -> new BusinessValidationException("produit", "notFound", "Produit introuvable pour ce code-barres"));
        Long boutiqueId = produit.getBoutique().getId();

        List<CodeBarresProduit> activeBarcodes = codeBarresProduitRepository.findAllActiveByCodeAndBoutiqueId(
            codeBarresProduit.getCode(),
            boutiqueId
        );
        boolean duplicateExists = activeBarcodes
            .stream()
            .anyMatch(existing -> !Objects.equals(existing.getId(), codeBarresProduit.getId()));
        if (duplicateExists) {
            throw new BusinessValidationException(
                "codeBarresProduit",
                "duplicateActiveBarcode",
                "Un code-barres actif identique existe deja dans cette boutique"
            );
        }

        if (Boolean.TRUE.equals(codeBarresProduit.getPrincipal())) {
            boolean anotherPrincipalExists = codeBarresProduitRepository
                .findAllActivePrincipalByProduitId(codeBarresProduit.getProduit().getId())
                .stream()
                .anyMatch(existing -> !Objects.equals(existing.getId(), codeBarresProduit.getId()));
            if (anotherPrincipalExists) {
                throw new BusinessValidationException(
                    "codeBarresProduit",
                    "duplicateActivePrimaryBarcode",
                    "Un seul code-barres principal actif est autorise pour ce produit dans la boutique"
                );
            }
        }
    }

    private void normalize(CodeBarresProduit barcode) {
        if (barcode.getCode() != null) {
            barcode.setCode(barcode.getCode().trim());
        }
        if (barcode.getDateAffectation() == null) {
            barcode.setDateAffectation(Instant.now());
        }
    }

    private void recordRecoding(String previousCode, CodeBarresProduit barcode) {
        if (Objects.equals(previousCode, barcode.getCode())) {
            return;
        }
        Produit produit = produitRepository
            .findById(barcode.getProduit().getId())
            .orElseThrow(() -> new BusinessValidationException("produit", "notFound", "Produit introuvable"));
        HistoriqueCodeBarres history = new HistoriqueCodeBarres()
            .ancienCode(previousCode)
            .nouveauCode(barcode.getCode())
            .motif("Recodification du code-barres produit")
            .dateChangement(Instant.now())
            .produit(produit);
        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(history::setUtilisateur);
        historiqueCodeBarresRepository.save(history);
    }

    private String generateUniqueCode(String prefix, int requestedLength, TypeCodeBarres type, Long boutiqueId) {
        int length = Math.max(requestedLength, prefix.length() + 4);
        for (int attempt = 0; attempt < 100; attempt++) {
            String seed = Long.toString(System.currentTimeMillis()) + String.format("%02d", attempt);
            int bodyLength = type == TypeCodeBarres.EAN13 ? 12 : length;
            String body = (prefix + seed);
            body = body.substring(Math.max(0, body.length() - bodyLength));
            body = "0".repeat(Math.max(0, bodyLength - body.length())) + body;
            String candidate = type == TypeCodeBarres.EAN13 ? body + ean13CheckDigit(body) : body;
            if (codeBarresProduitRepository.findAllActiveByCodeAndBoutiqueId(candidate, boutiqueId).isEmpty()) {
                return candidate;
            }
        }
        throw new BusinessValidationException("codeBarresProduit", "generationFailed", "Impossible de generer un code-barres unique");
    }

    private int defaultLength(TypeCodeBarres type) {
        return type == TypeCodeBarres.EAN8 ? 8 : 13;
    }

    private int ean13CheckDigit(String twelveDigits) {
        int sum = 0;
        for (int index = 0; index < twelveDigits.length(); index++) {
            int digit = Character.digit(twelveDigits.charAt(index), 10);
            sum += index % 2 == 0 ? digit : digit * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
}
