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
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { LocataireDeleteDialog } from '../delete/locataire-delete-dialog';
import { ILocataire } from '../locataire.model';
import { LocataireService } from '../service/locataire.service';

@Component({
  selector: 'jhi-locataire',
  templateUrl: './locataire.html',
  styleUrl: './locataire.scss',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    TranslateModule,
    TranslateDirective,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    NgbPagination,
    ItemCount,
  ],
})
export class Locataire implements OnInit {
  subscription: Subscription | null = null;
  readonly locataires = signal<ILocataire[]>([]);
  readonly searchTerm = signal('');
  readonly selectedType = signal<'ALL' | 'PERSONNE_PHYSIQUE' | 'PERSONNE_MORALE'>('ALL');
  readonly selectedStatus = signal<'ALL' | 'ACTIF' | 'INACTIF' | 'SUSPENDU'>('ALL');
  readonly filteredLocataires = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedType = this.selectedType();
    const selectedStatus = this.selectedStatus();

    return this.locataires().filter(locataire => {
      const matchesSearch =
        !searchTerm ||
        [locataire.code, locataire.nom, locataire.numeroIdentification, locataire.email, locataire.telephone, locataire.adresse]
          .filter(Boolean)
          .some(value => value!.toLowerCase().includes(searchTerm));
      const matchesType = selectedType === 'ALL' || locataire.typeLocataire === selectedType;
      const matchesStatus = selectedStatus === 'ALL' || locataire.statut === selectedStatus;

      return matchesSearch && matchesType && matchesStatus;
    });
  });
  readonly activeCount = computed(() => this.locataires().filter(locataire => locataire.statut === 'ACTIF').length);
  readonly physicalCount = computed(() => this.locataires().filter(locataire => locataire.typeLocataire === 'PERSONNE_PHYSIQUE').length);
  readonly corporateCount = computed(() => this.locataires().filter(locataire => locataire.typeLocataire === 'PERSONNE_MORALE').length);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);

  readonly permissionsUi = inject(UiPermissionService);

  readonly router = inject(Router);
  protected readonly locataireService = inject(LocataireService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.locataireService.locatairesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly filterOptions = toSignal(this.filters.filterChanges);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      const headers = this.locataireService.locatairesResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      this.locataires.set(this.fillComponentAttributesFromResponseBody([...this.locataireService.locataires()]));
    });

    effect(() => {
      const filterOptions = this.filterOptions();
      if (filterOptions) {
        untracked(() => {
          // Only watch for filter changes. Other signals should be ignored.
          this.handleNavigation(1, this.sortState(), filterOptions);
        });
      }
    });
  }

  trackId = (item: ILocataire): number => this.locataireService.getLocataireIdentifier(item);

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
      )
      .subscribe();
  }

  delete(locataire: ILocataire): void {
    const modalRef = this.modalService.open(LocataireDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.locataire = locataire;
    // unsubscribe not needed because closed completes on modal close
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

  protected fillComponentAttributesFromResponseBody(data: ILocataire[]): ILocataire[] {
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
    this.locataireService.locatairesParams.set(queryObject);
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
