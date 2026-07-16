import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { CodeBarresProduitService } from 'app/entities/code-barres-produit/service/code-barres-produit.service';
import { ILigneVente } from 'app/entities/ligne-vente/ligne-vente.model';
import { LigneVenteService } from 'app/entities/ligne-vente/service/ligne-vente.service';
import {
  IOperationCorrectiveVente,
  NewOperationCorrectiveVente,
} from 'app/entities/operation-corrective-vente/operation-corrective-vente.model';
import { OperationCorrectiveVenteService } from 'app/entities/operation-corrective-vente/service/operation-corrective-vente.service';
import { IPaiementVente } from 'app/entities/paiement-vente/paiement-vente.model';
import { PaiementVenteService } from 'app/entities/paiement-vente/service/paiement-vente.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { TypeOperationCorrective } from 'app/entities/enumerations/type-operation-corrective.model';
import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { ITicketCaisse } from 'app/entities/ticket-caisse/ticket-caisse.model';
import { TicketCaisseService } from 'app/entities/ticket-caisse/service/ticket-caisse.service';
import { IVente } from 'app/entities/vente/vente.model';
import { CaissePosteArticle, CaissePosteContexte, VenteService } from 'app/entities/vente/service/vente.service';
import { TranslateDirective } from 'app/shared/language';

interface CaisseLignePanier {
  article: CaissePosteArticle;
  quantite: number;
  remise: number;
  typePrix: keyof typeof TypePrix;
  prixUnitaire: number;
  codeBarresScanne?: string | null;
}

interface CaissePaiementSaisie {
  uid: number;
  modePaiementId: number | null;
  montant: number | null;
  reference: string;
}

interface MessageCaisse {
  type: 'success' | 'danger' | 'info';
  key: string;
  params?: Record<string, unknown>;
}

interface CategorieCaisse {
  id: number;
  libelle: string;
}

type ActionHistorique = 'annulation' | 'retour' | 'reimpression' | null;
type ModalCaisse = 'validation-ticket' | 'operation-historique' | 'apercu-ticket' | null;
type OperationHistoriqueModal = 'ANNULATION' | 'RETOUR' | null;
type OngletCaisse = 'caisse' | 'historique';

@Component({
  selector: 'jhi-caisse',
  templateUrl: './caisse.html',
  styleUrl: './caisse.scss',
  imports: [FormsModule, RouterLink, FontAwesomeModule, TranslateDirective, TranslateModule],
})
export default class Caisse implements OnInit {
  readonly account = inject(AccountService).account;
  readonly permissionsUi = inject(UiPermissionService);

  readonly contexte = signal<CaissePosteContexte | null>(null);
  readonly ventesHistorique = signal<IVente[]>([]);
  readonly panier = signal<CaisseLignePanier[]>([]);
  readonly paiements = signal<CaissePaiementSaisie[]>([{ uid: 1, modePaiementId: null, montant: null, reference: '' }]);
  readonly scanInput = signal('');
  readonly rechercheProduit = signal('');
  readonly referencePassager = signal('');
  readonly referenceCarte = signal('');
  readonly commentaire = signal('');
  readonly ticketGenere = signal<ITicketCaisse | null>(null);
  readonly venteGeneree = signal<IVente | null>(null);
  readonly chargement = signal(false);
  readonly chargementArticles = signal(false);
  readonly enregistrement = signal(false);
  readonly message = signal<MessageCaisse | null>(null);
  readonly scanInconnuAffectable = signal(false);
  readonly activeTab = signal<OngletCaisse>('caisse');
  readonly categorieActive = signal<number | null>(null);
  readonly chargementPermissions = signal(true);
  readonly typePrixActif = signal<keyof typeof TypePrix>(TypePrix.STANDARD);
  readonly typesPrix = Object.values(TypePrix);

  readonly rechercheHistorique = signal('');
  readonly statutHistorique = signal<string>('TOUS');
  readonly venteSelectionneeId = signal<number | null>(null);
  readonly lignesVenteSelectionnee = signal<ILigneVente[]>([]);
  readonly paiementsVenteSelectionnee = signal<IPaiementVente[]>([]);
  readonly ticketVenteSelectionnee = signal<ITicketCaisse | null>(null);
  readonly operationsVenteSelectionnee = signal<IOperationCorrectiveVente[]>([]);
  readonly actionHistorique = signal<ActionHistorique>(null);
  readonly modalActif = signal<ModalCaisse>(null);
  readonly operationHistoriqueModal = signal<OperationHistoriqueModal>(null);
  readonly motifOperationHistorique = signal('');

