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
import { UniteMesureDeleteDialog } from '../delete/unite-mesure-delete-dialog';
import { UniteMesureService } from '../service/unite-mesure.service';
import { IUniteMesure } from '../unite-mesure.model';

@Component({
  selector: 'jhi-unite-mesure',
  templateUrl: './unite-mesure.html',
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
export class UniteMesure implements OnInit {
  subscription: Subscription | null = null;
  readonly uniteMesures = signal<IUniteMesure[]>([]);

  sortState = sortStateSignal({});

  readonly permissionsUi = inject(UiPermissionService);

  readonly router = inject(Router);
  protected readonly uniteMesureService = inject(UniteMesureService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.uniteMesureService.uniteMesuresResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.uniteMesures.set(this.fillComponentAttributesFromResponseBody([...this.uniteMesureService.uniteMesures()]));
    });
  }

  trackId = (item: IUniteMesure): number => this.uniteMesureService.getUniteMesureIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(uniteMesure: IUniteMesure): void {
    const modalRef = this.modalService.open(UniteMesureDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.uniteMesure = uniteMesure;
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

  protected refineData(data: IUniteMesure[]): IUniteMesure[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IUniteMesure[]): IUniteMesure[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.uniteMesureService.uniteMesuresParams.set(queryObject);
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
