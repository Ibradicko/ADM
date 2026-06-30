import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { DecimalPipe, SlicePipe } from '@angular/common';
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
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { RegularisationRedevanceDeleteDialog } from '../delete/regularisation-redevance-delete-dialog';
import { IRegularisationRedevance } from '../regularisation-redevance.model';
import { RegularisationRedevanceService } from '../service/regularisation-redevance.service';

@Component({
  selector: 'jhi-regularisation-redevance',
  templateUrl: './regularisation-redevance.html',
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
    DecimalPipe,
    SlicePipe,
  ],
})
export class RegularisationRedevance implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  subscription: Subscription | null = null;
  readonly regularisationRedevances = signal<IRegularisationRedevance[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly regularisationRedevanceService = inject(RegularisationRedevanceService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.regularisationRedevanceService.regularisationRedevancesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.regularisationRedevances.set(
        this.fillComponentAttributesFromResponseBody([...this.regularisationRedevanceService.regularisationRedevances()]),
      );
    });
  }

  trackId = (item: IRegularisationRedevance): number => this.regularisationRedevanceService.getRegularisationRedevanceIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(regularisationRedevance: IRegularisationRedevance): void {
    if (!this.permissionsUi.peutGererRedevances()) {
      return;
    }
    const modalRef = this.modalService.open(RegularisationRedevanceDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.regularisationRedevance = regularisationRedevance;
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

  protected refineData(data: IRegularisationRedevance[]): IRegularisationRedevance[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IRegularisationRedevance[]): IRegularisationRedevance[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.regularisationRedevanceService.regularisationRedevancesParams.set(queryObject);
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
