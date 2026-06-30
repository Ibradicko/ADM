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
import { IMouvementStock } from 'app/entities/mouvement-stock/mouvement-stock.model';
import { MouvementStockService } from 'app/entities/mouvement-stock/service/mouvement-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ILigneMouvementStock } from '../ligne-mouvement-stock.model';
import { LigneMouvementStockService } from '../service/ligne-mouvement-stock.service';

import { LigneMouvementStockFormGroup, LigneMouvementStockFormService } from './ligne-mouvement-stock-form.service';

@Component({
  selector: 'jhi-ligne-mouvement-stock-update',
  templateUrl: './ligne-mouvement-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneMouvementStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneMouvementStock: ILigneMouvementStock | null = null;

  mouvementStocksSharedCollection = signal<IMouvementStock[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);
  depotStocksSharedCollection = signal<IDepotStock[]>([]);

  protected ligneMouvementStockService = inject(LigneMouvementStockService);
  protected ligneMouvementStockFormService = inject(LigneMouvementStockFormService);
  protected mouvementStockService = inject(MouvementStockService);
  protected produitService = inject(ProduitService);
  protected depotStockService = inject(DepotStockService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneMouvementStockFormGroup = this.ligneMouvementStockFormService.createLigneMouvementStockFormGroup();

  compareMouvementStock = (o1: IMouvementStock | null, o2: IMouvementStock | null): boolean =>
    this.mouvementStockService.compareMouvementStock(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  compareDepotStock = (o1: IDepotStock | null, o2: IDepotStock | null): boolean => this.depotStockService.compareDepotStock(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneMouvementStock }) => {
      this.ligneMouvementStock = ligneMouvementStock;
      if (ligneMouvementStock) {
        this.updateForm(ligneMouvementStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneMouvementStock = this.ligneMouvementStockFormService.getLigneMouvementStock(this.editForm);
    if (ligneMouvementStock.id === null) {
      this.subscribeToSaveResponse(this.ligneMouvementStockService.create(ligneMouvementStock));
    } else {
      this.subscribeToSaveResponse(this.ligneMouvementStockService.update(ligneMouvementStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneMouvementStock | null>): void {
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

  protected updateForm(ligneMouvementStock: ILigneMouvementStock): void {
    this.ligneMouvementStock = ligneMouvementStock;
    this.ligneMouvementStockFormService.resetForm(this.editForm, ligneMouvementStock);

    this.mouvementStocksSharedCollection.update(mouvementStocks =>
      this.mouvementStockService.addMouvementStockToCollectionIfMissing<IMouvementStock>(mouvementStocks, ligneMouvementStock.mouvement),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ligneMouvementStock.produit),
    );
    this.depotStocksSharedCollection.update(depotStocks =>
      this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, ligneMouvementStock.depot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.mouvementStockService
      .query()
      .pipe(map((res: HttpResponse<IMouvementStock[]>) => res.body ?? []))
      .pipe(
        map((mouvementStocks: IMouvementStock[]) =>
          this.mouvementStockService.addMouvementStockToCollectionIfMissing<IMouvementStock>(
            mouvementStocks,
            this.ligneMouvementStock?.mouvement,
          ),
        ),
      )
      .subscribe((mouvementStocks: IMouvementStock[]) => this.mouvementStocksSharedCollection.set(mouvementStocks));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.ligneMouvementStock?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));

    this.depotStockService
      .query()
      .pipe(map((res: HttpResponse<IDepotStock[]>) => res.body ?? []))
      .pipe(
        map((depotStocks: IDepotStock[]) =>
          this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, this.ligneMouvementStock?.depot),
        ),
      )
      .subscribe((depotStocks: IDepotStock[]) => this.depotStocksSharedCollection.set(depotStocks));
  }
}
