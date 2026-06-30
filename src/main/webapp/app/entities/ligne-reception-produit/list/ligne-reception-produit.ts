import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { LigneReceptionProduitDeleteDialog } from '../delete/ligne-reception-produit-delete-dialog';
import { ILigneReceptionProduit } from '../ligne-reception-produit.model';
import { LigneReceptionProduitService } from '../service/ligne-reception-produit.service';

@Component({
  selector: 'jhi-ligne-reception-produit',
  templateUrl: './ligne-reception-produit.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslateModule,
  ],
})
export class LigneReceptionProduit implements OnInit {
  subscription: Subscription | null = null;
  readonly ligneReceptionProduits = signal<ILigneReceptionProduit[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly ligneReceptionProduitService = inject(LigneReceptionProduitService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.ligneReceptionProduitService.ligneReceptionProduitsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.ligneReceptionProduits.set(
        this.fillComponentAttributesFromResponseBody([...this.ligneReceptionProduitService.ligneReceptionProduits()]),
      );
    });
  }

  trackId = (item: ILigneReceptionProduit): number => this.ligneReceptionProduitService.getLigneReceptionProduitIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(ligneReceptionProduit: ILigneReceptionProduit): void {
    const modalRef = this.modalService.open(LigneReceptionProduitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ligneReceptionProduit = ligneReceptionProduit;
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
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: ILigneReceptionProduit[]): ILigneReceptionProduit[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ILigneReceptionProduit[]): ILigneReceptionProduit[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.ligneReceptionProduitService.ligneReceptionProduitsParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