  readonly compteSansIdentifiant = computed(() => !this.account()?.id);
  readonly boutique = computed(() => this.contexte()?.boutique ?? null);
  readonly boutiquesAccessibles = computed(() => this.contexte()?.boutiquesAccessibles ?? []);
  readonly locataire = computed(() => this.contexte()?.locataire ?? null);
  readonly modesPaiementActifs = computed(() => (this.contexte()?.modesPaiement ?? []).filter(mode => mode.actif !== false));
  readonly articlesDisponibles = computed(() => this.contexte()?.articles ?? []);
  readonly aucunArticleConfigure = computed(
    () => !this.chargementArticles() && !!this.boutique() && this.articlesDisponibles().length === 0,
  );
  readonly articlesEnStock = computed(() => this.articlesDisponibles().filter(article => (article.stockDisponible ?? 0) > 0));
  readonly categories = computed<CategorieCaisse[]>(() => {
    const vues = new Map<number, string>();
    for (const article of this.articlesDisponibles()) {
      if (article.groupeArticleId && !vues.has(article.groupeArticleId)) {
        vues.set(article.groupeArticleId, article.groupeArticleLibelle ?? '--');
      }
    }
    return [...vues.entries()].map(([id, libelle]) => ({ id, libelle })).sort((left, right) => left.libelle.localeCompare(right.libelle));
  });
  readonly produitsGrille = computed(() => {
    const recherche = this.rechercheProduit().trim().toLowerCase();
    const categorie = this.categorieActive();

    return this.articlesDisponibles().filter(article => {
      if (categorie && article.groupeArticleId !== categorie) {
        return false;
      }
      if (!recherche) {
        return true;
      }
      return [article.codeInterne, article.designation, article.description, article.groupeArticleLibelle]
        .filter(Boolean)
        .some(value => value!.toLowerCase().includes(recherche));
    });
  });
  readonly sousTotal = computed(() => this.panier().reduce((total, ligne) => total + ligne.prixUnitaire * ligne.quantite, 0));
  readonly totalRemise = computed(() => this.panier().reduce((total, ligne) => total + ligne.remise, 0));
  readonly totalNet = computed(() => this.arrondirMontant(this.sousTotal() - this.totalRemise()));
  readonly totalPaiements = computed(() =>
    this.arrondirMontant(
      this.paiements().reduce((total, paiement) => total + (paiement.montant && paiement.montant > 0 ? paiement.montant : 0), 0),
    ),
  );
  readonly resteAPayer = computed(() => this.arrondirMontant(this.totalNet() - this.totalPaiements()));
  readonly peutValider = computed(
    () =>
      !!this.boutique() &&
      !!this.locataire() &&
      !this.compteSansIdentifiant() &&
      this.panier().length > 0 &&
      this.panierStockValide() &&
      this.totalNet() > 0 &&
      this.totalPaiements() > 0 &&
      this.resteAPayer() === 0,
  );
  readonly ventesHistoriqueFiltrees = computed(() => {
    const recherche = this.rechercheHistorique().trim().toLowerCase();
    const statut = this.statutHistorique();

    return this.ventesHistorique().filter(vente => {
      if (statut !== 'TOUS' && vente.statut !== statut) {
        return false;
      }

      if (!recherche) {
        return true;
      }

      return [vente.numeroTicket, vente.boutique?.nom, vente.locataire?.nom, vente.vendeur?.login]
        .filter(Boolean)
        .some(valeur => valeur!.toLowerCase().includes(recherche));
    });
  });
  readonly venteSelectionnee = computed(() => this.ventesHistorique().find(vente => vente.id === this.venteSelectionneeId()) ?? null);
  readonly peutAnnulerSelection = computed(() => this.venteSelectionnee()?.statut === 'VALIDEE');
  readonly peutRetourSelection = computed(() => this.venteSelectionnee()?.statut === 'VALIDEE');
  readonly estVendeurPoste = computed(
    () =>
      this.permissionsUi.estProfilVente() &&
      !this.permissionsUi.estAdmin() &&
      !this.permissionsUi.estProfilAdm() &&
      !this.permissionsUi.estProfilBoutique(),
  );
  readonly peutVoirHistoriqueSav = computed(() => !this.estVendeurPoste());
  readonly peutVoirHistoriqueVentes = computed(() => this.permissionsUi.peutLireVentes());
  readonly peutGererSav = computed(() => this.permissionsUi.peutGererVentes() && !this.estVendeurPoste());

