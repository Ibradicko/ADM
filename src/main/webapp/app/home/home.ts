import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import {
  AdmDashboardReportingService,
  DashboardOverviewResponse,
  DashboardRoyaltyByGroupeArticleResponse,
  DashboardSalesByDayPointResponse,
  DashboardStockAlertResponse,
} from 'app/core/services/adm-dashboard-reporting.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { ILigneVente } from 'app/entities/ligne-vente/ligne-vente.model';
import { LigneVenteService } from 'app/entities/ligne-vente/service/ligne-vente.service';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { IExploitationBoutique } from 'app/entities/exploitation-boutique/exploitation-boutique.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { IVente } from 'app/entities/vente/vente.model';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { TranslateDirective } from 'app/shared/language';

interface DashboardMetric {
  labelKey: string;
  value: string;
  tone: 'primary' | 'success' | 'warning' | 'neutral';
  featured?: boolean;
}

interface TrendPoint {
  label: string;
  value: number;
}

interface RankingRow {
  name: string;
  detail: string;
  value: string;
}

interface TenantShopSummary {
  boutiqueId: number;
  name: string;
  detail: string;
  netSales: string;
  grossSales: string;
  validatedSales: string;
  pendingRoyalties: string;
}

interface ProductRow {
  rank: number;
  product: string;
  boutique: string;
  sales: number;
  revenue: string;
  trend: string;
}

interface QuickAction {
  labelKey: string;
  route: string;
  icon: string;
  visible: boolean;
}

type DashboardMode = 'ADMIN' | 'LOCATAIRE' | 'BOUTIQUE' | 'VENDEUR';

