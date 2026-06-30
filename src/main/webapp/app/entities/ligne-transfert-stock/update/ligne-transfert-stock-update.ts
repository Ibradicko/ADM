import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { TransfertStockService } from 'app/entities/transfert-stock/service/transfert-stock.service';
import { ITransfertStock } from 'app/entities/transfert-stock/transfert-stock.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneTransfertStock } from '../ligne-transfert-stock.model';
import { LigneTransfertStockService } from '../service/ligne-transfert-stock.service';

import { LigneTransfertStockFormGroup, LigneTransfertStockFormService } from './ligne-transfert-stock-form.service';

@Component({
  selector: 'jhi-ligne-transfert-stock-update',
  templateUrl: './ligne-transfert-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneTransfertStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneTransfertStock: ILigneTransfertStock | null = null;

  transfertStocksSharedCollection = signal<ITransfertStock[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected ligneTransfertStockService = inject(LigneTransfertStockService);
  protected ligneTransfertStockFormService = inject(LigneTransfertStockFormService);
  protected transfertStockService = inject(TransfertStockService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneTransfertStockFormGroup = this.ligneTransfertStockFormService.createLigneTransfertStockFormGroup();

  compareTransfertStock = (o1: ITransfertStock | null, o2: ITransfertStock | null): boolean =>
    this.transfertStockService.compareTransfertStock(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneTransfertStock }) => {
      this.ligneTransfertStock = ligneTransfertStock;
      if (ligneTransfertStock) {
        this.updateForm(ligneTransfertStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneTransfertStock = this.ligneTransfertStockFormService.getLigneTransfertStock(this.editForm);
    if (ligneTransfertStock.id === null) {
      this.subscribeToSaveResponse(this.ligneTransfertStockService.create(ligneTransfertStock));
    } else {
      this.subscribeToSaveResponse(this.ligneTransfertStockService.update(ligneTransfertStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneTransfertStock | null>): void {
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

  protected updateForm(ligneTransfertStock: ILigneTransfertStock): void {
    this.ligneTransfertStock = ligneTransfertStock;
    this.ligneTransfertStockFormService.resetForm(this.editForm, ligneTransfertStock);

    this.transfertStocksSharedCollection.update(transfertStocks =>
      this.transfertStockService.addTransfertStockToCollectionIfMissing<ITransfertStock>(transfertStocks, ligneTransfertStock.transfert),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ligneTransfertStock.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transfertStockService
      .query()
      .pipe(map((res: HttpResponse<ITransfertStock[]>) => res.body ?? []))
      .pipe(
        map((transfertStocks: ITransfertStock[]) =>
          this.transfertStockService.addTransfertStockToCollectionIfMissing<ITransfertStock>(
            transfertStocks,
            this.ligneTransfertStock?.transfert,
          ),
        ),
      )
      .subscribe((transfertStocks: ITransfertStock[]) => this.transfertStocksSharedCollection.set(transfertStocks));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.ligneTransfertStock?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
