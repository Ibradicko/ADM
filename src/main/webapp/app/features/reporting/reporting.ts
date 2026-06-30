import dayjs from 'dayjs/esm';

import { Component, OnInit, WritableSignal, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';

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
}

@Component({
  selector: 'jhi-reporting',
  templateUrl: './reporting.html',
  imports: [FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class ReportingComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  readonly typesRapport: ReadonlyArray<TypeRapportOption> = [
    {
      valeur: 'ventes-par-jour',
      labelKey: 'reporting.types.salesByDay.label',
      descriptionKey: 'reporting.types.salesByDay.description',
    },
    {
      valeur: 'alertes-stock',
      labelKey: 'reporting.types.stockAlerts.label',
      descriptionKey: 'reporting.types.stockAlerts.description',
    },
    {
      valeur: 'redevances',
      labelKey: 'reporting.types.royalties.label',
      descriptionKey: 'reporting.types.royalties.description',
    },
    {
      valeur: 'scans-inconnus',
      labelKey: 'reporting.types.unknownScans.label',
      descriptionKey: 'reporting.types.unknownScans.description',
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

  readonly typeActif = computed(() => this.typesRapport.find(option => option.valeur === this.typeRapport()) ?? this.typesRapport[0]);
  readonly previewDisponible = computed(() => !!this.apercu()?.export?.id);
  readonly apercuTexte = computed(() => this.apercu()?.preview ?? '');
  readonly depotsFiltres = computed(() => this.depots().filter(depot => !this.boutiqueId() || depot.boutique?.id === this.boutiqueId()));
  readonly produitsFiltres = computed(() =>
    this.produits().filter(produit => !this.boutiqueId() || produit.boutique?.id === this.boutiqueId()),
  );

  private readonly dashboardReportingService = inject(AdmDashboardReportingService);
  private readonly rapportExportService = inject(RapportExportService);
  private readonly boutiqueService = inject(BoutiqueService);
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

  async genererApercu(): Promise<void> {
    this.generationEnCours.set(true);
    this.message.set(null);
    try {
      const payload: GenerateRapportExportPayload = {
        typeRapport: this.typeRapport(),
        format: this.format(),
        periodeDebut: this.dateDebut() || null,
        periodeFin: this.dateFin() || null,
        boutiqueId: this.boutiqueId(),
        locataireId: this.locataireId(),
        depotId: this.depotId(),
        produitId: this.produitId(),
        statutVente: this.typeRapport() === 'ventes-par-jour' ? this.statutVente() : null,
        minMontantNet: this.typeRapport() === 'ventes-par-jour' ? this.minMontantNet() : null,
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

    await Promise.all([
      this.chargerUnReferentiel(this.boutiqueService.query({ size: 500, sort: ['nom,asc'] }), this.boutiques),
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

  private async chargerUnReferentiel<T>(request: Observable<HttpResponse<T[]>>, target: WritableSignal<T[]>): Promise<void> {
    try {
      const response = await firstValueFrom(request);
      target.set(response.body ?? []);
    } catch {
      target.set([]);
    }
  }

  private async chargerExportsRecents(): Promise<void> {
    const response = await firstValueFrom(this.rapportExportService.query({ size: 20, sort: ['dateGeneration,desc'] }));
    this.exportsRecents.set(response.body ?? []);
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

  private extraireNomFichier(contentDisposition: string): string | null {
    const utfMatch = /filename\*=UTF-8''([^;]+)/i.exec(contentDisposition);
    if (utfMatch?.[1]) {
      return decodeURIComponent(utfMatch[1]);
    }

    const standardMatch = /filename="?([^"]+)"?/i.exec(contentDisposition);
    return standardMatch?.[1] ?? null;
  }

  private localeCourante(): string {
    return this.translateService.currentLang === 'en' ? 'en-US' : 'fr-FR';
  }
}