const EMPTY_OVERVIEW: DashboardOverviewResponse = {
  grossSales: 0,
  netSales: 0,
  validatedSalesCount: 0,
  pendingSalesCount: 0,
  stockAlertCount: 0,
  unresolvedUnknownScans: 0,
  royaltyOutstandingAmount: 0,
};

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink, FormsModule, FontAwesomeModule, TranslateDirective, TranslateModule],
})
export default class Home implements OnInit {
  public readonly account = inject(AccountService).account;
  public readonly permissionsUi = inject(UiPermissionService);
  public readonly isAuthenticated = computed(() => this.account() !== null);
  public readonly dashboardMode = computed<DashboardMode>(() => {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm() || this.permissionsUi.estProfilSupervision()) {
      return 'ADMIN';
    }
    if (this.permissionsUi.estLocataire()) {
      return 'LOCATAIRE';
    }
    if (this.permissionsUi.estProfilBoutique()) {
      return 'BOUTIQUE';
    }
    return 'VENDEUR';
  });
  public readonly isTenantDashboard = computed(() => this.dashboardMode() === 'LOCATAIRE');
  public readonly heroPrefix = computed(() => {
    switch (this.dashboardMode()) {
      case 'LOCATAIRE':
        return 'tenant';
      case 'BOUTIQUE':
        return 'boutique';
      case 'VENDEUR':
        return 'vendeur';
      default:
        return 'hero';
    }
  });
  public readonly chargement = signal(false);
  public readonly messageKey = signal<string | null>(null);

  public readonly boutiques = signal<IBoutique[]>([]);
  public readonly locataires = signal<ILocataire[]>([]);
  public readonly exploitationsLocataire = signal<IExploitationBoutique[]>([]);
  public readonly overview = signal<DashboardOverviewResponse | null>(null);
  public readonly salesByDay = signal<DashboardSalesByDayPointResponse[]>([]);
  public readonly stockAlerts = signal<DashboardStockAlertResponse[]>([]);
  public readonly royaltyByGroupeArticle = signal<DashboardRoyaltyByGroupeArticleResponse[]>([]);
  public readonly ventesPeriode = signal<IVente[]>([]);
  public readonly lignesPeriode = signal<ILigneVente[]>([]);
  public readonly tenantShopSummaries = signal<TenantShopSummary[]>([]);

  public readonly dateDebut = signal(dayjs().subtract(29, 'day').format('YYYY-MM-DD'));
  public readonly dateFin = signal(dayjs().format('YYYY-MM-DD'));
  public readonly boutiqueId = signal<number | null>(null);
  public readonly locataireId = signal<number | null>(null);

  public readonly metrics = computed<DashboardMetric[]>(() => {
    const overview = this.overview();
    const mode = this.dashboardMode();

    if (mode === 'LOCATAIRE') {
      return [
        { labelKey: 'home.dashboard.metrics.netSales', value: this.formatMontant(overview?.netSales), tone: 'success' },
        { labelKey: 'home.dashboard.metrics.validatedSales', value: this.formatNombre(overview?.validatedSalesCount), tone: 'primary' },
        { labelKey: 'home.dashboard.metrics.activeShops', value: this.formatNombre(this.activeTenantShops().length), tone: 'neutral' },
        {
          labelKey: 'home.dashboard.metrics.pendingRoyalties',
          value: this.formatMontant(overview?.royaltyOutstandingAmount),
          tone: 'warning',
        },
      ];
    }

    if (mode === 'VENDEUR') {
      return [
        { labelKey: 'home.dashboard.metrics.netSales', value: this.formatMontant(overview?.netSales), tone: 'success', featured: true },
        { labelKey: 'home.dashboard.metrics.grossSales', value: this.formatMontant(overview?.grossSales), tone: 'primary' },
        { labelKey: 'home.dashboard.metrics.validatedSales', value: this.formatNombre(overview?.validatedSalesCount), tone: 'primary' },
      ];
    }

    return [
      { labelKey: 'home.dashboard.metrics.grossSales', value: this.formatMontant(overview?.grossSales), tone: 'primary' },
      { labelKey: 'home.dashboard.metrics.netSales', value: this.formatMontant(overview?.netSales), tone: 'success' },
      { labelKey: 'home.dashboard.metrics.validatedSales', value: this.formatNombre(overview?.validatedSalesCount), tone: 'neutral' },
      {
        labelKey: 'home.dashboard.metrics.pendingRoyalties',
        value: this.formatMontant(overview?.royaltyOutstandingAmount),
        tone: 'warning',
      },
    ];
  });
  public readonly afficherRedevanceParGroupe = computed(() => this.dashboardMode() === 'ADMIN' && this.permissionsUi.peutLireRedevances());
  public readonly activeTenantShops = computed(() => this.exploitationsLocataire().filter(exploitation => exploitation.statut === 'ACTIF'));
  public readonly tenantShopRows = computed<RankingRow[]>(() =>
    this.exploitationsLocataire().map(exploitation => ({
      name: exploitation.boutique?.nom ?? this.traduction('home.dashboard.fallbacks.unknownBoutique'),
      detail: [
        exploitation.numeroContrat
          ? this.traduction('home.dashboard.tenant.contract', { contract: exploitation.numeroContrat })
          : this.traduction('home.dashboard.tenant.noContract'),
        this.traduction(`home.dashboard.tenant.status.${exploitation.statut ?? 'UNKNOWN'}`),
      ].join(' / '),
      value: exploitation.dateDebut?.format('DD/MM/YYYY') ?? '--',
    })),
  );
  public readonly periodLabel = computed(() => {
    const start = this.dateDebut() ? dayjs(this.dateDebut()).format('DD/MM/YYYY') : '--';
    const end = this.dateFin() ? dayjs(this.dateFin()).format('DD/MM/YYYY') : '--';
    return `${start} - ${end}`;
  });
  public readonly dashboardScopeLabel = computed(() => {
    const boutique = this.boutiques().find(item => item.id === this.boutiqueId());
    const locataire = this.locataires().find(item => item.id === this.locataireId());

    if (boutique && locataire) {
      return `${boutique.nom} / ${locataire.nom}`;
    }
    if (boutique) {
      return boutique.nom ?? this.traduction('home.dashboard.fallbacks.unknownBoutique');
    }
    if (locataire) {
      return locataire.nom ?? this.traduction('home.dashboard.fallbacks.unknownTenant');
    }
    return this.traduction('home.dashboard.filters.globalScope');
  });
  public readonly quickActions = computed<QuickAction[]>(() =>
    [
      { labelKey: 'global.navbar.mesBoutiques', route: '/mes-boutiques', icon: 'store', visible: this.permissionsUi.estLocataire() },
      { labelKey: 'global.navbar.caisse', route: '/caisse', icon: 'cash-register', visible: this.permissionsUi.peutVoirEcran('caisse') },
      {
        labelKey: 'global.navbar.stocks',
        route: '/stock-operations',
        icon: 'database',
        visible: this.permissionsUi.peutVoirEcran('stock'),
      },
      { labelKey: 'global.navbar.reporting', route: '/reporting', icon: 'book', visible: this.permissionsUi.peutVoirEcran('reporting') },
      {
        labelKey: 'global.navbar.redevances',
        route: '/royalties',
        icon: 'database',
        visible: this.permissionsUi.peutVoirEcran('redevances'),
      },
    ].filter(action => action.visible),
  );

  public readonly salesTrend = computed<TrendPoint[]>(() =>
    this.salesByDay().map(point => ({
      label: point.day ? dayjs(point.day).format('DD/MM') : '--',
      value: point.netAmount ?? 0,
    })),
  );

  public readonly grossTrend = computed<TrendPoint[]>(() =>
    this.salesByDay().map(point => ({
      label: point.day ? dayjs(point.day).format('DD/MM') : '--',
      value: point.grossAmount ?? 0,
    })),
  );

  public readonly salesPolyline = computed(() =>
    this.buildPolyline(
      this.salesTrend().map(point => point.value),
      520,
      220,
    ),
  );
  public readonly ventesValidees = computed(() => this.ventesPeriode().filter(vente => vente.statut === 'VALIDEE'));
  public readonly tenantHasOperationalData = computed(
    () =>
      this.salesTrend().some(point => point.value > 0) ||
      this.tenantShopSummaries().some(summary => this.parseMontant(summary.netSales) > 0),
  );

  public readonly boutiquePerformance = computed<RankingRow[]>(() => {
    const grouped = new Map<string, { amount: number; locataire: string }>();
    for (const vente of this.ventesValidees()) {
      const key = vente.boutique?.nom ?? this.traduction('home.dashboard.fallbacks.unknownBoutique');
      const current = grouped.get(key) ?? {
        amount: 0,
        locataire: vente.locataire?.nom ?? this.traduction('home.dashboard.fallbacks.unknownTenant'),
      };
      current.amount += vente.montantNet ?? 0;
      if (!current.locataire || current.locataire === this.traduction('home.dashboard.fallbacks.unknownTenant')) {
        current.locataire = vente.locataire?.nom ?? current.locataire;
      }
      grouped.set(key, current);
    }

    return [...grouped.entries()]
      .map(([name, value]) => ({
        name,
        detail: value.locataire,
        value: this.formatMontant(value.amount),
      }))
      .sort((left, right) => this.parseMontant(right.value) - this.parseMontant(left.value))
      .slice(0, 5);
  });

  public readonly locatairePerformance = computed<RankingRow[]>(() => {
    const grouped = new Map<string, { amount: number; boutiques: Set<string> }>();
    for (const vente of this.ventesValidees()) {
      const key = vente.locataire?.nom ?? this.traduction('home.dashboard.fallbacks.unknownTenant');
      const current = grouped.get(key) ?? { amount: 0, boutiques: new Set<string>() };
      current.amount += vente.montantNet ?? 0;
      if (vente.boutique?.nom) {
        current.boutiques.add(vente.boutique.nom);
      }
      grouped.set(key, current);
    }

    return [...grouped.entries()]
      .map(([name, value]) => ({
        name,
        detail: this.traduction('home.dashboard.boutiqueCount', { count: value.boutiques.size }),
        value: this.formatMontant(value.amount),
      }))
      .sort((left, right) => this.parseMontant(right.value) - this.parseMontant(left.value))
      .slice(0, 5);
  });

  public readonly topProducts = computed<ProductRow[]>(() => {
    const ventes = new Map(this.ventesValidees().map(vente => [vente.id, vente]));
    const grouped = new Map<string, { product: string; boutique: string; sales: number; revenue: number }>();

    for (const ligne of this.lignesPeriode()) {
      const venteId = ligne.vente?.id;
      if (!venteId || !ventes.has(venteId)) {
        continue;
      }

      const vente = ventes.get(venteId)!;
      const designation = ligne.produit?.designation ?? this.traduction('home.dashboard.fallbacks.unknownProduct');
      const boutique = vente.boutique?.nom ?? this.traduction('home.dashboard.fallbacks.unknownBoutique');
      const key = `${designation}::${boutique}`;
      const current = grouped.get(key) ?? { product: designation, boutique, sales: 0, revenue: 0 };
      current.sales += ligne.quantite ?? 0;
      current.revenue += ligne.montantLigne ?? 0;
      grouped.set(key, current);
    }

    const sorted = [...grouped.values()].sort((left, right) => right.revenue - left.revenue).slice(0, 5);
    const maxRevenue = sorted[0]?.revenue ?? 0;

    return sorted.map((item, index) => ({
      rank: index + 1,
      product: item.product,
      boutique: item.boutique,
      sales: item.sales,
      revenue: this.formatMontant(item.revenue),
      trend: maxRevenue > 0 ? `+${Math.round((item.revenue / maxRevenue) * 100)}%` : '+0%',
    }));
  });

  private readonly router = inject(Router);
  private readonly accountService = inject(AccountService);
  private readonly boutiqueService = inject(BoutiqueService);
  private readonly locataireService = inject(LocataireService);
  private readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);
  private readonly venteService = inject(VenteService);
  private readonly ligneVenteService = inject(LigneVenteService);
  private readonly dashboardReportingService = inject(AdmDashboardReportingService);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    if (this.account()) {
      void this.initialiser();
      return;
    }

    this.accountService.identity().subscribe(() => {
      if (this.account()) {
        void this.initialiser();
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  async actualiserDashboard(): Promise<void> {
    if (!this.isAuthenticated()) {
      return;
    }

    this.chargement.set(true);
    this.messageKey.set(null);
    try {
      const stockAlertsRequest = this.permissionsUi.peutLireStock()
        ? this.queryOptionnelle(
            () =>
              firstValueFrom(
                this.dashboardReportingService.getStockAlerts({
                  boutiqueId: this.boutiqueId() ?? undefined,
                }),
              ),
            [],
          )
        : Promise.resolve<DashboardStockAlertResponse[]>([]);

      const shouldLoadRawSales =
        (this.permissionsUi.estAdmin() ||
          this.permissionsUi.estProfilAdm() ||
          this.permissionsUi.estProfilSupervision() ||
          this.dashboardMode() === 'VENDEUR') &&
        this.permissionsUi.peutLireVentes();

      const ventesRequest = shouldLoadRawSales
        ? this.queryOptionnelle(() => firstValueFrom(this.venteService.query(this.rawSalesQueryParams())), null)
        : Promise.resolve(null);

      const lignesRequest = shouldLoadRawSales
        ? this.queryOptionnelle(() => firstValueFrom(this.ligneVenteService.query({ size: 5000, sort: ['id,desc'] })), null)
        : Promise.resolve(null);

      const royaltyByGroupRequest =
        this.afficherRedevanceParGroupe() && this.dashboardMode() !== 'BOUTIQUE'
          ? this.queryOptionnelle(
              () =>
                firstValueFrom(
                  this.dashboardReportingService.getRoyaltyByGroupeArticle({
                    from: this.dateDebut(),
                    to: this.dateFin(),
                    boutiqueId: this.boutiqueId() ?? undefined,
                    locataireId: this.locataireId() ?? undefined,
                  }),
                ),
              [],
            )
          : Promise.resolve([]);

      const [overview, salesByDay, stockAlerts, ventesResponse, lignesResponse, royaltyByGroup] = await Promise.all([
        this.queryOptionnelle(
          () =>
            firstValueFrom(
              this.dashboardReportingService.getOverview({
                from: this.dateDebut(),
                to: this.dateFin(),
                boutiqueId: this.boutiqueId() ?? undefined,
                locataireId: this.locataireId() ?? undefined,
              }),
            ),
          EMPTY_OVERVIEW,
        ),
        this.queryOptionnelle(
          () =>
            firstValueFrom(
              this.dashboardReportingService.getSalesByDay({
                from: this.dateDebut(),
                to: this.dateFin(),
                boutiqueId: this.boutiqueId() ?? undefined,
                locataireId: this.locataireId() ?? undefined,
                statutVente: 'VALIDEE',
              }),
            ),
          [],
        ),
        stockAlertsRequest,
        ventesRequest,
        lignesRequest,
        royaltyByGroupRequest,
      ]);

      const ventesFiltrees = this.filtrerVentesPeriode(ventesResponse?.body ?? []);

      this.overview.set(overview);
      this.salesByDay.set(salesByDay);
      this.stockAlerts.set(stockAlerts);
      this.royaltyByGroupeArticle.set(royaltyByGroup);
      this.ventesPeriode.set(ventesFiltrees);
      this.lignesPeriode.set((lignesResponse?.body ?? []).filter(ligne => ventesFiltrees.some(vente => vente.id === ligne.vente?.id)));
      this.tenantShopSummaries.set(this.permissionsUi.estLocataire() ? await this.loadTenantShopSummaries() : []);
    } catch {
      this.messageKey.set('home.dashboard.messages.loadError');
    } finally {
      this.chargement.set(false);
    }
  }

  royaltiesBarHeight(value: number): number {
    const values = this.grossTrend().map(point => point.value);
    const max = Math.max(...values, 0);
    return max > 0 ? Math.round((value / max) * 100) : 0;
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString(this.localeCourante())} F CFA` : '0 F CFA';
  }

  private async initialiser(): Promise<void> {
    await this.permissionsUi.chargerPermissions(this.account());
    const mode = this.dashboardMode();

    if (mode === 'VENDEUR') {
      this.boutiques.set([]);
      this.locataires.set([]);
      this.exploitationsLocataire.set([]);
      const boutiqueIdsVendeur = this.permissionsUi.boutiqueIds();
      this.boutiqueId.set(boutiqueIdsVendeur.length > 0 ? boutiqueIdsVendeur[0] : null);
      await this.actualiserDashboard();
      return;
    }

    const [boutiquesResponse, locatairesResponse, exploitationsLocataire] = await Promise.all([
      mode === 'LOCATAIRE'
        ? Promise.resolve(null)
        : this.queryOptionnelle(
            () =>
              firstValueFrom(
                this.boutiqueService.query({
                  ...(mode === 'BOUTIQUE' ? this.paramsBoutiquesAccessibles() : {}),
                  size: 500,
                  sort: ['nom,asc'],
                }),
              ),
            null,
          ),
      mode === 'LOCATAIRE' || mode === 'BOUTIQUE'
        ? Promise.resolve(null)
        : this.queryOptionnelle(() => firstValueFrom(this.locataireService.query({ size: 500, sort: ['nom,asc'] })), null),
      mode === 'LOCATAIRE'
        ? this.queryOptionnelle(() => firstValueFrom(this.exploitationBoutiqueService.findMesExploitations()), [])
        : Promise.resolve(null),
    ]);

    const tenantExploitations = exploitationsLocataire ?? [];
    this.exploitationsLocataire.set(tenantExploitations);

    if (mode === 'LOCATAIRE') {
      this.boutiques.set(
        tenantExploitations
          .map(exploitation => exploitation.boutique)
          .filter((boutique): boutique is Pick<IBoutique, 'id' | 'nom' | 'code'> => !!boutique?.id)
          .map(boutique => boutique as IBoutique),
      );
    } else if (mode === 'BOUTIQUE') {
      const boutiqueIdsAutorisees = new Set(this.permissionsUi.boutiqueIds());
      const boutiquesAutorisees = (boutiquesResponse?.body ?? []).filter(boutique => boutiqueIdsAutorisees.has(boutique.id));
      this.boutiques.set(boutiquesAutorisees);
      if (!this.boutiqueId() && boutiquesAutorisees.length > 0) {
        this.boutiqueId.set(boutiquesAutorisees[0].id);
      }
    } else {
      this.boutiques.set(boutiquesResponse?.body ?? []);
    }

    this.locataires.set(locatairesResponse?.body ?? []);
    await this.actualiserDashboard();
  }

  private async queryOptionnelle<T>(requete: () => Promise<T>, valeurFallback: T): Promise<T> {
    try {
      return await requete();
    } catch {
      return valeurFallback;
    }
  }

  private paramsBoutiquesAccessibles(): Record<string, string | number> {
    const ids = this.permissionsUi.boutiqueIds();
    return ids.length ? { 'id.in': ids.join(',') } : { 'id.equals': -1 };
  }

  private rawSalesQueryParams(): Record<string, string | number | string[]> {
    const params: Record<string, string | number | string[]> = {
      size: this.dashboardMode() === 'VENDEUR' ? 100 : 2000,
      sort: ['dateHeure,desc'],
    };

    const currentUserId = this.account()?.id;
    if (this.dashboardMode() === 'VENDEUR' && typeof currentUserId === 'number') {
      params['vendeurId.equals'] = currentUserId;
    }

    return params;
  }

  private async loadTenantShopSummaries(): Promise<TenantShopSummary[]> {
    const selectedBoutiqueId = this.boutiqueId();
    const exploitations = this.exploitationsLocataire().filter(
      exploitation => exploitation.boutique?.id && (!selectedBoutiqueId || exploitation.boutique.id === selectedBoutiqueId),
    );

    const summaries = await Promise.all(
      exploitations.map(async exploitation => {
        const boutique = exploitation.boutique!;
        const boutiqueId = boutique.id;
        const overview = await this.queryOptionnelle(
          () =>
            firstValueFrom(
              this.dashboardReportingService.getOverview({
                from: this.dateDebut(),
                to: this.dateFin(),
                boutiqueId,
              }),
            ),
          EMPTY_OVERVIEW,
        );

        return {
          boutiqueId,
          name: boutique.nom ?? this.traduction('home.dashboard.fallbacks.unknownBoutique'),
          detail: this.tenantShopDetail(exploitation),
          netSales: this.formatMontant(overview.netSales),
          grossSales: this.formatMontant(overview.grossSales),
          validatedSales: this.formatNombre(overview.validatedSalesCount),
          pendingRoyalties: this.formatMontant(overview.royaltyOutstandingAmount),
        };
      }),
    );

    return summaries.sort((left, right) => this.parseMontant(right.netSales) - this.parseMontant(left.netSales));
  }

  private tenantShopDetail(exploitation: IExploitationBoutique): string {
    return [
      exploitation.numeroContrat
        ? this.traduction('home.dashboard.tenant.contract', { contract: exploitation.numeroContrat })
        : this.traduction('home.dashboard.tenant.noContract'),
      this.traduction(`home.dashboard.tenant.status.${exploitation.statut ?? 'UNKNOWN'}`),
      exploitation.dateDebut?.format('DD/MM/YYYY') ?? null,
    ]
      .filter((value): value is string => !!value)
      .join(' / ');
  }

  private filtrerVentesPeriode(ventes: IVente[]): IVente[] {
    const from = this.dateDebut() ? dayjs(`${this.dateDebut()}T00:00:00`) : null;
    const to = this.dateFin() ? dayjs(`${this.dateFin()}T23:59:59`) : null;

    return ventes.filter(vente => {
      if (!vente.dateHeure) {
        return false;
      }
      if (this.boutiqueId() && vente.boutique?.id !== this.boutiqueId()) {
        return false;
      }
      if (this.locataireId() && vente.locataire?.id !== this.locataireId()) {
        return false;
      }
      if (from && vente.dateHeure.isBefore(from)) {
        return false;
      }
      if (to && vente.dateHeure.isAfter(to)) {
        return false;
      }
      return true;
    });
  }

  private formatNombre(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? valeur.toLocaleString(this.localeCourante()) : '0';
  }

  private localeCourante(): string {
    return this.translateService.getCurrentLang() === 'en' ? 'en-US' : 'fr-FR';
  }

  private traduction(key: string, params?: Record<string, unknown>): string {
    return this.translateService.instant(key, params) as string;
  }

  private parseMontant(valeur: string): number {
    return (
      Number(
        valeur
          .replace(/[^0-9,-]/g, '')
          .replace(/\s/g, '')
          .replace(',', '.'),
      ) || 0
    );
  }

  private buildPolyline(values: number[], width: number, height: number): string {
    if (values.length === 0) {
      return '';
    }

    const min = Math.min(...values);
    const max = Math.max(...values);
    const innerHeight = height - 24;

    return values
      .map((value, index) => {
        const x = values.length === 1 ? width / 2 : (index / (values.length - 1)) * width;
        const normalized = max === min ? 0.5 : (value - min) / (max - min);
        const y = height - normalized * innerHeight - 12;
        return `${x},${y}`;
      })
      .join(' ');
  }
}
