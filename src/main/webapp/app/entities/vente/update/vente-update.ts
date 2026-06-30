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
import { StatutVente } from 'app/entities/enumerations/statut-vente.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { VenteService } from '../service/vente.service';
import { IVente } from '../vente.model';

import { VenteFormGroup, VenteFormService } from './vente-form.service';
import { UserService } from 'app/entities/user/service/user.service';

@Component({
  selector: 'jhi-vente-update',
  templateUrl: './vente-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class VenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  vente: IVente | null = null;
  statutVenteValues = Object.keys(StatutVente);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  locatairesSharedCollection = signal<ILocataire[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected venteService = inject(VenteService);
  protected venteFormService = inject(VenteFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected locataireService = inject(LocataireService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VenteFormGroup = this.venteFormService.createVenteFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareLocataire = (o1: ILocataire | null, o2: ILocataire | null): boolean => this.locataireService.compareLocataire(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vente }) => {
      this.vente = vente;
      if (vente) {
        this.updateForm(vente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const vente = this.venteFormService.getVente(this.editForm);
    if (vente.id === null) {
      this.subscribeToSaveResponse(this.venteService.create(vente));
    } else {
      this.subscribeToSaveResponse(this.venteService.update(vente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IVente | null>): void {
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

  protected updateForm(vente: IVente): void {
    this.vente = vente;
    this.venteFormService.resetForm(this.editForm, vente);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, vente.boutique),
    );
    this.locatairesSharedCollection.update(locataires =>
      this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, vente.locataire),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, vente.vendeur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) => this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.vente?.boutique)),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.locataireService
      .query()
      .pipe(map((res: HttpResponse<ILocataire[]>) => res.body ?? []))
      .pipe(
        map((locataires: ILocataire[]) =>
          this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, this.vente?.locataire),
        ),
      )
      .subscribe((locataires: ILocataire[]) => this.locatairesSharedCollection.set(locataires));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.vente?.vendeur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
