import dayjs from 'dayjs/esm';

import { Component, OnInit, WritableSignal, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable, firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import {
  AdmDashboardReportingService,
  DashboardStockAlertResponse,
  GenerateRapportExportPayload,
  RapportExportPreviewResponse,
} from 'app/core/services/adm-dashboard-reporting.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IRapportExport } from 'app/entities/rapport-export/rapport-export.model';
import { RapportExportService } from 'app/entities/rapport-export/service/rapport-export.service';
import { TranslateDirective } from 'app/shared/language';

interface MessageReporting {
  type: 'success' | 'danger' | 'info';
  key: string;
  params?: Record<string, unknown>;
}

interface TypeRapportOption {
  valeur: string;
  labelKey: string;
  descriptionKey: string;
  icon: string;
  tone: 'sales' | 'stock' | 'royalty' | 'audit';
}

interface RapportPreviewResume {
  label: string;
  valeur: string;
}

interface RapportPreviewStructure {
  titre: string;
  resume: RapportPreviewResume[];
  entetes: string[];
  lignes: string[][];
  noteComplementaire: string | null;
}

const RAPPORT_PREVIEW_VIDE: RapportPreviewStructure = { titre: '', resume: [], entetes: [], lignes: [], noteComplementaire: null };

@Component({
  selector: 'jhi-reporting',
  templateUrl: './reporting.html',
  styleUrl: './reporting.scss',
  imports: [FormsModule, RouterLink, FontAwesomeModule, TranslateDirective, TranslateModule],
})
export default class ReportingComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  readonly typesRapport: readonly TypeRapportOption[] = [
    {
      valeur: 'ventes-par-jour',
      labelKey: 'reporting.types.salesByDay.label',
      descriptionKey: 'reporting.types.salesByDay.description',
      icon: 'chart-line',
      tone: 'sales',
    },
    {
      valeur: 'alertes-stock',
      labelKey: 'reporting.types.stockAlerts.label',
      descriptionKey: 'reporting.types.stockAlerts.description',
      icon: 'database',
      tone: 'stock',
    },
    {
      valeur: 'redevances',
      labelKey: 'reporting.types.royalties.label',
      descriptionKey: 'reporting.types.royalties.description',
      icon: 'coins',
      tone: 'royalty',
    },
    {
      valeur: 'scans-inconnus',
      labelKey: 'reporting.types.unknownScans.label',
      descriptionKey: 'reporting.types.unknownScans.description',
      icon: 'barcode',
      tone: 'audit',
    },
  ];
  readonly boutiques = signal<IBoutique[]>([]);
  readonly locataires = signal<ILocataire[]>([]);
  readonly depots = signal<IDepotStock[]>([]);
  readonly produits = signal<IProduit[]>([]);
  readonly exportsRecents = signal<IRapportExport[]>([]);
  readonly apercu = signal<RapportExportPreviewResponse | null>(null);
  readonly alertesStock = signal<DashboardStockAlertResponse[]>([]);
  readonly chargement = signal(false);
  readonly generationEnCours = signal(false);
  readonly telechargementEnCours = signal<number | null>(null);
  readonly modalApercuOuverte = signal(false);
  readonly message = signal<MessageReporting | null>(null);

  readonly typeRapport = signal<string>('ventes-par-jour');
  readonly format = signal<'PDF' | 'EXCEL'>('PDF');
  readonly dateDebut = signal<string>(dayjs().subtract(6, 'day').format('YYYY-MM-DD'));
  readonly dateFin = signal<string>(dayjs().format('YYYY-MM-DD'));
  readonly boutiqueId = signal<number | null>(null);
  readonly locataireId = signal<number | null>(null);
  readonly depotId = signal<number | null>(null);
  readonly produitId = signal<number | null>(null);
  readonly statutVente = signal<string>('VALIDEE');
  readonly minMontantNet = signal<number | null>(null);

  readonly typesRapportDisponibles = computed(() =>
    this.typesRapport.filter(type => {
      if (type.valeur === 'alertes-stock') {
        return this.permissionsUi.peutLireStock();
      }
      if (type.valeur === 'redevances') {
        return this.permissionsUi.peutLireRedevances();
      }
      if (type.valeur === 'scans-inconnus') {
        return this.permissionsUi.peutLireAudit();
      }
      return this.permissionsUi.peutLireReporting();
    }),
  );
  readonly typeActif = computed(
    () =>
      this.typesRapportDisponibles().find(option => option.valeur === this.typeRapport()) ??
      this.typesRapportDisponibles()[0] ??
      this.typesRapport[0],
  );
  readonly peutGenererRapport = computed(() => this.permissionsUi.peutLireReporting() && this.typesRapportDisponibles().length > 0);
  readonly peutTelechargerRapport = computed(() => this.permissionsUi.peutLireReporting() && this.typesRapportDisponibles().length > 0);
  readonly peutFiltrerLocataire = computed(() => this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm());
  readonly peutFiltrerStock = computed(() => this.permissionsUi.peutLireStock());
  readonly previewDisponible = computed(() => !!this.apercu()?.export?.id);
  readonly apercuTexte = computed(() => this.apercu()?.preview ?? '');
  readonly apercuStructure = computed<RapportPreviewStructure>(() => this.analyserApercu(this.apercuTexte()));
  readonly depotsFiltres = computed(() => this.depots().filter(depot => !this.boutiqueId() || depot.boutique?.id === this.boutiqueId()));
  readonly produitsFiltres = computed(() =>
    this.produits().filter(produit => !this.boutiqueId() || produit.boutique?.id === this.boutiqueId()),
  );
  readonly modeAccesKey = computed(() => {
    if (this.permissionsUi.estAdmin()) {
      return 'reporting.scopeMode.admin';
    }
    if (this.permissionsUi.estProfilAdm()) {
      return 'reporting.scopeMode.managerAdm';
    }
    if (this.permissionsUi.estLocataire()) {
      return 'reporting.scopeMode.tenant';
    }
    if (this.permissionsUi.estProfilBoutique()) {
      return 'reporting.scopeMode.shopManager';
    }
    return 'reporting.scopeMode.readOnly';
  });
  readonly roleHintKey = computed(() => {
    if (this.permissionsUi.peutExporterReporting()) {
      return 'reporting.roleHint.exporter';
    }
    if (this.permissionsUi.estLocataire() || this.permissionsUi.estProfilBoutique()) {
      return 'reporting.roleHint.scoped';
    }
    return 'reporting.roleHint.limited';
  });
  readonly resumeFiltres = computed(() => {
    const fragments = [
      this.typeActif().labelKey,
      this.format(),
      this.dateDebut() && this.dateFin() ? `${this.dateDebut()} - ${this.dateFin()}` : null,
    ].filter(Boolean);

    return fragments.join(' / ');
  });

  private readonly dashboardReportingService = inject(AdmDashboardReportingService);
  private readonly rapportExportService = inject(RapportExportService);
  private readonly boutiqueService = inject(BoutiqueService);
  private readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);
  private readonly locataireService = inject(LocataireService);
  private readonly depotStockService = inject(DepotStockService);
  private readonly produitService = inject(ProduitService);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    void this.initialiser();
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString(this.localeCourante())} F CFA` : '--';
  }

  formatDate(valeur: string | null | undefined): string {
    return valeur ? dayjs(valeur).format('DD/MM/YYYY HH:mm') : '--';
  }

  choisirTypeRapport(valeur: string): void {
    this.typeRapport.set(valeur);
    this.normaliserTypeRapport();
  }

  async genererApercu(): Promise<void> {
    this.normaliserTypeRapport();
    if (!this.peutGenererRapport()) {
      this.message.set({ type: 'danger', key: 'reporting.messages.generateError' });
      return;
    }
    this.generationEnCours.set(true);
    this.message.set(null);
    try {
      const typeRapport = this.typeRapport();
      const payload: GenerateRapportExportPayload = {
        typeRapport,
        format: this.format(),
        periodeDebut: this.dateDebut() || null,
        periodeFin: this.dateFin() || null,
        boutiqueId: this.boutiqueId(),
        locataireId: this.peutFiltrerLocataire() ? this.locataireId() : null,
        depotId: this.peutFiltrerStock() ? this.depotId() : null,
        produitId: this.peutFiltrerStock() ? this.produitId() : null,
        statutVente: typeRapport === 'ventes-par-jour' ? this.statutVente() : null,
        minMontantNet: typeRapport === 'ventes-par-jour' ? this.minMontantNet() : null,
      };

      const apercu = await firstValueFrom(this.dashboardReportingService.generateExport(payload));
      this.apercu.set(apercu);
      this.modalApercuOuverte.set(true);
      this.message.set({
        type: 'success',
        key: 'reporting.messages.generated',
        params: { type: this.translateService.instant(this.typeActif().labelKey).toLowerCase() },
      });
      await Promise.all([this.chargerExportsRecents(), this.chargerAlertesStock()]);
    } catch {
      this.message.set({
        type: 'danger',
        key: 'reporting.messages.generateError',
      });
    } finally {
      this.generationEnCours.set(false);
    }
  }

  async ouvrirApercu(rapportExportId: number): Promise<void> {
    this.message.set(null);
    try {
      const apercu = await firstValueFrom(this.dashboardReportingService.previewExport(rapportExportId));
      this.apercu.set(apercu);
      this.modalApercuOuverte.set(true);
      this.message.set({
        type: 'info',
        key: 'reporting.messages.previewLoaded',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'reporting.messages.previewError',
      });
    }
  }

  ouvrirApercuGrandFormat(): void {
    if (!this.previewDisponible()) {
      this.message.set({ type: 'info', key: 'reporting.messages.previewRequired' });
      return;
    }

    this.modalApercuOuverte.set(true);
  }

  fermerApercuGrandFormat(): void {
    this.modalApercuOuverte.set(false);
  }

  async telechargerApercuCourant(): Promise<void> {
    const rapportExportId = this.apercu()?.export?.id;
    if (!rapportExportId) {
      this.message.set({ type: 'info', key: 'reporting.messages.noExportFile' });
      return;
    }

    await this.telechargerExport(rapportExportId);
  }

  async telechargerExport(rapportExportId: number): Promise<void> {
    this.telechargementEnCours.set(rapportExportId);
    this.message.set(null);
    try {
      const response = await firstValueFrom(this.dashboardReportingService.downloadExport(rapportExportId));
      const contenu = response.body;
      if (!contenu) {
        throw new Error('No file received');
      }

      const contentDisposition = response.headers.get('content-disposition') ?? '';
      const nomFichier = this.extraireNomFichier(contentDisposition) ?? `rapport-${rapportExportId}.${this.format().toLowerCase()}`;
      const url = window.URL.createObjectURL(contenu);
      const lien = document.createElement('a');
      lien.href = url;
      lien.download = nomFichier;
      document.body.appendChild(lien);
      lien.click();
      lien.remove();
      window.URL.revokeObjectURL(url);
      this.message.set({
        type: 'success',
        key: 'reporting.messages.downloaded',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'reporting.messages.downloadError',
      });
    } finally {
      this.telechargementEnCours.set(null);
    }
  }

  private async initialiser(): Promise<void> {
    this.chargement.set(true);
    try {
      this.normaliserTypeRapport();
      await Promise.all([this.chargerReferentiels(), this.chargerExportsRecents(), this.chargerAlertesStock()]);
    } finally {
      this.chargement.set(false);
    }
  }

  // Each referentiel is gated by its own backend permission (GET /api/locataires needs
  // canManageBoutiques(), GET /api/produits needs canReadStock(), neither of which a
  // locataire/vendeur satisfies). Skip calls a role is known not to have rights to
  // rather than firing-then-catching: a 403 is still logged to the console by the
  // browser itself even when the app handles it, which would make those filters
  // permanently noisy for those roles.
  private async chargerReferentiels(): Promise<void> {
    const peutLireLocataires = this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm();
    // DepotStockResource and ProduitResource are both class-level @PreAuthorize(canReadStock()).
    const peutLireStock = this.permissionsUi.peutLireStock();
    const boutiquesRequest = this.chargerBoutiquesSelonPerimetre();

    await Promise.all([
      boutiquesRequest,
      peutLireLocataires
        ? this.chargerUnReferentiel(this.locataireService.query({ size: 500, sort: ['nom,asc'] }), this.locataires)
        : Promise.resolve(this.locataires.set([])),
      peutLireStock
        ? this.chargerUnReferentiel(this.depotStockService.query({ size: 500, sort: ['code,asc'] }), this.depots)
        : Promise.resolve(this.depots.set([])),
      peutLireStock
        ? this.chargerUnReferentiel(this.produitService.query({ size: 2000, sort: ['designation,asc'] }), this.produits)
        : Promise.resolve(this.produits.set([])),
    ]);
  }

  private async chargerBoutiquesSelonPerimetre(): Promise<void> {
    if (this.permissionsUi.estLocataire()) {
      try {
        const exploitations = await firstValueFrom(this.exploitationBoutiqueService.findMesExploitations());
        const boutiques = exploitations
          .filter(exploitation => exploitation.statut === 'ACTIF' && exploitation.boutique)
          .map(exploitation => exploitation.boutique!)
          .filter((boutique, index, collection) => collection.findIndex(candidate => candidate.id === boutique.id) === index)
          .sort((left, right) => (left.nom ?? '').localeCompare(right.nom ?? ''));
        this.boutiques.set(boutiques);
        this.verrouillerBoutiqueSiUnique();
      } catch {
        this.boutiques.set([]);
      }
      return;
    }

    const boutiqueIds = this.permissionsUi.boutiqueIds();
    const params =
      this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm() || boutiqueIds.length === 0
        ? { size: 500, sort: ['nom,asc'] }
        : { 'id.in': boutiqueIds.join(','), size: 500, sort: ['nom,asc'] };
    await this.chargerUnReferentiel(this.boutiqueService.query(params), this.boutiques);
    this.verrouillerBoutiqueSiUnique();
  }

  private async chargerUnReferentiel<T>(request: Observable<HttpResponse<T[]>>, target: WritableSignal<T[]>): Promise<void> {
    try {
      const response = await firstValueFrom(request);
      target.set(response.body ?? []);
    } catch {
      target.set([]);
    }
  }

  private async chargerExportsRecents(): Promise<void> {
    try {
      const response = await firstValueFrom(this.rapportExportService.query({ size: 20, sort: ['dateGeneration,desc'] }));
      this.exportsRecents.set(response.body ?? []);
    } catch {
      this.exportsRecents.set([]);
    }
  }

  private async chargerAlertesStock(): Promise<void> {
    // GET /api/dashboard/stock-alerts requires canReadStock() (admin/manager boutique
    // only); skip the call entirely for other roles instead of firing-then-catching,
    // since the browser logs the 403 to the console regardless of the app's own handling.
    if (!this.permissionsUi.peutLireStock()) {
      this.alertesStock.set([]);
      return;
    }
    try {
      const alertes = await firstValueFrom(
        this.dashboardReportingService.getStockAlerts({
          boutiqueId: this.boutiqueId() ?? undefined,
          depotId: this.depotId() ?? undefined,
          produitId: this.produitId() ?? undefined,
        }),
      );
      this.alertesStock.set(alertes);
    } catch {
      this.alertesStock.set([]);
    }
  }

  // Le backend renvoie l apercu sous forme de texte brut (titre, puis lignes
  // "Libelle: valeur", puis une ligne d entetes et des lignes de donnees jointes
  // par " | "). On le reanalyse ici pour l afficher comme un tableau plutot
  // qu un bloc de texte preformate.
  private analyserApercu(texte: string): RapportPreviewStructure {
    const PIPE = ' | ';
    const lignesBrutes = texte.split('\n');
    if (lignesBrutes.length === 0) {
      return RAPPORT_PREVIEW_VIDE;
    }

    const [titre, ...reste] = lignesBrutes;
    const resume: RapportPreviewResume[] = [];
    let index = 0;
    while (index < reste.length && !reste[index].includes(PIPE)) {
      const ligne = reste[index];
      const separateur = ligne.indexOf(': ');
      if (separateur > -1) {
        resume.push({ label: ligne.slice(0, separateur), valeur: ligne.slice(separateur + 2) });
      } else if (ligne) {
        resume.push({ label: '', valeur: ligne });
      }
      index++;
    }

    if (index >= reste.length) {
      return { titre, resume, entetes: [], lignes: [], noteComplementaire: null };
    }

    const entetes = reste[index].split(PIPE);
    index++;

    const lignes: string[][] = [];
    let noteComplementaire: string | null = null;
    while (index < reste.length) {
      const ligne = reste[index];
      if (ligne.includes(PIPE)) {
        lignes.push(ligne.split(PIPE));
      } else if (ligne.startsWith('...')) {
        noteComplementaire = ligne;
      }
      index++;
    }

    return { titre, resume, entetes, lignes, noteComplementaire };
  }

  private extraireNomFichier(contentDisposition: string): string | null {
    const utfMatch = /filename\*=UTF-8''([^;]+)/i.exec(contentDisposition);
    if (utfMatch?.[1]) {
      return decodeURIComponent(utfMatch[1]);
    }

    const standardMatch = /filename="?([^"]+)"?/i.exec(contentDisposition);
    return standardMatch?.[1] ?? null;
  }

  private localeCourante(): string {
    return this.translateService.getCurrentLang() === 'en' ? 'en-US' : 'fr-FR';
  }

  private normaliserTypeRapport(): void {
    const typeDisponible = this.typesRapportDisponibles();
    if (!typeDisponible.some(type => type.valeur === this.typeRapport())) {
      this.typeRapport.set(typeDisponible[0]?.valeur ?? 'ventes-par-jour');
    }
    if (!this.peutFiltrerStock()) {
      this.depotId.set(null);
      this.produitId.set(null);
    }
    if (!this.peutFiltrerLocataire()) {
      this.locataireId.set(null);
    }
  }

  private verrouillerBoutiqueSiUnique(): void {
    const boutiques = this.boutiques();
    if (boutiques.length === 1 && boutiques[0].id) {
      this.boutiqueId.set(boutiques[0].id);
    }
  }
}
