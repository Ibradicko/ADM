import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { AdmDashboardReportingService, DashboardStockAlertResponse } from 'app/core/services/adm-dashboard-reporting.service';
import {
  AdmStockWorkflowService,
  CloseInventairePayload,
  ReverseMouvementPayload,
  ScanInventairePayload,
  ScanReceptionPayload,
  ScanTransfertPayload,
  ValidateReceptionPayload,
  ValidateTransfertPayload,
} from 'app/core/services/adm-stock-workflow.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { IInventaireStock } from 'app/entities/inventaire-stock/inventaire-stock.model';
import { InventaireStockService } from 'app/entities/inventaire-stock/service/inventaire-stock.service';
import { ILigneInventaireStock } from 'app/entities/ligne-inventaire-stock/ligne-inventaire-stock.model';
import { LigneInventaireStockService } from 'app/entities/ligne-inventaire-stock/service/ligne-inventaire-stock.service';
import { ILigneReceptionProduit } from 'app/entities/ligne-reception-produit/ligne-reception-produit.model';
import { LigneReceptionProduitService } from 'app/entities/ligne-reception-produit/service/ligne-reception-produit.service';
import { ILigneTransfertStock } from 'app/entities/ligne-transfert-stock/ligne-transfert-stock.model';
import { LigneTransfertStockService } from 'app/entities/ligne-transfert-stock/service/ligne-transfert-stock.service';
import { IMouvementStock } from 'app/entities/mouvement-stock/mouvement-stock.model';
import { MouvementStockService } from 'app/entities/mouvement-stock/service/mouvement-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IReceptionProduit } from 'app/entities/reception-produit/reception-produit.model';
import { ReceptionProduitService } from 'app/entities/reception-produit/service/reception-produit.service';
import { IStockProduit } from 'app/entities/stock-produit/stock-produit.model';
import { StockProduitService } from 'app/entities/stock-produit/service/stock-produit.service';
import { ITransfertStock } from 'app/entities/transfert-stock/transfert-stock.model';
import { TransfertStockService } from 'app/entities/transfert-stock/service/transfert-stock.service';
import { TranslateDirective } from 'app/shared/language';

type OngletStock = 'stock' | 'approvisionnement' | 'receptions' | 'inventaires' | 'transferts';

interface MessageStock {
  type: 'success' | 'danger' | 'info';
  key: string;
}

type QueryParams = Record<string, string | number | boolean | readonly string[]>;

