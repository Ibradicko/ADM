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
import { EtiquetteProduitDeleteDialog } from '../delete/etiquette-produit-delete-dialog';
import { IEtiquetteProduit } from '../etiquette-produit.model';
import { EtiquetteProduitService } from '../service/etiquette-produit.service';

@Component({
  selector: 'jhi-etiquette-produit',
  templateUrl: './etiquette-produit.html',
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
export class EtiquetteProduit implements OnInit {
  subscription: Subscription | null = null;
  readonly etiquetteProduits = signal<IEtiquetteProduit[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly etiquetteProduitService = inject(EtiquetteProduitService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.etiquetteProduitService.etiquetteProduitsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.etiquetteProduits.set(this.fillComponentAttributesFromResponseBody([...this.etiquetteProduitService.etiquetteProduits()]));
    });
  }

  trackId = (item: IEtiquetteProduit): number => this.etiquetteProduitService.getEtiquetteProduitIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  delete(etiquetteProduit: IEtiquetteProduit): void {
    const modalRef = this.modalService.open(EtiquetteProduitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.etiquetteProduit = etiquetteProduit;
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

  protected refineData(data: IEtiquetteProduit[]): IEtiquetteProduit[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IEtiquetteProduit[]): IEtiquetteProduit[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.etiquetteProduitService.etiquetteProduitsParams.set(queryObject);
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
