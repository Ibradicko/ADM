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
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ModePaiementRefDeleteDialog } from '../delete/mode-paiement-ref-delete-dialog';
import { IModePaiementRef } from '../mode-paiement-ref.model';
import { ModePaiementRefService } from '../service/mode-paiement-ref.service';

@Component({
  selector: 'jhi-mode-paiement-ref',
  templateUrl: './mode-paiement-ref.html',
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
export class ModePaiementRef implements OnInit {
  subscription: Subscription | null = null;
  readonly modePaiementRefs = signal<IModePaiementRef[]>([]);

  sortState = sortStateSignal({});

  readonly permissionsUi = inject(UiPermissionService);

  readonly router = inject(Router);
  protected readonly modePaiementRefService = inject(ModePaiementRefService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.modePaiementRefService.modePaiementRefsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.modePaiementRefs.set(this.fillComponentAttributesFromResponseBody([...this.modePaiementRefService.modePaiementRefs()]));
    });
  }

  trackId = (item: IModePaiementRef): number => this.modePaiementRefService.getModePaiementRefIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(modePaiementRef: IModePaiementRef): void {
    const modalRef = this.modalService.open(ModePaiementRefDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.modePaiementRef = modePaiementRef;
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

  protected refineData(data: IModePaiementRef[]): IModePaiementRef[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IModePaiementRef[]): IModePaiementRef[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.modePaiementRefService.modePaiementRefsParams.set(queryObject);
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