  private readonly accountService = inject(AccountService);
  private readonly codeBarresProduitService = inject(CodeBarresProduitService);
  private readonly venteService = inject(VenteService);
  private readonly ligneVenteService = inject(LigneVenteService);
  private readonly paiementVenteService = inject(PaiementVenteService);
  private readonly ticketCaisseService = inject(TicketCaisseService);
  private readonly operationCorrectiveVenteService = inject(OperationCorrectiveVenteService);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    void this.initialiserPosteCaisse();
  }

  private async initialiserPosteCaisse(): Promise<void> {
    const compte = this.account() ?? (await firstValueFrom(this.accountService.identity()));
    await this.permissionsUi.chargerPermissions(compte);
    this.chargementPermissions.set(false);
    await this.initialiser();
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString(this.localeCourante())} F CFA` : '--';
  }

  formatValeur(valeur: string | null | undefined): string {
    return valeur?.trim() ? valeur : '--';
  }

  imageArticleSrc(article: CaissePosteArticle): string | null {
    return article.image && article.imageContentType
      ? `data:${article.imageContentType};base64,${article.image}`
      : 'content/images/default-article.svg';
  }

  selectionnerBoutique(boutiqueId: number | null): void {
    if (!boutiqueId || boutiqueId === this.boutique()?.id) {
      return;
    }
    void this.chargerContexte(boutiqueId);
  }

  async rafraichirArticles(): Promise<void> {
    this.message.set(null);
    await this.chargerContexte(this.boutique()?.id ?? null);
  }

  stockDisponible(produitId: number): number {
    return this.articlesDisponibles().find(article => article.produitId === produitId)?.stockDisponible ?? 0;
  }

  quantiteDansPanier(produitId: number): number {
    return this.panier()
      .filter(ligne => ligne.article.produitId === produitId)
      .reduce((total, ligne) => total + ligne.quantite, 0);
  }

  peutAjouterProduit(article: CaissePosteArticle): boolean {
    return this.stockDisponible(article.produitId) > this.quantiteDansPanier(article.produitId);
  }

  prixArticle(article: CaissePosteArticle, typePrix: keyof typeof TypePrix = this.typePrixActif()): number {
    const tarif = article.tarifsParType?.[typePrix];
    return typeof tarif === 'number' ? tarif : (article.prixVente ?? 0);
  }

  libelleTypePrix(typePrix: keyof typeof TypePrix): string {
    return this.translateService.instant(`admSupervisionVentesApp.TypePrix.${typePrix}`) as string;
  }

  selectionnerTypePrix(typePrix: string): void {
    if (this.typesPrix.includes(typePrix as TypePrix)) {
      this.typePrixActif.set(typePrix as keyof typeof TypePrix);
    }
  }

  changerTypePrixLigne(produitId: number, ancienTypePrix: keyof typeof TypePrix, nouveauTypePrix: string): void {
    if (!this.typesPrix.includes(nouveauTypePrix as TypePrix)) {
      return;
    }

    const typePrix = nouveauTypePrix as keyof typeof TypePrix;
    this.panier.update(panier =>
      panier.map(ligne =>
        ligne.article.produitId === produitId && ligne.typePrix === ancienTypePrix
          ? {
              ...ligne,
              typePrix,
              prixUnitaire: this.prixArticle(ligne.article, typePrix),
            }
          : ligne,
      ),
    );
    this.ajusterPaiementUniqueAuTotal();
  }

  solderPaiement(uid: number): void {
    const autresPaiements = this.paiements()
      .filter(paiement => paiement.uid !== uid)
      .reduce((total, paiement) => total + (paiement.montant ?? 0), 0);
    this.mettreAJourPaiement(uid, 'montant', Math.max(0, this.arrondirMontant(this.totalNet() - autresPaiements)));
  }

  async traiterScan(): Promise<void> {
    const code = this.scanInput().trim();
    this.message.set(null);
    this.scanInconnuAffectable.set(false);
    if (!code) {
      return;
    }

    if (!this.boutique()) {
      this.message.set({
        type: 'info',
        key: 'caisse.messages.selectShopBeforeScan',
      });
      return;
    }

    const article = await this.resoudreProduitScanne(code);
    if (article) {
      this.ajouterArticleAuPanier(article, code);
      this.scanInput.set('');
      this.rechercheProduit.set('');
      this.message.set({
        type: 'success',
        key: 'caisse.messages.productAdded',
        params: { product: this.formatValeur(article.designation) },
      });
    }
  }

  ajouterProduitDepuisRecherche(article: CaissePosteArticle): void {
    if (!this.peutAjouterProduit(article)) {
      this.message.set({
        type: 'info',
        key: 'caisse.messages.productOutOfStock',
        params: { product: this.formatValeur(article.designation) },
      });
      return;
    }
    this.ajouterArticleAuPanier(article);
    this.rechercheProduit.set('');
    this.message.set({
      type: 'success',
      key: 'caisse.messages.productAdded',
      params: { product: this.formatValeur(article.designation) },
    });
  }

  incrementerQuantite(produitId: number, typePrix?: keyof typeof TypePrix): void {
    const ligne = this.panier().find(item => item.article.produitId === produitId && (!typePrix || item.typePrix === typePrix));
    if (ligne && !this.peutAjouterProduit(ligne.article)) {
      this.message.set({
        type: 'info',
        key: 'caisse.messages.stockLimitReached',
        params: { product: this.formatValeur(ligne.article.designation), stock: this.stockDisponible(produitId) },
      });
      return;
    }
    this.panier.update(panier =>
      panier.map(lignePanier =>
        lignePanier.article.produitId === produitId && (!typePrix || lignePanier.typePrix === typePrix)
          ? { ...lignePanier, quantite: lignePanier.quantite + 1 }
          : lignePanier,
      ),
    );
    this.ajusterPaiementUniqueAuTotal();
  }

  decrementerQuantite(produitId: number, typePrix?: keyof typeof TypePrix): void {
    this.panier.update(panier =>
      panier
        .map(ligne =>
          ligne.article.produitId === produitId && (!typePrix || ligne.typePrix === typePrix)
            ? { ...ligne, quantite: Math.max(0, ligne.quantite - 1) }
            : ligne,
        )
        .filter(ligne => ligne.quantite > 0),
    );
    this.ajusterPaiementUniqueAuTotal();
  }

  retirerProduit(produitId: number, typePrix?: keyof typeof TypePrix): void {
    this.panier.update(panier =>
      panier.filter(ligne => ligne.article.produitId !== produitId || (typePrix && ligne.typePrix !== typePrix)),
    );
    this.ajusterPaiementUniqueAuTotal();
  }

  ajouterPaiement(): void {
    const prochainUid = Math.max(...this.paiements().map(paiement => paiement.uid), 0) + 1;
    this.paiements.update(paiements => [
      ...paiements,
      {
        uid: prochainUid,
        modePaiementId: this.modesPaiementActifs()[0]?.id ?? null,
        montant: null,
        reference: '',
      },
    ]);
  }

  supprimerPaiement(uid: number): void {
    if (this.paiements().length === 1) {
      this.paiements.set([{ uid: 1, modePaiementId: this.modesPaiementActifs()[0]?.id ?? null, montant: null, reference: '' }]);
      return;
    }
    this.paiements.update(paiements => paiements.filter(paiement => paiement.uid !== uid));
  }

  mettreAJourPaiement(uid: number, champ: 'modePaiementId' | 'montant' | 'reference', valeur: number | string | null): void {
    this.paiements.update(paiements =>
      paiements.map(paiement => {
        if (paiement.uid !== uid) {
          return paiement;
        }

        return {
          ...paiement,
          [champ]:
            champ === 'montant' ? (typeof valeur === 'number' ? valeur : valeur === null || valeur === '' ? null : Number(valeur)) : valeur,
        };
      }),
    );
  }

  async finaliserTicket(): Promise<void> {
    const boutique = this.boutique();
    const locataire = this.locataire();
    if (!this.peutValider() || !boutique || !locataire) {
      this.message.set({
        type: 'danger',
        key: 'caisse.messages.ticketIncomplete',
      });
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);
    this.ticketGenere.set(null);

    try {
      const resultat = await firstValueFrom(
        this.venteService.checkout({
          boutiqueId: boutique.id,
          locataireId: locataire.id,
          referencePassager: this.referencePassager().trim() || null,
          referenceCarteEmbarquement: this.referenceCarte().trim() || null,
          commentaire: this.commentaire().trim() || null,
          lignes: this.panier().map(ligne => ({
            produitId: ligne.article.produitId,
            quantite: ligne.quantite,
            remise: ligne.remise,
            typePrix: ligne.typePrix,
            codeBarresScanne: ligne.codeBarresScanne ?? null,
          })),
          paiements: this.paiements()
            .filter(paiement => paiement.modePaiementId && paiement.montant && paiement.montant > 0)
            .map(paiement => ({
              modePaiementId: paiement.modePaiementId!,
              montant: paiement.montant!,
              referencePaiement: paiement.reference.trim() || null,
            })),
        }),
      );

      this.ticketGenere.set(resultat.ticket);
      this.venteGeneree.set(resultat.vente);
      this.lignesVenteSelectionnee.set(resultat.lignes);
      this.paiementsVenteSelectionnee.set(resultat.paiements);
      this.ticketVenteSelectionnee.set(resultat.ticket);
      this.message.set({
        type: 'success',
        key: 'caisse.messages.saleValidated',
        params: { ticket: resultat.vente.numeroTicket },
      });
      this.reinitialiserSessionCaisse();
      await this.chargerContexte(boutique.id);
      if (this.peutVoirHistoriqueVentes()) {
        await this.chargerHistoriqueVentes();
        await this.selectionnerVenteHistorique(resultat.vente.id);
      }
    } catch (error) {
      this.message.set({
        type: 'danger',
        key: this.messageErreurCheckout(error),
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  ouvrirValidationTicket(): void {
    if (!this.peutValider()) {
      this.message.set({
        type: 'danger',
        key: 'caisse.messages.completeBeforeValidate',
      });
      return;
    }

    this.modalActif.set('validation-ticket');
  }

  async confirmerValidationTicket(): Promise<void> {
    this.fermerModal();
    await this.finaliserTicket();
  }

  ouvrirModalTicket(): void {
    if (!this.venteSelectionnee()) {
      this.message.set({ type: 'info', key: 'caisse.messages.selectSaleForPreview' });
      return;
    }

    this.modalActif.set('apercu-ticket');
  }

  ouvrirOperationHistorique(typeOperation: Exclude<OperationHistoriqueModal, null>): void {
    const vente = this.venteSelectionnee();
    if (!vente) {
      this.message.set({ type: 'info', key: 'caisse.messages.selectSaleBeforeAction' });
      return;
    }

    if (typeOperation === 'ANNULATION' && !this.peutAnnulerSelection()) {
      this.message.set({ type: 'info', key: 'caisse.messages.saleCannotBeCancelled' });
      return;
    }

    if (typeOperation === 'RETOUR' && !this.peutRetourSelection()) {
      this.message.set({ type: 'info', key: 'caisse.messages.onlyValidatedCanReturn' });
      return;
    }

    this.operationHistoriqueModal.set(typeOperation);
    this.motifOperationHistorique.set(
      typeOperation === 'ANNULATION'
        ? this.translateService.instant('caisse.defaults.operatorCancellation')
        : this.translateService.instant('caisse.defaults.customerReturn'),
    );
    this.modalActif.set('operation-historique');
  }

  async confirmerOperationHistorique(): Promise<void> {
    const typeOperation = this.operationHistoriqueModal();
    if (!typeOperation) {
      return;
    }

    const motif = this.motifOperationHistorique().trim();
    if (!motif) {
      this.message.set({ type: 'danger', key: 'caisse.messages.reasonRequired' });
      return;
    }

    this.fermerModal();
    await this.appliquerOperationHistorique(typeOperation, motif);
  }

  fermerModal(): void {
    this.modalActif.set(null);
    this.operationHistoriqueModal.set(null);
    this.motifOperationHistorique.set('');
  }

  async selectionnerVenteHistorique(venteId: number): Promise<void> {
    this.venteSelectionneeId.set(venteId);
    await this.chargerDetailsVente(venteId);
  }

  annulerVenteSelectionnee(): void {
    this.ouvrirOperationHistorique('ANNULATION');
  }

  retournerVenteSelectionnee(): void {
    this.ouvrirOperationHistorique('RETOUR');
  }

  async reimprimerTicketSelectionne(): Promise<void> {
    const vente = this.venteSelectionnee();
    if (!vente) {
      this.message.set({ type: 'info', key: 'caisse.messages.selectSaleBeforeReprint' });
      return;
    }

    this.actionHistorique.set('reimpression');
    this.message.set(null);

    try {
      const { lignes, paiements, ticket } = await this.chargerDetailsVente(vente.id);
      const contenu = this.genererContenuTicket(vente, lignes, paiements);
      let ticketMisAJour: ITicketCaisse;

      if (ticket) {
        ticketMisAJour = await firstValueFrom(
          this.ticketCaisseService.partialUpdate({
            id: ticket.id,
            dateEmission: dayjs(),
            nombreImpressions: (ticket.nombreImpressions ?? 0) + 1,
            contenu,
          }),
        );
      } else {
        ticketMisAJour = await firstValueFrom(
          this.ticketCaisseService.create({
            id: null,
            numero: `TC-${vente.numeroTicket ?? vente.id}`,
            dateEmission: dayjs(),
            nombreImpressions: 1,
            contenu,
            vente: { id: vente.id, numeroTicket: vente.numeroTicket },
          }),
        );
      }

      this.ticketVenteSelectionnee.set(ticketMisAJour);
      this.ticketGenere.set(ticketMisAJour);
      this.message.set({
        type: 'success',
        key: 'caisse.messages.ticketReprinted',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'caisse.messages.reprintError',
      });
    } finally {
      this.actionHistorique.set(null);
    }
  }

  imprimerTicket(ticket: ITicketCaisse | null | undefined): void {
    if (!ticket?.contenu) {
      this.message.set({ type: 'info', key: 'caisse.messages.selectTicketBeforePrint' });
      return;
    }

    const fenetreImpression = window.open('', '_blank', 'width=420,height=720');
    if (!fenetreImpression) {
      this.message.set({ type: 'danger', key: 'caisse.messages.printError' });
      return;
    }

    const titre = this.echapperHtml(ticket.numero ?? this.translateService.instant('caisse.ticket.title'));
    const contenu = this.echapperHtml(ticket.contenu);

    fenetreImpression.document.open();
    // eslint-disable-next-line @typescript-eslint/no-deprecated
    fenetreImpression.document.write(`<!doctype html>
