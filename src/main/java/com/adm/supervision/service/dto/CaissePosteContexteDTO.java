package com.adm.supervision.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contexte complet resolu par le serveur pour le poste de caisse : boutique active, locataire
 * exploitant, articles vendables avec stock agrege et modes de paiement actifs. Le serveur reste
 * la seule autorite sur le perimetre boutique accessible ; le frontend n'a plus besoin de
 * recalculer ce perimetre a partir des affectations.
 */
public class CaissePosteContexteDTO implements Serializable {

    private BoutiqueDTO boutique;

    private List<BoutiqueDTO> boutiquesAccessibles = new ArrayList<>();

    private LocataireDTO locataire;

    private List<CaissePosteArticleDTO> articles = new ArrayList<>();

    private List<ModePaiementRefDTO> modesPaiement = new ArrayList<>();

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public List<BoutiqueDTO> getBoutiquesAccessibles() {
        return boutiquesAccessibles;
    }

    public void setBoutiquesAccessibles(List<BoutiqueDTO> boutiquesAccessibles) {
        this.boutiquesAccessibles = boutiquesAccessibles;
    }

    public LocataireDTO getLocataire() {
        return locataire;
    }

    public void setLocataire(LocataireDTO locataire) {
        this.locataire = locataire;
    }

    public List<CaissePosteArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<CaissePosteArticleDTO> articles) {
        this.articles = articles;
    }

    public List<ModePaiementRefDTO> getModesPaiement() {
        return modesPaiement;
    }

    public void setModesPaiement(List<ModePaiementRefDTO> modesPaiement) {
        this.modesPaiement = modesPaiement;
    }
}
