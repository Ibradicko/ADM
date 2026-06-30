import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { StockProduitService } from '../service/stock-produit.service';
import { IStockProduit } from '../stock-produit.model';

import { StockProduitFormGroup, StockProduitFormService } from './stock-produit-form.service';

@Component({
  selector: 'jhi-stock-produit-update',
  templateUrl: './stock-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class StockProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  stockProduit: IStockProduit | null = null;

  produitsSharedCollection = signal<IProduit[]>([]);
  depotStocksSharedCollection = signal<IDepotStock[]>([]);

  protected stockProduitService = inject(StockProduitService);
  protected stockProduitFormService = inject(StockProduitFormService);
  protected produitService = inject(ProduitService);
  protected depotStockService = inject(DepotStockService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StockProduitFormGroup = this.stockProduitFormService.createStockProduitFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  compareDepotStock = (o1: IDepotStock | null, o2: IDepotStock | null): boolean => this.depotStockService.compareDepotStock(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stockProduit }) => {
      this.stockProduit = stockProduit;
      if (stockProduit) {
        this.updateForm(stockProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const stockProduit = this.stockProduitFormService.getStockProduit(this.editForm);
    if (stockProduit.id === null) {
      this.subscribeToSaveResponse(this.stockProduitService.create(stockProduit));
    } else {
      this.subscribeToSaveResponse(this.stockProduitService.update(stockProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IStockProduit | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(stockProduit: IStockProduit): void {
    this.stockProduit = stockProduit;
    this.stockProduitFormService.resetForm(this.editForm, stockProduit);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, stockProduit.produit),
    );
    this.depotStocksSharedCollection.update(depotStocks =>
      this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, stockProduit.depot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) => this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.stockProduit?.produit)),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));

    this.depotStockService
      .query()
      .pipe(map((res: HttpResponse<IDepotStock[]>) => res.body ?? []))
      .pipe(
        map((depotStocks: IDepotStock[]) =>
          this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, this.stockProduit?.depot),
        ),
      )
      .subscribe((depotStocks: IDepotStock[]) => this.depotStocksSharedCollection.set(depotStocks));
  }
}
