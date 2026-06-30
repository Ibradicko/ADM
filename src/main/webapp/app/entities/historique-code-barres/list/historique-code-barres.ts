import dayjs from 'dayjs/esm';

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
import { HistoriqueCodeBarresDeleteDialog } from '../delete/historique-code-barres-delete-dialog';
import { IHistoriqueCodeBarres } from '../historique-code-barres.model';
import { HistoriqueCodeBarresService } from '../service/historique-code-barres.service';

@Component({
  selector: 'jhi-historique-code-barres',
  templateUrl: './historique-code-barres.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatetimePipe],
})
export class HistoriqueCodeBarres implements OnInit {
  subscription: Subscription | null = null;
  readonly historiqueCodeBarreses = signal<IHistoriqueCodeBarres[]>([]);
  readonly searchTerm = signal('');
  readonly selectedUtilisateur = signal('ALL');
  readonly selectedProduit = signal('ALL');
  readonly historiqueFiltre = computed(() => {
    const searchTerm = this.searchTerm().trim().toLowerCase();
    const selectedUtilisateur = this.selectedUtilisateur();
    const selectedProduit = this.selectedProduit();

    return this.historiqueCodeBarreses().filter(historique => {
      const matchesSearch =
        !searchTerm ||
        [historique.ancienCode, historique.nouveauCode, historique.motif, historique.produit?.designation, historique.utilisateur?.login]
          .filter(Boolean)
          .some(value => value!.toLowerCase().includes(searchTerm));
      const matchesUtilisateur = selectedUtilisateur === 'ALL' || historique.utilisateur?.login === selectedUtilisateur;
      const matchesProduit = selectedProduit === 'ALL' || String(historique.produit?.id) === selectedProduit;

      return matchesSearch && matchesUtilisateur && matchesProduit;
    });
  });
  readonly totalAujourdhui = computed(
    () => this.historiqueCodeBarreses().filter(historique => historique.dateChangement?.isSame(dayjs(), 'day')).length,
  );
  readonly totalAvecAncienCode = computed(() => this.historiqueCodeBarreses().filter(historique => !!historique.ancienCode?.trim()).length);
  readonly utilisateursDisponibles = computed(() =>
    [
      ...new Set(
        this.historiqueCodeBarreses()
          .map(historique => historique.utilisateur?.login)
          .filter((login): login is string => !!login?.trim()),
      ),
    ].sort(),
  );
  readonly produitsDisponibles = computed(() => [
    ...new Map(
      this.historiqueCodeBarreses()
        .filter(historique => historique.produit?.id)
        .map(historique => [historique.produit!.id, historique.produit!]),
    ).values(),
  ]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly historiqueCodeBarresService = inject(HistoriqueCodeBarresService);
  readonly isLoading = this.historiqueCodeBarresService.historiqueCodeBarresesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.historiqueCodeBarreses.set(
        this.fillComponentAttributesFromResponseBody([...this.historiqueCodeBarresService.historiqueCodeBarreses()]),
      );
    });
  }

  trackId = (item: IHistoriqueCodeBarres): number => this.historiqueCodeBarresService.getHistoriqueCodeBarresIdentifier(item);

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
    this.selectedUtilisateur.set('ALL');
    this.selectedProduit.set('ALL');
  }

  delete(historiqueCodeBarres: IHistoriqueCodeBarres): void {
    const modalRef = this.modalService.open(HistoriqueCodeBarresDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.historiqueCodeBarres = historiqueCodeBarres;
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

  protected refineData(data: IHistoriqueCodeBarres[]): IHistoriqueCodeBarres[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IHistoriqueCodeBarres[]): IHistoriqueCodeBarres[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    this.historiqueCodeBarresService.historiqueCodeBarresesParams.set({
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
