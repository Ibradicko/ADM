package com.adm.supervision.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaisseVenteResultDTO implements Serializable {

    private VenteDTO vente;

    private TicketCaisseDTO ticket;

    private List<LigneVenteDTO> lignes = new ArrayList<>();

    private List<PaiementVenteDTO> paiements = new ArrayList<>();

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
    }

    public TicketCaisseDTO getTicket() {
        return ticket;
    }

    public void setTicket(TicketCaisseDTO ticket) {
        this.ticket = ticket;
    }

    public List<LigneVenteDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneVenteDTO> lignes) {
        this.lignes = lignes;
    }

    public List<PaiementVenteDTO> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<PaiementVenteDTO> paiements) {
        this.paiements = paiements;
    }
}
