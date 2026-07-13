import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { Subscription, combineLatest, filter, map, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { IExploitationBoutique } from 'app/entities/exploitation-boutique/exploitation-boutique.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { IBoutique } from '../boutique.model';
import { BoutiqueAssignDialog } from '../assign/boutique-assign-dialog';
import { BoutiqueDeleteDialog } from '../delete/boutique-delete-dialog';
import { BoutiqueService } from '../service/boutique.service';

@Component({
  selector: 'jhi-boutique',
  templateUrl: './boutique.html',
  styleUrl: './boutique.scss',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    NgbPagination,
    ItemCount,
  ],
})
export class Boutique implements OnInit {
  subscription: Subscription | null = null;
  readonly boutiques = signal<IBoutique[]>([]);
  readonly searchTerm = signal('');
  readonly selectedType = signal<'ALL' | 'DUTY_FREE' | 'RESTAURATION' | 'COMMERCE' | 'SERVICE' | 'AUTRE'>('ALL');
  readonly selectedStatus = signal<'ALL' | 'ACTIF' | 'INACTIF' | 'SUSPENDU'>('ALL');
  readonly filteredBoutiques = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedType = this.selectedType();
    const selectedStatus = this.selectedStatus();

    const exploitationsActives = this.activeExploitationByBoutiqueId();

    return this.boutiques()
      .filter(boutique => {
        const matchesSearch =
          !searchTerm ||
          [boutique.code, boutique.nom, boutique.emplacement, boutique.telephone]
            .filter(Boolean)
            .some(value => value!.toLowerCase().includes(searchTerm));
        const matchesType = selectedType === 'ALL' || boutique.type === selectedType;
        const matchesStatus = selectedStatus === 'ALL' || boutique.statut === selectedStatus;

        return matchesSearch && matchesType && matchesStatus;
      })
      .sort((a, b) => Number(exploitationsActives.has(b.id)) - Number(exploitationsActives.has(a.id)));
  });
  readonly activeExploitations = signal<IExploitationBoutique[]>([]);
  readonly activeExploitationByBoutiqueId = computed(() => {
    const exploitationsByBoutiqueId = new Map<number, IExploitationBoutique>();
    for (const exploitation of this.activeExploitations()) {
      if (exploitation.boutique?.id != null) {
        exploitationsByBoutiqueId.set(exploitation.boutique.id, exploitation);
      }
    }
    return exploitationsByBoutiqueId;
  });
  readonly assignedCount = computed(
    () => this.boutiques().filter(boutique => this.activeExploitationByBoutiqueId().has(boutique.id)).length,
  );
  readonly unassignedCount = computed(() => Math.max(0, this.boutiques().length - this.assignedCount()));
  readonly inactiveCount = computed(() => this.boutiques().filter(boutique => boutique.statut !== 'ACTIF').length);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly router = inject(Router);
  protected readonly boutiqueService = inject(BoutiqueService);
  readonly isLoading = this.boutiqueService.boutiquesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly filterOptions = toSignal(this.filters.filterChanges);
  readonly permissionsUi = inject(UiPermissionService);
  protected modalService = inject(NgbModal);
  protected readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);

  constructor() {
    effect(() => {
      const headers = this.boutiqueService.boutiquesResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.boutiques.set(this.fillComponentAttributesFromResponseBody([...this.boutiqueService.boutiques()]));
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

  trackId = (item: IBoutique): number => this.boutiqueService.getBoutiqueIdentifier(item);

  formatType(type: IBoutique['type']): string {
    return (
      {
        DUTY_FREE: 'Duty Free',
        RESTAURATION: 'Restauration',
        COMMERCE: 'Commerce',
        SERVICE: 'Service',
        AUTRE: 'Autre',
      }[type ?? 'AUTRE'] ?? 'Autre'
    );
  }

  formatStatus(statut: IBoutique['statut']): string {
    return (
      {
        ACTIF: 'Active',
        INACTIF: 'Inactive',
        SUSPENDU: 'Suspendue',
      }[statut ?? 'INACTIF'] ?? 'Inactive'
    );
  }

  formatValue(value: string | null | undefined): string {
    return value?.trim() ? value : '--';
  }

  clearFilters(): void {
    this.searchTerm.set('');
    this.selectedType.set('ALL');
    this.selectedStatus.set('ALL');
  }

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
        tap(() => this.loadActiveExploitations()),
      )
      .subscribe();
  }

  loadActiveExploitations(): void {
    this.exploitationBoutiqueService
      .query({ 'statut.equals': 'ACTIF', size: 500, eagerload: true })
      .pipe(map(res => res.body ?? []))
      .subscribe(exploitations => this.activeExploitations.set(exploitations));
  }

  delete(boutique: IBoutique): void {
    const modalRef = this.modalService.open(BoutiqueDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.boutique = boutique;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  assign(boutique: IBoutique): void {
    const modalRef = this.modalService.open(BoutiqueAssignDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.boutique = boutique;
    modalRef.componentInstance.activeExploitation = this.activeExploitationByBoutiqueId().get(boutique.id) ?? null;
    modalRef.closed
      .pipe(
        filter(reason => reason === 'assigned'),
        tap(() => {
          this.load();
          this.boutiqueService.boutiquesResource.reload();
          this.loadActiveExploitations();
        }),
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

  protected fillComponentAttributesFromResponseBody(data: IBoutique[]): IBoutique[] {
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
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    this.boutiqueService.boutiquesParams.set(queryObject);
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
