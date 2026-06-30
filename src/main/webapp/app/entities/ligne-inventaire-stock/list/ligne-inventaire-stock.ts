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
import { LigneInventaireStockDeleteDialog } from '../delete/ligne-inventaire-stock-delete-dialog';
import { ILigneInventaireStock } from '../ligne-inventaire-stock.model';
import { LigneInventaireStockService } from '../service/ligne-inventaire-stock.service';

@Component({
  selector: 'jhi-ligne-inventaire-stock',
  templateUrl: './ligne-inventaire-stock.html',
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
export class LigneInventaireStock implements OnInit {
  subscription: Subscription | null = null;
  readonly ligneInventaireStocks = signal<ILigneInventaireStock[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly ligneInventaireStockService = inject(LigneInventaireStockService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.ligneInventaireStockService.ligneInventaireStocksResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.ligneInventaireStocks.set(
        this.fillComponentAttributesFromResponseBody([...this.ligneInventaireStockService.ligneInventaireStocks()]),
      );
    });
  }

  trackId = (item: ILigneInventaireStock): number => this.ligneInventaireStockService.getLigneInventaireStockIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(ligneInventaireStock: ILigneInventaireStock): void {
    const modalRef = this.modalService.open(LigneInventaireStockDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ligneInventaireStock = ligneInventaireStock;
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

  protected refineData(data: ILigneInventaireStock[]): ILigneInventaireStock[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ILigneInventaireStock[]): ILigneInventaireStock[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.ligneInventaireStockService.ligneInventaireStocksParams.set(queryObject);
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
