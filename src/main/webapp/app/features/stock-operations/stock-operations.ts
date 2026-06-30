import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

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

type OngletStock = 'stock' | 'receptions' | 'inventaires' | 'transferts';

interface MessageStock {
  type: 'success' | 'danger' | 'info';
  key: string;
}

type QueryParams = Record<string, string | number | boolean | readonly string[]>;

@Component({
  selector: 'jhi-stock-operations',
  templateUrl: './stock-operations.html',
  imports: [FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class StockOperationsComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  readonly ongletActif = signal<OngletStock>('stock');
  readonly chargement = signal(false);
  readonly actionEnCours = signal<string | null>(null);
  readonly message = signal<MessageStock | null>(null);

  readonly boutiques = signal<IBoutique[]>([]);
  readonly depots = signal<IDepotStock[]>([]);
  readonly produits = signal<IProduit[]>([]);
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
  readonly depotFiltreId = signal<number | null>(null);
  readonly rechercheStock = signal('');

  readonly receptionSelectionneeId = signal<number | null>(null);
  readonly inventaireSelectionneId = signal<number | null>(null);
  readonly transfertSelectionneId = signal<number | null>(null);
  readonly mouvementSelectionneId = signal<number | null>(null);

  readonly receptionProduitId = signal<number | null>(null);
  readonly receptionCodeBarres = signal('');
  readonly receptionQuantite = signal<number>(1);
  readonly receptionDepotValidationId = signal<number | null>(null);

  readonly inventaireProduitId = signal<number | null>(null);
  readonly inventaireCodeBarres = signal('');
  readonly inventaireQuantite = signal<number>(0);
  readonly inventaireCommentaire = signal('');
  readonly inventaireAppliquerAjustements = signal(true);

  readonly transfertDepotOrigineId = signal<number | null>(null);
  readonly transfertDepotDestinationId = signal<number | null>(null);
  readonly transfertProduitId = signal<number | null>(null);
  readonly transfertCodeBarres = signal('');
  readonly transfertQuantite = signal(1);
  readonly motifReverse = signal('');

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
  readonly depotsAccessibles = computed(() => {
    const boutiqueFiltre = this.boutiqueFiltreId();
    return this.depots().filter(depot => {
      if (boutiqueFiltre && depot.boutique?.id !== boutiqueFiltre) {
        return false;
      }
      return this.peutVoirToutesBoutiques() || (depot.boutique?.id ? this.boutiqueIdsAccessibles().has(depot.boutique.id) : false);
    });
  });
  readonly stocksFiltres = computed(() => {
    const recherche = this.rechercheStock().trim().toLowerCase();
    const depotsAccessibles = new Set(this.depotsAccessibles().map(depot => depot.id));
    return this.stocks().filter(stock => {
      if (stock.depot?.id && !depotsAccessibles.has(stock.depot.id)) {
        return false;
      }
      if (this.boutiqueFiltreId() && stock.depot?.id && !this.estDepotDansBoutique(stock.depot.id, this.boutiqueFiltreId())) {
        return false;
      }
      if (this.depotFiltreId() && stock.depot?.id !== this.depotFiltreId()) {
        return false;
      }
      if (!recherche) {
        return true;
      }
      return [stock.produit?.designation, stock.depot?.code].filter(Boolean).some(valeur => valeur!.toLowerCase().includes(recherche));
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
  readonly depotsFiltres = this.depotsAccessibles;
  readonly receptionSelectionnee = computed(
    () => this.receptionsAccessibles().find(item => item.id === this.receptionSelectionneeId()) ?? null,
  );
  readonly inventaireSelectionne = computed(
    () => this.inventairesAccessibles().find(item => item.id === this.inventaireSelectionneId()) ?? null,
  );
  readonly transfertSelectionne = computed(
    () => this.transfertsAccessibles().find(item => item.id === this.transfertSelectionneId()) ?? null,
  );
  readonly depotsOrigineTransfert = computed(() => {
    const boutiqueId = this.transfertSelectionne()?.boutiqueOrigine?.id;
    return this.depotsAccessibles().filter(depot => !boutiqueId || depot.boutique?.id === boutiqueId);
  });
  readonly depotsDestinationTransfert = computed(() => {
    const boutiqueId = this.transfertSelectionne()?.boutiqueDestination?.id;
    return this.depotsAccessibles().filter(depot => !boutiqueId || depot.boutique?.id === boutiqueId);
  });
  readonly produitsTransfert = computed(() => {
    const boutiqueId = this.transfertSelectionne()?.boutiqueOrigine?.id;
    return this.produitsAccessibles().filter(produit => !boutiqueId || produit.boutique?.id === boutiqueId);
  });
  readonly mouvementSelectionne = computed(
    () => this.mouvementsAccessibles().find(item => item.id === this.mouvementSelectionneId()) ?? null,
  );

  private readonly boutiqueService = inject(BoutiqueService);
  private readonly depotStockService = inject(DepotStockService);
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

  definirOnglet(onglet: OngletStock): void {
    this.ongletActif.set(onglet);
  }

  changerBoutiqueFiltre(boutiqueId: number | null): void {
    this.boutiqueFiltreId.set(boutiqueId);
    if (this.depotFiltreId() && !this.depotsAccessibles().some(depot => depot.id === this.depotFiltreId())) {
      this.depotFiltreId.set(null);
    }
    this.reinitialiserSelectionsOperationnelles();
    void this.chargerAlertes();
  }

  changerDepotFiltre(depotId: number | null): void {
    this.depotFiltreId.set(depotId);
    void this.chargerAlertes();
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
    const depotId = this.receptionDepotValidationId();
    if (!receptionId || !depotId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectReceptionAndDepot' });
      return;
    }

    await this.executerAction('validation-reception', async () => {
      const payload: ValidateReceptionPayload = { depotId };
      await firstValueFrom(this.stockWorkflowService.validateReception(receptionId, payload));
      await Promise.all([this.chargerReceptions(), this.chargerStocks(), this.chargerAlertes()]);
      this.message.set({ type: 'success', key: 'stockOperations.messages.receptionValidated' });
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
    const depotOrigineId = this.transfertDepotOrigineId();
    const depotDestinationId = this.transfertDepotDestinationId();
    if (!transfertId || !depotOrigineId || !depotDestinationId) {
      this.message.set({ type: 'info', key: 'stockOperations.messages.selectTransferAndDepots' });
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
        this.chargerProduits(),
        this.chargerStocks(),
        this.chargerReceptions(),
        this.chargerInventaires(),
        this.chargerTransferts(),
        this.chargerMouvements(),
        this.chargerAlertes(),
      ]);
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
          depotId: this.depotFiltreId() ?? undefined,
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

  private estDepotDansBoutique(depotId: number, boutiqueId: number | null): boolean {
    if (!boutiqueId) {
      return true;
    }
    return this.depots().some(depot => depot.id === depotId && depot.boutique?.id === boutiqueId);
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

    const ids = this.depotsAccessibles()
      .map(depot => depot.id)
      .filter((id): id is number => typeof id === 'number');
    return ids.length ? { 'depotId.in': ids.join(',') } : { 'depotId.equals': -1 };
  }

  private timestampTransfert(transfert: ITransfertStock): number {
    return transfert.dateTransfert?.valueOf() ?? 0;
  }
}