<html lang="${this.localeCourante()}">
  <head>
    <meta charset="utf-8">
    <title>${titre}</title>
    <style>
      @page {
        size: 80mm auto;
        margin: 4mm;
      }

      * {
        box-sizing: border-box;
      }

      body {
        margin: 0;
        color: #111827;
        background: #fff;
        font-family: "Consolas", "Courier New", monospace;
        font-size: 11px;
        line-height: 1.35;
      }

      .ticket {
        width: 72mm;
        margin: 0 auto;
        white-space: pre-wrap;
        overflow-wrap: anywhere;
      }

      .ticket__header {
        margin-bottom: 8px;
        padding-bottom: 6px;
        border-bottom: 1px dashed #94a3b8;
        text-align: center;
        font-weight: 700;
      }

      pre {
        margin: 0;
        font: inherit;
        white-space: pre-wrap;
      }

      @media screen {
        body {
          padding: 16px;
          background: #f8fafc;
        }

        .ticket {
          padding: 14px;
          border: 1px solid #e2e8f0;
          background: #fff;
          box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
        }
      }
    </style>
  </head>
  <body>
    <main class="ticket">
      <div class="ticket__header">${titre}</div>
      <pre>${contenu}</pre>
    </main>
    <script>
      window.addEventListener('load', () => {
        window.focus();
        window.print();
      });
    </script>
  </body>
