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
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IReceptionProduit } from '../reception-produit.model';
import { ReceptionProduitService } from '../service/reception-produit.service';

import { ReceptionProduitFormGroup, ReceptionProduitFormService } from './reception-produit-form.service';

@Component({
  selector: 'jhi-reception-produit-update',
  templateUrl: './reception-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ReceptionProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  receptionProduit: IReceptionProduit | null = null;

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected receptionProduitService = inject(ReceptionProduitService);
  protected receptionProduitFormService = inject(ReceptionProduitFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ReceptionProduitFormGroup = this.receptionProduitFormService.createReceptionProduitFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ receptionProduit }) => {
      this.receptionProduit = receptionProduit;
      if (receptionProduit) {
        this.updateForm(receptionProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const receptionProduit = this.receptionProduitFormService.getReceptionProduit(this.editForm);
    if (receptionProduit.id === null) {
      this.subscribeToSaveResponse(this.receptionProduitService.create(receptionProduit));
    } else {
      this.subscribeToSaveResponse(this.receptionProduitService.update(receptionProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IReceptionProduit | null>): void {
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

  protected updateForm(receptionProduit: IReceptionProduit): void {
    this.receptionProduit = receptionProduit;
    this.receptionProduitFormService.resetForm(this.editForm, receptionProduit);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, receptionProduit.boutique),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, receptionProduit.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.receptionProduit?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.receptionProduit?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
