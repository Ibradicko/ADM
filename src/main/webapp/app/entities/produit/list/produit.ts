import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ProduitDeleteDialog } from '../delete/produit-delete-dialog';
import { ProduitCodeBarresModal } from '../modals/produit-code-barres-modal';
import { ProduitEtiquettesModal } from '../modals/produit-etiquettes-modal';
import { ProduitTarifModal } from '../modals/produit-tarif-modal';
import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

@Component({
  selector: 'jhi-produit',
  templateUrl: './produit.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    NgbPagination,
    ItemCount,
    TranslateDirective,
    TranslateModule,
  ],
})
export class Produit implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  subscription: Subscription | null = null;
  readonly produits = signal<IProduit[]>([]);
  readonly searchTerm = signal('');
  readonly selectedTypePrix = signal<'ALL' | 'STANDARD' | 'PROMOTION' | 'CONTRACTUEL'>('ALL');
  readonly selectedStatus = signal<'ALL' | 'ACTIF' | 'INACTIF' | 'SUSPENDU'>('ALL');
  readonly selectedBoutique = signal('ALL');
  readonly filteredProduits = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedTypePrix = this.selectedTypePrix();
    const selectedStatus = this.selectedStatus();
    const selectedBoutique = this.selectedBoutique();

    return this.produits().filter(produit => {
      const matchesSearch =
        !searchTerm ||
        [produit.codeInterne, produit.designation, produit.description, produit.groupeArticle?.libelle, produit.familleArticle?.libelle]
          .filter(Boolean)
          .some(value => value!.toLowerCase().includes(searchTerm));
      const matchesTypePrix = selectedTypePrix === 'ALL' || produit.typePrix === selectedTypePrix;
      const matchesStatus = selectedStatus === 'ALL' || produit.statut === selectedStatus;
      const matchesBoutique = selectedBoutique === 'ALL' || String(produit.boutique?.id) === selectedBoutique;

      return matchesSearch && matchesTypePrix && matchesStatus && matchesBoutique;
    });
  });
  readonly totalActifs = computed(() => this.produits().filter(produit => produit.statut === 'ACTIF').length);
  readonly totalPromotions = computed(() => this.produits().filter(produit => produit.typePrix === 'PROMOTION').length);
  readonly totalContractuels = computed(() => this.produits().filter(produit => produit.typePrix === 'CONTRACTUEL').length);
  readonly boutiquesDisponibles = computed(() => [
    ...new Map(
      this.produits()
        .filter(produit => produit.boutique?.id)
        .map(produit => [produit.boutique!.id, produit.boutique!]),
    ).values(),
  ]);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly router = inject(Router);
  protected readonly produitService = inject(ProduitService);
  readonly isLoading = this.produitService.produitsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly filterOptions = toSignal(this.filters.filterChanges);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      const headers = this.produitService.produitsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.produits.set(this.fillComponentAttributesFromResponseBody([...this.produitService.produits()]));
    });

    effect(() => {
      const filterOptions = this.filterOptions();
      if (filterOptions) {
        untracked(() => {
          this.handleNavigation(1, this.sortState(), filterOptions);
        });
      }
    });
  }

  trackId = (item: IProduit): number => this.produitService.getProduitIdentifier(item);

  formatTypePrix(typePrix: IProduit['typePrix']): string {
    return (
      {
        STANDARD: 'Standard',
        PROMOTION: 'Promotion',
        CONTRACTUEL: 'Contractuel',
      }[typePrix ?? 'STANDARD'] ?? 'Standard'
    );
  }

  formatStatus(statut: IProduit['statut']): string {
    return (
      {
        ACTIF: 'Actif',
        INACTIF: 'Inactif',
        SUSPENDU: 'Suspendu',
      }[statut ?? 'INACTIF'] ?? 'Inactif'
    );
  }

  formatValeur(valeur: string | null | undefined): string {
    return valeur?.trim() ? valeur : '--';
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
  }

  formatTaux(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur}%` : '--';
  }

  reinitialiserFiltres(): void {
    this.searchTerm.set('');
    this.selectedTypePrix.set('ALL');
    this.selectedStatus.set('ALL');
    this.selectedBoutique.set('ALL');
  }

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  async ouvrirModalCodeBarres(produit: IProduit): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    const modalRef = this.modalService.open(ProduitCodeBarresModal, { size: 'xl', backdrop: 'static' });
    modalRef.componentInstance.produit = produit;
    await modalRef.componentInstance.chargerHistorique();
    modalRef.closed
      .pipe(
        filter(reason => reason === 'saved'),
        tap(() => this.load()),
      )
      .subscribe();
  }

  async ouvrirModalTarif(produit: IProduit): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    const modalRef = this.modalService.open(ProduitTarifModal, { size: 'xl', backdrop: 'static' });
    modalRef.componentInstance.produit = produit;
    await modalRef.componentInstance.chargerHistorique();
    modalRef.closed
      .pipe(
        filter(reason => reason === 'saved'),
        tap(() => this.load()),
      )
      .subscribe();
  }

  async ouvrirModalEtiquettes(produit: IProduit): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    const modalRef = this.modalService.open(ProduitEtiquettesModal, { size: 'xl', backdrop: 'static' });
    modalRef.componentInstance.produit = produit;
    await modalRef.componentInstance.chargerHistorique();
    modalRef.closed
      .pipe(
        filter(reason => reason === 'saved'),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(produit: IProduit): void {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    const modalRef = this.modalService.open(ProduitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.produit = produit;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected fillComponentAttributesFromResponseBody(data: IProduit[]): IProduit[] {
    return data;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(): void {
    const pageToLoad: number = this.page();
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    this.produitService.produitsParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    if (filterOptions) {
      for (const filterOption of filterOptions) {
        queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
      }
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
