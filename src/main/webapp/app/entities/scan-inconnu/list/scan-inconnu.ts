import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ScanInconnuDeleteDialog } from '../delete/scan-inconnu-delete-dialog';
import { IScanInconnu } from '../scan-inconnu.model';
import { ScanInconnuService } from '../service/scan-inconnu.service';

@Component({
  selector: 'jhi-scan-inconnu',
  templateUrl: './scan-inconnu.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class ScanInconnu implements OnInit {
  subscription: Subscription | null = null;
  readonly scanInconnus = signal<IScanInconnu[]>([]);
  readonly searchTerm = signal('');
  readonly selectedResolution = signal<'ALL' | 'OPEN' | 'DONE'>('ALL');
  readonly selectedBoutique = signal('ALL');
  readonly selectedOrigin = signal('ALL');
  readonly scansFiltres = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedResolution = this.selectedResolution();
    const selectedBoutique = this.selectedBoutique();
    const selectedOrigin = this.selectedOrigin();

    return this.scanInconnus().filter(scan => {
      const matchesSearch =
        !searchTerm ||
        [scan.codeScanne, scan.ecranOrigine, scan.commentaire, scan.boutique?.nom, scan.produitAffecte?.designation]
          .filter(Boolean)
          .some(value => value!.toLowerCase().includes(searchTerm));
      const matchesResolution =
        selectedResolution === 'ALL' || (selectedResolution === 'DONE' && scan.resolu) || (selectedResolution === 'OPEN' && !scan.resolu);
      const matchesBoutique = selectedBoutique === 'ALL' || String(scan.boutique?.id) === selectedBoutique;
      const matchesOrigin = selectedOrigin === 'ALL' || scan.ecranOrigine === selectedOrigin;

      return matchesSearch && matchesResolution && matchesBoutique && matchesOrigin;
    });
  });
  readonly totalResolus = computed(() => this.scanInconnus().filter(scan => scan.resolu).length);
  readonly totalOuverts = computed(() => this.scanInconnus().filter(scan => !scan.resolu).length);
  readonly boutiquesDisponibles = computed(() => [
    ...new Map(
      this.scanInconnus()
        .filter(scan => scan.boutique?.id)
        .map(scan => [scan.boutique!.id, scan.boutique!]),
    ).values(),
  ]);
  readonly originesDisponibles = computed(() =>
    [
      ...new Set(
        this.scanInconnus()
          .map(scan => scan.ecranOrigine)
          .filter((origine): origine is string => !!origine?.trim()),
      ),
    ].sort(),
  );

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly scanInconnuService = inject(ScanInconnuService);
  readonly isLoading = this.scanInconnuService.scanInconnusResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.scanInconnus.set(this.fillComponentAttributesFromResponseBody([...this.scanInconnuService.scanInconnus()]));
    });
  }

  trackId = (item: IScanInconnu): number => this.scanInconnuService.getScanInconnuIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  formatValeur(valeur: string | null | undefined): string {
    return valeur?.trim() ? valeur : '--';
  }

  reinitialiserFiltres(): void {
    this.searchTerm.set('');
    this.selectedResolution.set('ALL');
    this.selectedBoutique.set('ALL');
    this.selectedOrigin.set('ALL');
  }

  delete(scanInconnu: IScanInconnu): void {
    const modalRef = this.modalService.open(ScanInconnuDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.scanInconnu = scanInconnu;
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
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IScanInconnu[]): IScanInconnu[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IScanInconnu[]): IScanInconnu[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    this.scanInconnuService.scanInconnusParams.set({
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    });
  }

  protected handleNavigation(sortState: SortState): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: {
        sort: this.sortService.buildSortParam(sortState),
      },
    });
  }
}