@Component({
  selector: 'jhi-stock-operations',
  templateUrl: './stock-operations.html',
  imports: [FormsModule, RouterLink, FontAwesomeModule, TranslateDirective, TranslateModule],
})
export default class StockOperationsComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  readonly ongletActif = signal<OngletStock>('stock');
  readonly chargement = signal(false);
  readonly actionEnCours = signal<string | null>(null);
  readonly message = signal<MessageStock | null>(null);

  readonly boutiques = signal<IBoutique[]>([]);
  readonly produits = signal<IProduit[]>([]);
  readonly groupeArticles = signal<IGroupeArticle[]>([]);
  readonly stocks = signal<IStockProduit[]>([]);
  readonly receptions = signal<IReceptionProduit[]>([]);
  readonly inventaires = signal<IInventaireStock[]>([]);
  readonly transferts = signal<ITransfertStock[]>([]);
  readonly mouvements = signal<IMouvementStock[]>([]);
  readonly alertesStock = signal<DashboardStockAlertResponse[]>([]);

  readonly lignesReception = signal<ILigneReceptionProduit[]>([]);
  readonly lignesInventaire = signal<ILigneInventaireStock[]>([]);
  readonly lignesTransfert = signal<ILigneTransfertStock[]>([]);

  readonly boutiqueFiltreId = signal<number | null>(null);
  readonly rechercheStock = signal('');

  readonly receptionSelectionneeId = signal<number | null>(null);
  readonly inventaireSelectionneId = signal<number | null>(null);
  readonly transfertSelectionneId = signal<number | null>(null);
  readonly mouvementSelectionneId = signal<number | null>(null);

  readonly receptionProduitId = signal<number | null>(null);
  readonly receptionCodeBarres = signal('');
  readonly receptionQuantite = signal<number>(1);

  readonly approvisionnementBoutiqueId = signal<number | null>(null);
  readonly approvisionnementGroupeId = signal<number | null>(null);
  readonly approvisionnementProduitId = signal<number | null>(null);
  readonly approvisionnementQuantite = signal<number>(1);
  readonly approvisionnementDate = signal(dayjs().format('YYYY-MM-DDTHH:mm'));
  readonly approvisionnementReference = signal('');
  readonly approvisionnementObservation = signal('');

  readonly inventaireProduitId = signal<number | null>(null);
  readonly inventaireCodeBarres = signal('');
  readonly inventaireQuantite = signal<number>(0);
  readonly inventaireCommentaire = signal('');
  readonly inventaireAppliquerAjustements = signal(true);

  readonly transfertProduitId = signal<number | null>(null);
  readonly transfertCodeBarres = signal('');
  readonly transfertQuantite = signal(1);
  readonly motifReverse = signal('');

  private readonly depots = signal<IDepotStock[]>([]);

  readonly peutVoirToutesBoutiques = computed(() => this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm());
  readonly libellePerimetre = computed(() => {
    if (this.peutVoirToutesBoutiques()) {
      return 'stockOperations.scope.allShops';
    }
    return this.boutiquesAccessibles().length > 1 ? 'stockOperations.scope.myShops' : 'stockOperations.scope.myShop';
  });
  readonly boutiqueIdsAccessibles = computed(() => new Set(this.permissionsUi.boutiqueIds()));
  readonly boutiquesAccessibles = computed(() => {
    if (this.peutVoirToutesBoutiques()) {
      return this.boutiques();
    }
    const ids = this.boutiqueIdsAccessibles();
    return this.boutiques().filter(boutique => ids.has(boutique.id));
  });
  readonly stocksFiltres = computed(() => {
    const recherche = this.rechercheStock().trim().toLowerCase();
    const boutiqueFiltre = this.boutiqueFiltreId();
    const boutiqueIds = this.boutiqueIdsAccessibles();
    return this.stocks().filter(stock => {
      if (!this.peutVoirToutesBoutiques()) {
        const depotBoutiqueId = this.depots().find(d => d.id === stock.depot?.id)?.boutique?.id;
        if (depotBoutiqueId && !boutiqueIds.has(depotBoutiqueId)) {
          return false;
        }
      }
      if (boutiqueFiltre && !this.estDepotDansBoutique(stock.depot?.id ?? null, boutiqueFiltre)) {
        return false;
      }
      if (!recherche) {
        return true;
      }
      return stock.produit?.designation?.toLowerCase().includes(recherche) ?? false;
    });
  });
  readonly produitsAccessibles = computed(() => {
    if (this.peutVoirToutesBoutiques()) {
      return this.produits();
    }
    const ids = this.boutiqueIdsAccessibles();
    return this.produits().filter(produit => produit.boutique?.id && ids.has(produit.boutique.id));
  });
  readonly receptionsAccessibles = computed(() =>
    this.filtrerParBoutiqueAccessible(this.receptions(), reception => reception.boutique?.id),
  );
  readonly inventairesAccessibles = computed(() =>
    this.filtrerParBoutiqueAccessible(this.inventaires(), inventaire => inventaire.boutique?.id),
  );
  readonly mouvementsAccessibles = computed(() =>
    this.filtrerParBoutiqueAccessible(this.mouvements(), mouvement => mouvement.boutique?.id),
  );
  readonly transfertsAccessibles = computed(() =>
    this.transferts().filter(transfert => {
      if (this.boutiqueFiltreId()) {
        return transfert.boutiqueOrigine?.id === this.boutiqueFiltreId() || transfert.boutiqueDestination?.id === this.boutiqueFiltreId();
      }
      if (this.peutVoirToutesBoutiques()) {
        return true;
      }
      const ids = this.boutiqueIdsAccessibles();
      return (
        (transfert.boutiqueOrigine?.id ? ids.has(transfert.boutiqueOrigine.id) : false) ||
        (transfert.boutiqueDestination?.id ? ids.has(transfert.boutiqueDestination.id) : false)
      );
    }),
  );
  readonly receptionSelectionnee = computed(
    () => this.receptionsAccessibles().find(item => item.id === this.receptionSelectionneeId()) ?? null,
  );
  readonly inventaireSelectionne = computed(
    () => this.inventairesAccessibles().find(item => item.id === this.inventaireSelectionneId()) ?? null,
  );
  readonly transfertSelectionne = computed(
    () => this.transfertsAccessibles().find(item => item.id === this.transfertSelectionneId()) ?? null,
  );
  readonly produitsTransfert = computed(() => {
    const boutiqueId = this.transfertSelectionne()?.boutiqueOrigine?.id;
    return this.produitsAccessibles().filter(produit => !boutiqueId || produit.boutique?.id === boutiqueId);
  });
  readonly produitsApprovisionnement = computed(() => {
    const boutiqueId = this.approvisionnementBoutiqueId();
    const groupeId = this.approvisionnementGroupeId();
    return this.produitsAccessibles().filter(produit => {
      if (boutiqueId && produit.boutique?.id !== boutiqueId) {
        return false;
      }
      if (groupeId && produit.groupeArticle?.id !== groupeId) {
        return false;
      }
      return true;
    });
  });
  readonly articleApprovisionnement = computed(
    () => this.produitsAccessibles().find(produit => produit.id === this.approvisionnementProduitId()) ?? null,
  );
  readonly stockArticleApprovisionnement = computed(() => {
    const produitId = this.approvisionnementProduitId();
    const boutiqueId = this.approvisionnementBoutiqueId();
    if (!produitId) {
      return null;
    }
    return (
      this.stocks().find(stock => {
        if (stock.produit?.id !== produitId) {
          return false;
        }
        return !boutiqueId || this.estDepotDansBoutique(stock.depot?.id ?? null, boutiqueId);
      }) ?? null
    );
  });
  readonly formulaireApprovisionnementValide = computed(
    () =>
      !!this.approvisionnementBoutiqueId() &&
      !!this.approvisionnementProduitId() &&
      this.approvisionnementQuantite() > 0 &&
      !!this.approvisionnementDate(),
  );
  readonly mouvementSelectionne = computed(
    () => this.mouvementsAccessibles().find(item => item.id === this.mouvementSelectionneId()) ?? null,
  );

  private readonly accountService = inject(AccountService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly boutiqueService = inject(BoutiqueService);
  private readonly depotStockService = inject(DepotStockService);
  private readonly groupeArticleService = inject(GroupeArticleService);
  private readonly produitService = inject(ProduitService);
  private readonly stockProduitService = inject(StockProduitService);
  private readonly receptionProduitService = inject(ReceptionProduitService);
  private readonly inventaireStockService = inject(InventaireStockService);
  private readonly transfertStockService = inject(TransfertStockService);
  private readonly mouvementStockService = inject(MouvementStockService);
  private readonly ligneReceptionProduitService = inject(LigneReceptionProduitService);
  private readonly ligneInventaireStockService = inject(LigneInventaireStockService);
  private readonly ligneTransfertStockService = inject(LigneTransfertStockService);
  private readonly stockWorkflowService = inject(AdmStockWorkflowService);
  private readonly dashboardReportingService = inject(AdmDashboardReportingService);

  ngOnInit(): void {
    void this.initialiser();
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? valeur.toLocaleString('fr-FR') : '--';
  }

  formatDate(valeur?: dayjs.Dayjs | null): string {
    return valeur ? valeur.format('DD/MM/YYYY HH:mm') : '--';
  }

  produitParId(produitId: number | null | undefined): IProduit | undefined {
    if (!produitId) {
      return undefined;
    }
    return (
      this.produitsAccessibles().find(produit => produit.id === produitId) ?? this.produits().find(produit => produit.id === produitId)
    );
  }

  imageProduitSrc(produit: IProduit | null | undefined): string | null {
    return produit?.image && produit.imageContentType ? `data:${produit.imageContentType};base64,${produit.image}` : null;
  }

  definirOnglet(onglet: OngletStock): void {
    this.ongletActif.set(onglet);
  }

  changerBoutiqueFiltre(boutiqueId: number | null): void {
    this.boutiqueFiltreId.set(boutiqueId);
    this.reinitialiserSelectionsOperationnelles();
    void this.chargerAlertes();
  }

  changerBoutiqueApprovisionnement(boutiqueId: number | null): void {
    this.approvisionnementBoutiqueId.set(boutiqueId);
    if (
      this.approvisionnementProduitId() &&
      !this.produitsApprovisionnement().some(produit => produit.id === this.approvisionnementProduitId())
    ) {
      this.approvisionnementProduitId.set(null);
    }
  }

  changerGroupeApprovisionnement(groupeId: number | null): void {
    this.approvisionnementGroupeId.set(groupeId);
    if (
      this.approvisionnementProduitId() &&
      !this.produitsApprovisionnement().some(produit => produit.id === this.approvisionnementProduitId())
    ) {
      this.approvisionnementProduitId.set(null);
    }
  }

  changerArticleApprovisionnement(produitId: number | null): void {
    this.approvisionnementProduitId.set(produitId);
    const produit = this.produitsAccessibles().find(item => item.id === produitId);
    if (!produit) {
      return;
    }
    if (produit.boutique?.id) {
      this.approvisionnementBoutiqueId.set(produit.boutique.id);
    }
    if (produit.groupeArticle?.id) {
      this.approvisionnementGroupeId.set(produit.groupeArticle.id);
    }
  }

  preparerApprovisionnement(produit?: IProduit | null): void {
    if (produit?.boutique?.id) {
      this.approvisionnementBoutiqueId.set(produit.boutique.id);
    }
    if (produit?.groupeArticle?.id) {
      this.approvisionnementGroupeId.set(produit.groupeArticle.id);
    }
    if (produit?.id) {
      this.approvisionnementProduitId.set(produit.id);
    }
    this.initialiserApprovisionnementParDefaut();
    this.ongletActif.set('approvisionnement');
  }

  preparerApprovisionnementDepuisStock(stock: IStockProduit): void {
    const produit = this.produitsAccessibles().find(item => item.id === stock.produit?.id) ?? null;
    this.preparerApprovisionnement(produit);
  }

  async selectionnerReception(receptionId: number): Promise<void> {
    if (!this.receptionsAccessibles().some(reception => reception.id === receptionId)) {
      return;
    }
    this.receptionSelectionneeId.set(receptionId);
    await this.chargerLignesReception(receptionId);
  }

  async selectionnerInventaire(inventaireId: number): Promise<void> {
    if (!this.inventairesAccessibles().some(inventaire => inventaire.id === inventaireId)) {
      return;
    }
    this.inventaireSelectionneId.set(inventaireId);
    await this.chargerLignesInventaire(inventaireId);
  }

  async selectionnerTransfert(transfertId: number): Promise<void> {
    if (!this.transfertsAccessibles().some(transfert => transfert.id === transfertId)) {
      return;
    }
    this.transfertSelectionneId.set(transfertId);
    await this.chargerLignesTransfert(transfertId);
  }

  async scannerReception(): Promise<void> {
    const receptionId = this.receptionSelectionneeId();
    if (!receptionId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectReceptionForScan' });
      return;
    }

    await this.executerAction('scan-reception', async () => {
      const payload: ScanReceptionPayload = {
        produitId: this.receptionProduitId(),
        codeBarres: this.receptionCodeBarres().trim() || null,
        quantiteRecue: this.receptionQuantite(),
      };
      await firstValueFrom(this.stockWorkflowService.scanReception(receptionId, payload));
      this.receptionCodeBarres.set('');
      this.receptionProduitId.set(null);
      this.receptionQuantite.set(1);
      await Promise.all([this.chargerLignesReception(receptionId), this.chargerReceptions()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.receptionLineAdded' });
    });
  }

  async validerReception(): Promise<void> {
    const receptionId = this.receptionSelectionneeId();
    const reception = this.receptionSelectionnee();
    if (!receptionId || !reception) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectReceptionForScan' });
      return;
    }
    const boutiqueReception = reception.boutique;
    const depotId = boutiqueReception ? await this.resolveOuCreerDepotPourBoutique(boutiqueReception) : null;
    if (!depotId) {
      this.message.set({ type: 'danger', key: 'stockOperations.messages.actionFailed' });
      return;
    }

    await this.executerAction('validation-reception', async () => {
      const payload: ValidateReceptionPayload = { depotId };
      await firstValueFrom(this.stockWorkflowService.validateReception(receptionId, payload));
      await Promise.all([this.chargerReceptions(), this.chargerStocks(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.receptionValidated' });
    });
  }

  async validerApprovisionnement(): Promise<void> {
    const boutiqueId = this.approvisionnementBoutiqueId();
    const produitId = this.approvisionnementProduitId();
    const quantite = this.approvisionnementQuantite();
    const account = this.accountService.account();
    const accountId = account?.id;
    const accountLogin = account?.login ?? '';
    const boutique = this.boutiquesAccessibles().find(item => item.id === boutiqueId);

    if (!boutiqueId || !produitId || quantite <= 0 || !this.approvisionnementDate() || !accountId || !boutique) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.supplyMissingFields' });
      return;
    }

    const depotId = await this.resolveOuCreerDepotPourBoutique(boutique);
    if (!depotId) {
      this.message.set({ type: 'danger', key: 'stockOperations.messages.actionFailed' });
      return;
    }

    await this.executerAction('approvisionnement', async () => {
      const reference = this.approvisionnementReference().trim() || `APP-${Date.now()}`;
      const reception = await firstValueFrom(
        this.receptionProduitService.create({
          id: null,
          reference,
          dateReception: dayjs(this.approvisionnementDate()),
          fournisseur: null,
          commentaire: this.approvisionnementObservation().trim() || null,
          boutique,
          utilisateur: { id: accountId, login: accountLogin },
        }),
      );

      if (!reception?.id) {
        throw new Error('Reception creation failed');
      }

      await firstValueFrom(this.stockWorkflowService.scanReception(reception.id, { produitId, codeBarres: null, quantiteRecue: quantite }));
      await firstValueFrom(this.stockWorkflowService.validateReception(reception.id, { depotId }));
      this.approvisionnementQuantite.set(1);
      this.approvisionnementReference.set('');
      this.approvisionnementObservation.set('');
      await Promise.all([this.chargerReceptions(), this.chargerStocks(), this.chargerMouvements(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.supplyValidated' });
    });
  }

  async demarrerInventaire(): Promise<void> {
    const inventaireId = this.inventaireSelectionneId();
    if (!inventaireId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectInventoryToStart' });
      return;
    }

    await this.executerAction('demarrage-inventaire', async () => {
      await firstValueFrom(this.stockWorkflowService.startInventaire(inventaireId));
      await this.chargerInventaires();
      this.message.set({ type: 'success', key: 'stockOperations.messages.inventoryStarted' });
    });
  }

  async scannerInventaire(): Promise<void> {
    const inventaireId = this.inventaireSelectionneId();
    if (!inventaireId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectInventoryForCount' });
      return;
    }

    await this.executerAction('scan-inventaire', async () => {
      const payload: ScanInventairePayload = {
        produitId: this.inventaireProduitId(),
        codeBarres: this.inventaireCodeBarres().trim() || null,
        quantiteComptee: this.inventaireQuantite(),
        commentaire: this.inventaireCommentaire().trim() || null,
      };
      await firstValueFrom(this.stockWorkflowService.scanInventaire(inventaireId, payload));
      this.inventaireProduitId.set(null);
      this.inventaireCodeBarres.set('');
      this.inventaireQuantite.set(0);
      this.inventaireCommentaire.set('');
      await Promise.all([this.chargerLignesInventaire(inventaireId), this.chargerInventaires()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.inventoryCountSaved' });
    });
  }

  async cloturerInventaire(): Promise<void> {
    const inventaireId = this.inventaireSelectionneId();
    if (!inventaireId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectInventoryToClose' });
      return;
    }

    await this.executerAction('cloture-inventaire', async () => {
      const payload: CloseInventairePayload = { applyAdjustments: this.inventaireAppliquerAjustements() };
      await firstValueFrom(this.stockWorkflowService.closeInventaire(inventaireId, payload));
      await Promise.all([this.chargerInventaires(), this.chargerStocks(), this.chargerMouvements(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.inventoryClosed' });
    });
  }

  async validerTransfert(): Promise<void> {
    const transfertId = this.transfertSelectionneId();
    const transfert = this.transfertSelectionne();
    if (!transfertId || !transfert) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectTransferAndDepots' });
      return;
    }

    const [depotOrigineId, depotDestinationId] = await Promise.all([
      transfert.boutiqueOrigine ? this.resolveOuCreerDepotPourBoutique(transfert.boutiqueOrigine) : Promise.resolve(null),
      transfert.boutiqueDestination ? this.resolveOuCreerDepotPourBoutique(transfert.boutiqueDestination) : Promise.resolve(null),
    ]);
    if (!depotOrigineId || !depotDestinationId) {
      this.message.set({ type: 'danger', key: 'stockOperations.messages.actionFailed' });
      return;
    }

    await this.executerAction('validation-transfert', async () => {
      const payload: ValidateTransfertPayload = { depotOrigineId, depotDestinationId };
      await firstValueFrom(this.stockWorkflowService.validateTransfert(transfertId, payload));
      await Promise.all([this.chargerTransferts(), this.chargerStocks(), this.chargerMouvements(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.transferValidated' });
    });
  }

  async scannerTransfert(): Promise<void> {
    const transfertId = this.transfertSelectionneId();
    if (!transfertId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectTransferForScan' });
      return;
    }

    await this.executerAction('scan-transfert', async () => {
      const payload: ScanTransfertPayload = {
        produitId: this.transfertProduitId(),
        codeBarres: this.transfertCodeBarres().trim() || null,
        quantite: this.transfertQuantite(),
      };
      await firstValueFrom(this.stockWorkflowService.scanTransfert(transfertId, payload));
      this.transfertProduitId.set(null);
      this.transfertCodeBarres.set('');
      this.transfertQuantite.set(1);
      await this.chargerLignesTransfert(transfertId);
      this.message.set({ type: 'success', key: 'stockOperations.messages.transferLineAdded' });
    });
  }

  async reverserMouvement(): Promise<void> {
    const mouvementId = this.mouvementSelectionneId();
    if (!mouvementId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectMovementToReverse' });
      return;
    }

    await this.executerAction('reverse-mouvement', async () => {
      const payload: ReverseMouvementPayload = { motif: this.motifReverse().trim() || null };
      await firstValueFrom(this.stockWorkflowService.reverseMouvement(mouvementId, payload));
      this.motifReverse.set('');
      await Promise.all([this.chargerMouvements(), this.chargerStocks(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.movementReversed' });
    });
  }

  private async initialiser(): Promise<void> {
    this.chargement.set(true);
    try {
      await this.chargerBoutiquesEtDepots();
      this.initialiserBoutiqueParDefaut();
      await Promise.all([
        this.chargerGroupeArticles(),
        this.chargerProduits(),
        this.chargerStocks(),
        this.chargerReceptions(),
        this.chargerInventaires(),
        this.chargerTransferts(),
        this.chargerMouvements(),
        this.chargerAlertes(),
      ]);
      this.initialiserApprovisionnementDepuisRoute();
      this.initialiserApprovisionnementParDefaut();
    } finally {
      this.chargement.set(false);
    }
  }

  private async chargerBoutiquesEtDepots(): Promise<void> {
    const [boutiquesResponse, depotsResponse] = await Promise.all([
      firstValueFrom(this.boutiqueService.query({ size: 500, sort: ['nom,asc'] })),
      firstValueFrom(this.depotStockService.query({ size: 500, sort: ['code,asc'] })),
    ]);

    this.boutiques.set(boutiquesResponse.body ?? []);
    this.depots.set(depotsResponse.body ?? []);
  }

  private async chargerGroupeArticles(): Promise<void> {
    const response = await firstValueFrom(this.groupeArticleService.query({ size: 200, sort: ['libelle,asc'] }));
    this.groupeArticles.set(response.body ?? []);
  }

  private async chargerProduits(): Promise<void> {
    const response = await firstValueFrom(
      this.produitService.query({ ...this.paramsScopeBoutique(), size: 2000, sort: ['designation,asc'] }),
    );
    this.produits.set(response.body ?? []);
  }

  private async chargerStocks(): Promise<void> {
    const paramsDepot = this.paramsScopeDepot();
    const response = await firstValueFrom(
      this.stockProduitService.query({ ...paramsDepot, size: 2000, sort: ['dateDernierMouvement,desc'] }),
    );
    this.stocks.set(response.body ?? []);
  }

  private async chargerReceptions(): Promise<void> {
    const response = await firstValueFrom(
      this.receptionProduitService.query({ ...this.paramsScopeBoutique(), size: 100, sort: ['dateReception,desc'] }),
    );
    this.receptions.set(response.body ?? []);
  }

  private async chargerInventaires(): Promise<void> {
    const response = await firstValueFrom(
      this.inventaireStockService.query({ ...this.paramsScopeBoutique(), size: 100, sort: ['dateDebut,desc'] }),
    );
    this.inventaires.set(response.body ?? []);
  }

  private async chargerTransferts(): Promise<void> {
    if (this.peutVoirToutesBoutiques()) {
      const response = await firstValueFrom(this.transfertStockService.query({ size: 100, sort: ['dateTransfert,desc'] }));
      this.transferts.set(response.body ?? []);
      return;
    }

    const ids = this.permissionsUi.boutiqueIds();
    if (!ids.length) {
      this.transferts.set([]);
      return;
    }

    const idsParam = ids.join(',');
    const [originesResponse, destinationsResponse] = await Promise.all([
      firstValueFrom(this.transfertStockService.query({ 'boutiqueOrigineId.in': idsParam, size: 100, sort: ['dateTransfert,desc'] })),
      firstValueFrom(this.transfertStockService.query({ 'boutiqueDestinationId.in': idsParam, size: 100, sort: ['dateTransfert,desc'] })),
    ]);
    const transfertsParId = new Map<number, ITransfertStock>();
    [...(originesResponse.body ?? []), ...(destinationsResponse.body ?? [])].forEach(transfert => {
      transfertsParId.set(transfert.id, transfert);
    });
    this.transferts.set(Array.from(transfertsParId.values()).sort((a, b) => this.timestampTransfert(b) - this.timestampTransfert(a)));
  }

  private async chargerMouvements(): Promise<void> {
    const response = await firstValueFrom(
      this.mouvementStockService.query({ ...this.paramsScopeBoutique(), size: 120, sort: ['dateMouvement,desc'] }),
    );
    this.mouvements.set(response.body ?? []);
  }

  private async chargerAlertes(): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.dashboardReportingService.getStockAlerts({
          boutiqueId: this.boutiqueFiltreId() ?? undefined,
        }),
      );
      this.alertesStock.set(response);
    } catch {
      this.alertesStock.set([]);
    }
  }

  private async chargerLignesReception(receptionId: number): Promise<void> {
    const response = await firstValueFrom(
      this.ligneReceptionProduitService.query({ 'receptionId.equals': receptionId, size: 200, sort: ['id,desc'] }),
    );
    this.lignesReception.set(response.body ?? []);
  }

  private async chargerLignesInventaire(inventaireId: number): Promise<void> {
    const response = await firstValueFrom(
      this.ligneInventaireStockService.query({ 'inventaireId.equals': inventaireId, size: 200, sort: ['id,desc'] }),
    );
    this.lignesInventaire.set(response.body ?? []);
  }

  private async chargerLignesTransfert(transfertId: number): Promise<void> {
    const response = await firstValueFrom(
      this.ligneTransfertStockService.query({ 'transfertId.equals': transfertId, size: 200, sort: ['id,desc'] }),
    );
    this.lignesTransfert.set(response.body ?? []);
  }

  private async executerAction(cle: string, action: () => Promise<void>): Promise<void> {
    this.actionEnCours.set(cle);
    this.message.set(null);
    try {
      await action();
    } catch {
      this.message.set({
        type: 'danger',
        key: 'stockOperations.messages.actionFailed',
      });
    } finally {
      this.actionEnCours.set(null);
    }
  }

  private estDepotDansBoutique(depotId: number | null, boutiqueId: number | null): boolean {
    if (!boutiqueId || depotId === null) {
      return true;
    }
    return this.depots().some(depot => depot.id === depotId && depot.boutique?.id === boutiqueId);
  }

  private resolveDepotPourBoutique(boutiqueId: number | null): number | null {
    const depotsActifs = this.depots().filter(d => d.actif === true);
    if (!boutiqueId) {
      return depotsActifs[0]?.id ?? null;
    }
    return depotsActifs.find(depot => depot.boutique?.id === boutiqueId)?.id ?? null;
  }

  private async resolveOuCreerDepotPourBoutique(boutique: Pick<IBoutique, 'id' | 'nom' | 'code'>): Promise<number | null> {
    const existing = this.resolveDepotPourBoutique(boutique.id);
    if (existing) {
      return existing;
    }
    try {
      const depot = await firstValueFrom(
        this.depotStockService.create({
          id: null,
          code: `DPT-${boutique.id}`,
          libelle: `Dépôt ${boutique.nom ?? 'principal'}`.substring(0, 150),
          actif: true,
          emplacement: null,
          boutique: { id: boutique.id, nom: boutique.nom ?? '', code: boutique.code ?? '' },
        }),
      );
      if (depot?.id) {
        this.depots.update(depots => [...depots, depot]);
        return depot.id;
      }
      return null;
    } catch {
      return null;
    }
  }

  private filtrerParBoutiqueAccessible<T>(items: T[], getBoutiqueId: (item: T) => number | null | undefined): T[] {
    const boutiqueFiltre = this.boutiqueFiltreId();
    return items.filter(item => {
      const boutiqueId = getBoutiqueId(item);
      if (boutiqueFiltre && boutiqueId !== boutiqueFiltre) {
        return false;
      }
      return this.peutVoirToutesBoutiques() || (boutiqueId ? this.boutiqueIdsAccessibles().has(boutiqueId) : false);
    });
  }

  private initialiserBoutiqueParDefaut(): void {
    if (this.peutVoirToutesBoutiques() || this.boutiqueFiltreId()) {
      return;
    }

    const boutiques = this.boutiquesAccessibles();
    if (boutiques.length === 1 && boutiques[0].id) {
      this.boutiqueFiltreId.set(boutiques[0].id);
    }
  }

  private initialiserApprovisionnementDepuisRoute(): void {
    const queryParamMap = this.activatedRoute.snapshot.queryParamMap;
    const onglet = queryParamMap.get('onglet');
    const produitId = Number(queryParamMap.get('produitId'));

    if (onglet === 'approvisionnement' || (Number.isFinite(produitId) && produitId > 0)) {
      this.ongletActif.set('approvisionnement');
    }

    if (Number.isFinite(produitId) && produitId > 0) {
      const produit = this.produitsAccessibles().find(item => item.id === produitId);
      if (produit) {
        this.preparerApprovisionnement(produit);
      }
    }
  }

  private initialiserApprovisionnementParDefaut(): void {
    const boutiqueId = this.approvisionnementBoutiqueId() ?? this.boutiqueFiltreId() ?? this.boutiquesAccessibles()[0]?.id ?? null;
    if (!this.approvisionnementBoutiqueId() && boutiqueId) {
      this.approvisionnementBoutiqueId.set(boutiqueId);
    }
  }

  private reinitialiserSelectionsOperationnelles(): void {
    this.receptionSelectionneeId.set(null);
    this.inventaireSelectionneId.set(null);
    this.transfertSelectionneId.set(null);
    this.mouvementSelectionneId.set(null);
    this.lignesReception.set([]);
    this.lignesInventaire.set([]);
    this.lignesTransfert.set([]);
  }

  private paramsScopeBoutique(): QueryParams {
    if (this.peutVoirToutesBoutiques()) {
      return {};
    }

    const ids = this.permissionsUi.boutiqueIds();
    return ids.length ? { 'boutiqueId.in': ids.join(',') } : { 'boutiqueId.equals': -1 };
  }

  private paramsScopeDepot(): QueryParams {
    if (this.peutVoirToutesBoutiques()) {
      return {};
    }

    const ids = this.depots()
      .filter(depot => {
        const boutiqueId = depot.boutique?.id;
        return boutiqueId ? this.boutiqueIdsAccessibles().has(boutiqueId) : false;
      })
      .map(depot => depot.id)
      .filter((id): id is number => typeof id === 'number');
    return ids.length ? { 'depotId.in': ids.join(',') } : { 'depotId.equals': -1 };
  }

  private timestampTransfert(transfert: ITransfertStock): number {
    return transfert.dateTransfert?.valueOf() ?? 0;
  }
}