</html>`);
    fenetreImpression.document.close();
    this.message.set({ type: 'success', key: 'caisse.messages.ticketPrinted' });
  }

  private async initialiser(): Promise<void> {
    this.chargement.set(true);
    try {
      await this.chargerContexte(null);
      if (this.peutVoirHistoriqueVentes()) {
        await this.chargerHistoriqueVentes();
      }
    } finally {
      this.chargement.set(false);
    }
  }

  /**
   * Le serveur reste la seule autorite sur la boutique active, le locataire exploitant et les
   * articles vendables (`VenteService.getContextePoste`, cote backend) : plus aucun calcul de
   * perimetre boutique n'est reproduit cote client. Cela evite tout ecart entre ce que l'UI pense
   * accessible et ce que le backend autorise reellement (source des articles "invisibles" en
   * caisse alors qu'ils existaient bien en stock).
   */
  private async chargerContexte(boutiqueId: number | null): Promise<void> {
    this.chargementArticles.set(true);
    try {
      const contexte = await firstValueFrom(this.venteService.getContextePoste(boutiqueId));
      this.contexte.set(contexte);
      this.panier.update(panier => panier.filter(ligne => ligne.article.produitId in this.indexArticles(contexte.articles)));
      this.ajusterPaiementUniqueAuTotal();
      if (!this.paiements()[0]?.modePaiementId && this.modesPaiementActifs().length > 0) {
        this.paiements.update(paiements =>
          paiements.map((paiement, index) => (index === 0 ? { ...paiement, modePaiementId: this.modesPaiementActifs()[0].id } : paiement)),
        );
      }
      this.message.set(null);
    } catch {
      this.contexte.set(null);
      this.message.set({ type: 'danger', key: 'caisse.messages.contextLoadError' });
    } finally {
      this.chargementArticles.set(false);
    }
  }

  private indexArticles(articles: CaissePosteArticle[]): Record<number, true> {
    const index: Record<number, true> = {};
    for (const article of articles) {
      index[article.produitId] = true;
    }
    return index;
  }

  private async chargerHistoriqueVentes(): Promise<void> {
    if (!this.peutVoirHistoriqueVentes()) {
      this.ventesHistorique.set([]);
      return;
    }

    const ids = this.boutiquesAccessibles().map(boutique => boutique.id);
    const params = this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm() ? {} : { 'boutiqueId.in': ids.join(',') };
    if (!this.permissionsUi.estAdmin() && !this.permissionsUi.estProfilAdm() && ids.length === 0) {
      this.ventesHistorique.set([]);
      return;
    }

    const response = await firstValueFrom(this.venteService.query({ ...params, size: 120, sort: ['dateHeure,desc'] }));
    this.ventesHistorique.set(response.body ?? []);
  }

  private async chargerDetailsVente(venteId: number): Promise<{
    lignes: ILigneVente[];
    paiements: IPaiementVente[];
    ticket: ITicketCaisse | null;
    operations: IOperationCorrectiveVente[];
  }> {
    const [lignesResponse, paiementsResponse, ticketsResponse, operationsResponse] = await Promise.all([
      firstValueFrom(this.ligneVenteService.query({ 'venteId.equals': venteId, size: 200, sort: ['id,asc'] })),
      firstValueFrom(this.paiementVenteService.query({ 'venteId.equals': venteId, size: 50, sort: ['datePaiement,asc'] })),
      firstValueFrom(this.ticketCaisseService.query({ 'venteId.equals': venteId, size: 10, sort: ['dateEmission,desc'] })),
      firstValueFrom(this.operationCorrectiveVenteService.query({ 'venteId.equals': venteId, size: 20, sort: ['dateOperation,desc'] })),
    ]);

    const lignes = lignesResponse.body ?? [];
    const paiements = paiementsResponse.body ?? [];
    const ticket = (ticketsResponse.body ?? [])[0] ?? null;
    const operations = operationsResponse.body ?? [];

    this.lignesVenteSelectionnee.set(lignes);
    this.paiementsVenteSelectionnee.set(paiements);
    this.ticketVenteSelectionnee.set(ticket);
    this.operationsVenteSelectionnee.set(operations);

    return { lignes, paiements, ticket, operations };
  }

  private async appliquerOperationHistorique(typeOperation: keyof typeof TypeOperationCorrective, motifSaisi: string): Promise<void> {
    const vente = this.venteSelectionnee();
    if (!vente) {
      this.message.set({ type: 'info', key: 'caisse.messages.selectSaleBeforeAction' });
      return;
    }

    this.actionHistorique.set(typeOperation === 'ANNULATION' ? 'annulation' : 'retour');
    this.message.set(null);

    try {
      const compte = this.account();
      const operation: NewOperationCorrectiveVente = {
        id: null,
        typeOperation: TypeOperationCorrective[typeOperation],
        motif: motifSaisi.trim() || (typeOperation === 'ANNULATION' ? 'Annulation' : 'Retour'),
        montantImpact: this.arrondirMontant(vente.montantNet ?? 0),
        dateOperation: dayjs(),
        vente: { id: vente.id, numeroTicket: vente.numeroTicket },
        utilisateur: compte?.id ? { id: compte.id, login: compte.login } : null,
      };

      await firstValueFrom(this.operationCorrectiveVenteService.create(operation));
      await this.chargerHistoriqueVentes();
      await this.selectionnerVenteHistorique(vente.id);
      this.message.set({
        type: 'success',
        key: typeOperation === 'ANNULATION' ? 'caisse.messages.saleCancelled' : 'caisse.messages.saleReturned',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'caisse.messages.operationError',
      });
    } finally {
      this.actionHistorique.set(null);
    }
  }

  private async resoudreProduitScanne(code: string): Promise<CaissePosteArticle | null> {
    const boutique = this.boutique();
    if (!boutique) {
      return null;
    }

    const resultat = await firstValueFrom(this.codeBarresProduitService.scan(code, boutique.id, 'POSTE_CAISSE'));
    this.scanInconnuAffectable.set(!resultat.trouve && resultat.affectationAutorisee);
    if (resultat.trouve && resultat.produit) {
      const article = this.resoudreArticleDepuisProduit(resultat.produit);
      if (!article || !this.peutAjouterProduit(article)) {
        this.message.set({
          type: 'info',
          key: 'caisse.messages.productOutOfStock',
          params: { product: this.formatValeur(resultat.produit.designation) },
        });
        return null;
      }
      return article;
    }
    this.message.set({
      type: 'danger',
      key: resultat.affectationAutorisee ? 'caisse.messages.unknownScanAssignable' : 'caisse.messages.unknownScanSaved',
      params: { code },
    });
    return null;
  }

  private resoudreArticleDepuisProduit(produit: IProduit): CaissePosteArticle | null {
    return this.articlesDisponibles().find(article => article.produitId === produit.id) ?? null;
  }

  private ajouterArticleAuPanier(article: CaissePosteArticle, codeBarresScanne?: string): void {
    this.panier.update(panier => {
      const typePrix = this.typePrixActif();
      const prixUnitaire = this.prixArticle(article, typePrix);
      const ligneExistante = panier.find(ligne => ligne.article.produitId === article.produitId && ligne.typePrix === typePrix);
      if (ligneExistante) {
        return panier.map(ligne =>
          ligne.article.produitId === article.produitId && ligne.typePrix === typePrix
            ? {
                ...ligne,
                quantite: ligne.quantite + 1,
                codeBarresScanne: codeBarresScanne ?? ligne.codeBarresScanne,
              }
            : ligne,
        );
      }

      return [
        ...panier,
        {
          article,
          quantite: 1,
          remise: 0,
          typePrix,
          prixUnitaire,
          codeBarresScanne: codeBarresScanne ?? null,
        },
      ];
    });
    this.ajusterPaiementUniqueAuTotal();
  }

  private ajusterPaiementUniqueAuTotal(): void {
    if (this.paiements().length !== 1) {
      return;
    }

    const modePaiementId = this.paiements()[0]?.modePaiementId ?? this.modesPaiementActifs()[0]?.id ?? null;
    this.paiements.set([
      {
        ...this.paiements()[0],
        uid: this.paiements()[0]?.uid ?? 1,
        modePaiementId,
        montant: this.totalNet() > 0 ? this.totalNet() : null,
      },
    ]);
  }

  private genererContenuTicket(
    vente: IVente,
    lignes: ILigneVente[],
    paiements: { montant?: number | null; modePaiement?: { libelle?: string | null } | null }[],
  ): string {
    const lignesTexte = lignes
      .map(
        ligne =>
          `${ligne.produit?.designation ?? this.translateService.instant('caisse.ticket.product')} x${ligne.quantite ?? 0} - ${this.formatMontant(ligne.montantLigne ?? 0)}`,
      )
      .join('\n');
    const paiementsTexte = paiements
      .map(
        paiement =>
          `${paiement.modePaiement?.libelle ?? this.translateService.instant('caisse.ticket.payment')} - ${this.formatMontant(paiement.montant ?? 0)}`,
      )
      .join('\n');

    return [
      `${this.translateService.instant('caisse.ticket.receiptPrefix')} ${vente.boutique?.nom ?? '--'}`,
      `${this.translateService.instant('caisse.ticket.number')}: ${vente.numeroTicket ?? '--'}`,
      `${this.translateService.instant('caisse.ticket.date')}: ${vente.dateHeure?.format('DD/MM/YYYY HH:mm') ?? '--'}`,
      `${this.translateService.instant('caisse.ticket.shop')}: ${vente.boutique?.nom ?? '--'}`,
      `${this.translateService.instant('caisse.ticket.tenant')}: ${vente.locataire?.nom ?? '--'}`,
      `${this.translateService.instant('caisse.ticket.seller')}: ${vente.vendeur?.login ?? '--'}`,
      '',
      this.translateService.instant('caisse.ticket.items'),
      lignesTexte,
      '',
      this.translateService.instant('caisse.ticket.payments'),
      paiementsTexte,
      '',
      `${this.translateService.instant('caisse.ticket.netTotal')}: ${this.formatMontant(vente.montantNet ?? 0)}`,
    ].join('\n');
  }

  private echapperHtml(valeur: string): string {
    return valeur
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  private reinitialiserSessionCaisse(): void {
    this.panier.set([]);
    this.scanInput.set('');
    this.rechercheProduit.set('');
    this.referencePassager.set('');
    this.referenceCarte.set('');
    this.commentaire.set('');
    this.paiements.set([
      {
        uid: 1,
        modePaiementId: this.modesPaiementActifs()[0]?.id ?? null,
        montant: null,
        reference: '',
      },
    ]);
  }

  private arrondirMontant(valeur: number): number {
    return Math.round(valeur * 100) / 100;
  }

  private panierStockValide(): boolean {
    return this.panier().every(ligne => ligne.quantite <= this.stockDisponible(ligne.article.produitId));
  }

  private messageErreurCheckout(error: unknown): string {
    const err = error as { error?: { message?: string; errorKey?: string } };
    const cle = err.error?.errorKey ?? err.error?.message;
    if (cle === 'insufficientStock') {
      return 'caisse.messages.insufficientStock';
    }
    if (cle === 'paymentMismatch') {
      return 'caisse.messages.paymentMismatch';
    }
    if (cle === 'inactiveTenant') {
      return 'caisse.messages.inactiveTenant';
    }
    return 'caisse.messages.finalizeError';
  }

  statutVenteLabelKey(statut: string | null | undefined): string {
    return statut ? `caisse.statuses.sale.${statut}` : 'caisse.common.notAvailable';
  }

  statutPaiementLabelKey(statut: string | null | undefined): string {
    return statut ? `caisse.statuses.payment.${statut}` : 'caisse.common.notAvailable';
  }

  private localeCourante(): string {
    return this.translateService.getCurrentLang() === 'en' ? 'en-US' : 'fr-FR';
  }
}
