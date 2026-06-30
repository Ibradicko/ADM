import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IInventaireStock } from 'app/entities/inventaire-stock/inventaire-stock.model';
import { InventaireStockService } from 'app/entities/inventaire-stock/service/inventaire-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneInventaireStock } from '../ligne-inventaire-stock.model';
import { LigneInventaireStockService } from '../service/ligne-inventaire-stock.service';

import { LigneInventaireStockFormGroup, LigneInventaireStockFormService } from './ligne-inventaire-stock-form.service';

@Component({
  selector: 'jhi-ligne-inventaire-stock-update',
  templateUrl: './ligne-inventaire-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneInventaireStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneInventaireStock: ILigneInventaireStock | null = null;

  inventaireStocksSharedCollection = signal<IInventaireStock[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected ligneInventaireStockService = inject(LigneInventaireStockService);
  protected ligneInventaireStockFormService = inject(LigneInventaireStockFormService);
  protected inventaireStockService = inject(InventaireStockService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneInventaireStockFormGroup = this.ligneInventaireStockFormService.createLigneInventaireStockFormGroup();

  compareInventaireStock = (o1: IInventaireStock | null, o2: IInventaireStock | null): boolean =>
    this.inventaireStockService.compareInventaireStock(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneInventaireStock }) => {
      this.ligneInventaireStock = ligneInventaireStock;
      if (ligneInventaireStock) {
        this.updateForm(ligneInventaireStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneInventaireStock = this.ligneInventaireStockFormService.getLigneInventaireStock(this.editForm);
    if (ligneInventaireStock.id === null) {
      this.subscribeToSaveResponse(this.ligneInventaireStockService.create(ligneInventaireStock));
    } else {
      this.subscribeToSaveResponse(this.ligneInventaireStockService.update(ligneInventaireStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneInventaireStock | null>): void {
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

  protected updateForm(ligneInventaireStock: ILigneInventaireStock): void {
    this.ligneInventaireStock = ligneInventaireStock;
    this.ligneInventaireStockFormService.resetForm(this.editForm, ligneInventaireStock);

    this.inventaireStocksSharedCollection.update(inventaireStocks =>
      this.inventaireStockService.addInventaireStockToCollectionIfMissing<IInventaireStock>(
        inventaireStocks,
        ligneInventaireStock.inventaire,
      ),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ligneInventaireStock.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.inventaireStockService
      .query()
      .pipe(map((res: HttpResponse<IInventaireStock[]>) => res.body ?? []))
      .pipe(
        map((inventaireStocks: IInventaireStock[]) =>
          this.inventaireStockService.addInventaireStockToCollectionIfMissing<IInventaireStock>(
            inventaireStocks,
            this.ligneInventaireStock?.inventaire,
          ),
        ),
      )
      .subscribe((inventaireStocks: IInventaireStock[]) => this.inventaireStocksSharedCollection.set(inventaireStocks));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.ligneInventaireStock?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
