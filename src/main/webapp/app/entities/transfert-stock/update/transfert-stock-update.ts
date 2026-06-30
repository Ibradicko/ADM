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
import { StatutMouvementStock } from 'app/entities/enumerations/statut-mouvement-stock.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TransfertStockService } from '../service/transfert-stock.service';
import { ITransfertStock } from '../transfert-stock.model';

import { TransfertStockFormGroup, TransfertStockFormService } from './transfert-stock-form.service';

@Component({
  selector: 'jhi-transfert-stock-update',
  templateUrl: './transfert-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TransfertStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  transfertStock: ITransfertStock | null = null;
  statutMouvementStockValues = Object.keys(StatutMouvementStock);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected transfertStockService = inject(TransfertStockService);
  protected transfertStockFormService = inject(TransfertStockFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TransfertStockFormGroup = this.transfertStockFormService.createTransfertStockFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transfertStock }) => {
      this.transfertStock = transfertStock;
      if (transfertStock) {
        this.updateForm(transfertStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const transfertStock = this.transfertStockFormService.getTransfertStock(this.editForm);
    if (transfertStock.id === null) {
      this.subscribeToSaveResponse(this.transfertStockService.create(transfertStock));
    } else {
      this.subscribeToSaveResponse(this.transfertStockService.update(transfertStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITransfertStock | null>): void {
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

  protected updateForm(transfertStock: ITransfertStock): void {
    this.transfertStock = transfertStock;
    this.transfertStockFormService.resetForm(this.editForm, transfertStock);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(
        boutiques,
        transfertStock.boutiqueOrigine,
        transfertStock.boutiqueDestination,
      ),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, transfertStock.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(
            boutiques,
            this.transfertStock?.boutiqueOrigine,
            this.transfertStock?.boutiqueDestination,
          ),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.transfertStock?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
