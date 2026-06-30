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
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { LotEtiquettesDeleteDialog } from '../delete/lot-etiquettes-delete-dialog';
import { ILotEtiquettes } from '../lot-etiquettes.model';
import { LotEtiquettesService } from '../service/lot-etiquettes.service';

@Component({
  selector: 'jhi-lot-etiquettes',
  templateUrl: './lot-etiquettes.html',
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
    FormatMediumDatetimePipe,
  ],
})
export class LotEtiquettes implements OnInit {
  subscription: Subscription | null = null;
  readonly lotEtiquetteses = signal<ILotEtiquettes[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly lotEtiquettesService = inject(LotEtiquettesService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.lotEtiquettesService.lotEtiquettesesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.lotEtiquetteses.set(this.fillComponentAttributesFromResponseBody([...this.lotEtiquettesService.lotEtiquetteses()]));
    });
  }

  trackId = (item: ILotEtiquettes): number => this.lotEtiquettesService.getLotEtiquettesIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(lotEtiquettes: ILotEtiquettes): void {
    const modalRef = this.modalService.open(LotEtiquettesDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.lotEtiquettes = lotEtiquettes;
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

  protected refineData(data: ILotEtiquettes[]): ILotEtiquettes[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ILotEtiquettes[]): ILotEtiquettes[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.lotEtiquettesService.lotEtiquettesesParams.set(queryObject);
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
