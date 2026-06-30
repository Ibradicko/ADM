import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { PaiementRedevanceDeleteDialog } from '../delete/paiement-redevance-delete-dialog';
import { IPaiementRedevance } from '../paiement-redevance.model';
import { PaiementRedevanceService } from '../service/paiement-redevance.service';

@Component({
  selector: 'jhi-paiement-redevance',
  templateUrl: './paiement-redevance.html',
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
    FormatMediumDatePipe,
  ],
})
export class PaiementRedevance implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  subscription: Subscription | null = null;
  readonly paiementRedevances = signal<IPaiementRedevance[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly paiementRedevanceService = inject(PaiementRedevanceService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.paiementRedevanceService.paiementRedevancesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.paiementRedevances.set(this.fillComponentAttributesFromResponseBody([...this.paiementRedevanceService.paiementRedevances()]));
    });
  }

  trackId = (item: IPaiementRedevance): number => this.paiementRedevanceService.getPaiementRedevanceIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(paiementRedevance: IPaiementRedevance): void {
    if (!this.permissionsUi.peutGererRedevances()) {
      return;
    }
    const modalRef = this.modalService.open(PaiementRedevanceDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.paiementRedevance = paiementRedevance;
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

  protected refineData(data: IPaiementRedevance[]): IPaiementRedevance[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPaiementRedevance[]): IPaiementRedevance[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.paiementRedevanceService.paiementRedevancesParams.set(queryObject);
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
