import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { StatutInventaire } from 'app/entities/enumerations/statut-inventaire.model';
import { TypeInventaire } from 'app/entities/enumerations/type-inventaire.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IInventaireStock } from '../inventaire-stock.model';
import { InventaireStockService } from '../service/inventaire-stock.service';

import { InventaireStockFormGroup, InventaireStockFormService } from './inventaire-stock-form.service';

@Component({
  selector: 'jhi-inventaire-stock-update',
  templateUrl: './inventaire-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class InventaireStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  inventaireStock: IInventaireStock | null = null;
  typeInventaireValues = Object.keys(TypeInventaire);
  statutInventaireValues = Object.keys(StatutInventaire);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  depotStocksSharedCollection = signal<IDepotStock[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected inventaireStockService = inject(InventaireStockService);
  protected inventaireStockFormService = inject(InventaireStockFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected depotStockService = inject(DepotStockService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InventaireStockFormGroup = this.inventaireStockFormService.createInventaireStockFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareDepotStock = (o1: IDepotStock | null, o2: IDepotStock | null): boolean => this.depotStockService.compareDepotStock(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventaireStock }) => {
      this.inventaireStock = inventaireStock;
      if (inventaireStock) {
        this.updateForm(inventaireStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const inventaireStock = this.inventaireStockFormService.getInventaireStock(this.editForm);
    if (inventaireStock.id === null) {
      this.subscribeToSaveResponse(this.inventaireStockService.create(inventaireStock));
    } else {
      this.subscribeToSaveResponse(this.inventaireStockService.update(inventaireStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IInventaireStock | null>): void {
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

  protected updateForm(inventaireStock: IInventaireStock): void {
    this.inventaireStock = inventaireStock;
    this.inventaireStockFormService.resetForm(this.editForm, inventaireStock);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, inventaireStock.boutique),
    );
    this.depotStocksSharedCollection.update(depotStocks =>
      this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, inventaireStock.depot),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, inventaireStock.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.inventaireStock?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.depotStockService
      .query()
      .pipe(map((res: HttpResponse<IDepotStock[]>) => res.body ?? []))
      .pipe(
        map((depotStocks: IDepotStock[]) =>
          this.depotStockService.addDepotStockToCollectionIfMissing<IDepotStock>(depotStocks, this.inventaireStock?.depot),
        ),
      )
      .subscribe((depotStocks: IDepotStock[]) => this.depotStocksSharedCollection.set(depotStocks));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.inventaireStock?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
