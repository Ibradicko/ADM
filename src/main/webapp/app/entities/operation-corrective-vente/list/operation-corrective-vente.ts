import { DecimalPipe, SlicePipe } from '@angular/common';
import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { OperationCorrectiveVenteDeleteDialog } from '../delete/operation-corrective-vente-delete-dialog';
import { IOperationCorrectiveVente } from '../operation-corrective-vente.model';
import { OperationCorrectiveVenteService } from '../service/operation-corrective-vente.service';

@Component({
  selector: 'jhi-operation-corrective-vente',
  templateUrl: './operation-corrective-vente.html',
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
    DecimalPipe,
    SlicePipe,
    FormatMediumDatetimePipe,
  ],
})
export class OperationCorrectiveVente implements OnInit {
  subscription: Subscription | null = null;
  readonly operationCorrectiveVentes = signal<IOperationCorrectiveVente[]>([]);

  sortState = sortStateSignal({});

  readonly permissionsUi = inject(UiPermissionService);

  readonly router = inject(Router);
  protected readonly operationCorrectiveVenteService = inject(OperationCorrectiveVenteService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.operationCorrectiveVenteService.operationCorrectiveVentesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.operationCorrectiveVentes.set(
        this.fillComponentAttributesFromResponseBody([...this.operationCorrectiveVenteService.operationCorrectiveVentes()]),
      );
    });
  }

  trackId = (item: IOperationCorrectiveVente): number => this.operationCorrectiveVenteService.getOperationCorrectiveVenteIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(operationCorrectiveVente: IOperationCorrectiveVente): void {
    const modalRef = this.modalService.open(OperationCorrectiveVenteDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.operationCorrectiveVente = operationCorrectiveVente;
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

  protected refineData(data: IOperationCorrectiveVente[]): IOperationCorrectiveVente[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IOperationCorrectiveVente[]): IOperationCorrectiveVente[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.operationCorrectiveVenteService.operationCorrectiveVentesParams.set(queryObject);
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
