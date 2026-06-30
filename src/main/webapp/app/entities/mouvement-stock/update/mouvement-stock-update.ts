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
import { TypeMouvementStock } from 'app/entities/enumerations/type-mouvement-stock.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IMouvementStock } from '../mouvement-stock.model';
import { MouvementStockService } from '../service/mouvement-stock.service';

import { MouvementStockFormGroup, MouvementStockFormService } from './mouvement-stock-form.service';

@Component({
  selector: 'jhi-mouvement-stock-update',
  templateUrl: './mouvement-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MouvementStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  mouvementStock: IMouvementStock | null = null;
  typeMouvementStockValues = Object.keys(TypeMouvementStock);
  statutMouvementStockValues = Object.keys(StatutMouvementStock);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected mouvementStockService = inject(MouvementStockService);
  protected mouvementStockFormService = inject(MouvementStockFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MouvementStockFormGroup = this.mouvementStockFormService.createMouvementStockFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mouvementStock }) => {
      this.mouvementStock = mouvementStock;
      if (mouvementStock) {
        this.updateForm(mouvementStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const mouvementStock = this.mouvementStockFormService.getMouvementStock(this.editForm);
    if (mouvementStock.id === null) {
      this.subscribeToSaveResponse(this.mouvementStockService.create(mouvementStock));
    } else {
      this.subscribeToSaveResponse(this.mouvementStockService.update(mouvementStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMouvementStock | null>): void {
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

  protected updateForm(mouvementStock: IMouvementStock): void {
    this.mouvementStock = mouvementStock;
    this.mouvementStockFormService.resetForm(this.editForm, mouvementStock);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, mouvementStock.boutique),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, mouvementStock.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.mouvementStock?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.mouvementStock?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
