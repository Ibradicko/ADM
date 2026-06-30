import { Component, OnInit, computed, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ParametreCodeBarresDeleteDialog } from '../delete/parametre-code-barres-delete-dialog';
import { IParametreCodeBarres } from '../parametre-code-barres.model';
import { ParametreCodeBarresService } from '../service/parametre-code-barres.service';

@Component({
  selector: 'jhi-parametre-code-barres',
  templateUrl: './parametre-code-barres.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class ParametreCodeBarres implements OnInit {
  subscription: Subscription | null = null;
  readonly parametreCodeBarreses = signal<IParametreCodeBarres[]>([]);
  readonly searchTerm = signal('');
  readonly selectedEtat = signal<'ALL' | 'ACTIF' | 'INACTIF'>('ALL');
  readonly selectedFormat = signal('ALL');
  readonly parametresFiltres = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedEtat = this.selectedEtat();
    const selectedFormat = this.selectedFormat();

    return this.parametreCodeBarreses().filter(parametre => {
      const matchesSearch =
        !searchTerm ||
        [parametre.prefixe, parametre.formatParDefaut, parametre.longueur?.toString()]
          .filter(Boolean)
          .some(value => value!.toLowerCase().includes(searchTerm));
      const matchesEtat =
        selectedEtat === 'ALL' || (selectedEtat === 'ACTIF' && parametre.actif) || (selectedEtat === 'INACTIF' && !parametre.actif);
      const matchesFormat = selectedFormat === 'ALL' || parametre.formatParDefaut === selectedFormat;

      return matchesSearch && matchesEtat && matchesFormat;
    });
  });
  readonly totalActifs = computed(() => this.parametreCodeBarreses().filter(parametre => parametre.actif).length);
  readonly longueurMoyenne = computed(() => {
    const longueurs = this.parametreCodeBarreses()
      .map(parametre => parametre.longueur)
      .filter((longueur): longueur is number => typeof longueur === 'number');
    if (longueurs.length === 0) {
      return '--';
    }
    return `${Math.round(longueurs.reduce((total, longueur) => total + longueur, 0) / longueurs.length)}`;
  });
  readonly formatsDisponibles = computed(() =>
    [
      ...new Set(
        this.parametreCodeBarreses()
          .map(parametre => parametre.formatParDefaut)
          .filter((format): format is NonNullable<IParametreCodeBarres['formatParDefaut']> => !!format),
      ),
    ].sort(),
  );

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly parametreCodeBarresService = inject(ParametreCodeBarresService);
  readonly isLoading = this.parametreCodeBarresService.parametreCodeBarresesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.parametreCodeBarreses.set(
        this.fillComponentAttributesFromResponseBody([...this.parametreCodeBarresService.parametreCodeBarreses()]),
      );
    });
  }

  trackId = (item: IParametreCodeBarres): number => this.parametreCodeBarresService.getParametreCodeBarresIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  reinitialiserFiltres(): void {
    this.searchTerm.set('');
    this.selectedEtat.set('ALL');
    this.selectedFormat.set('ALL');
  }

  delete(parametreCodeBarres: IParametreCodeBarres): void {
    const modalRef = this.modalService.open(ParametreCodeBarresDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.parametreCodeBarres = parametreCodeBarres;
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

  protected refineData(data: IParametreCodeBarres[]): IParametreCodeBarres[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IParametreCodeBarres[]): IParametreCodeBarres[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    this.parametreCodeBarresService.parametreCodeBarresesParams.set({
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
