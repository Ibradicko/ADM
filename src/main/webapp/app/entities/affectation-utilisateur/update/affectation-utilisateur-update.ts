import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { UiPermissionService } from 'app/core/services/ui-permission.service';

import { IAffectationUtilisateur } from '../affectation-utilisateur.model';
import { AffectationUtilisateurService } from '../service/affectation-utilisateur.service';

import { AffectationUtilisateurFormGroup, AffectationUtilisateurFormService } from './affectation-utilisateur-form.service';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';

@Component({
  selector: 'jhi-affectation-utilisateur-update',
  templateUrl: './affectation-utilisateur-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class AffectationUtilisateurUpdate implements OnInit {
  readonly isSaving = signal(false);
  readonly validationMessageKey = signal<string | null>(null);
  affectationUtilisateur: IAffectationUtilisateur | null = null;

  usersSharedCollection = signal<IUser[]>([]);
  boutiquesSharedCollection = signal<IBoutique[]>([]);
  profilMetiersSharedCollection = signal<IProfilMetier[]>([]);

  protected affectationUtilisateurService = inject(AffectationUtilisateurService);
  protected affectationUtilisateurFormService = inject(AffectationUtilisateurFormService);
  protected userService = inject(UserService);
  protected boutiqueService = inject(BoutiqueService);
  protected profilMetierService = inject(ProfilMetierService);
  protected activatedRoute = inject(ActivatedRoute);
  protected permissionsUi = inject(UiPermissionService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AffectationUtilisateurFormGroup = this.affectationUtilisateurFormService.createAffectationUtilisateurFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareProfilMetier = (o1: IProfilMetier | null, o2: IProfilMetier | null): boolean =>
    this.profilMetierService.compareProfilMetier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ affectationUtilisateur }) => {
      this.affectationUtilisateur = affectationUtilisateur;
      if (affectationUtilisateur) {
        this.updateForm(affectationUtilisateur);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.validationMessageKey.set(null);
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      this.validationMessageKey.set('admSupervisionVentesApp.affectationUtilisateur.form.validationRequired');
      return;
    }

    this.isSaving.set(true);
    const affectationUtilisateur = this.affectationUtilisateurFormService.getAffectationUtilisateur(this.editForm);
    if (affectationUtilisateur.id === null) {
      this.subscribeToSaveResponse(this.affectationUtilisateurService.create(affectationUtilisateur));
    } else {
      this.subscribeToSaveResponse(this.affectationUtilisateurService.update(affectationUtilisateur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAffectationUtilisateur | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    this.validationMessageKey.set('admSupervisionVentesApp.affectationUtilisateur.form.saveFailed');
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(affectationUtilisateur: IAffectationUtilisateur): void {
    this.affectationUtilisateur = affectationUtilisateur;
    this.affectationUtilisateurFormService.resetForm(this.editForm, affectationUtilisateur);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, affectationUtilisateur.user));
    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, affectationUtilisateur.boutique),
    );
    this.profilMetiersSharedCollection.update(profilMetiers =>
      this.profilMetierService.addProfilMetierToCollectionIfMissing<IProfilMetier>(profilMetiers, affectationUtilisateur.profil),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.affectationUtilisateur?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(map((boutiques: IBoutique[]) => this.restreindreBoutiques(boutiques)))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.affectationUtilisateur?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.profilMetierService
      .query()
      .pipe(map((res: HttpResponse<IProfilMetier[]>) => res.body ?? []))
      .pipe(map((profilMetiers: IProfilMetier[]) => this.restreindreProfils(profilMetiers)))
      .pipe(
        map((profilMetiers: IProfilMetier[]) =>
          this.profilMetierService.addProfilMetierToCollectionIfMissing<IProfilMetier>(profilMetiers, this.affectationUtilisateur?.profil),
        ),
      )
      .subscribe((profilMetiers: IProfilMetier[]) => this.profilMetiersSharedCollection.set(profilMetiers));
  }

  /**
   * Le locataire et le manager boutique ne doivent voir/affecter que leurs propres boutiques.
   * Admin et Manager ADM gardent un acces complet.
   */
  protected restreindreBoutiques(boutiques: IBoutique[]): IBoutique[] {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.estProfilSupervision()) {
      return boutiques;
    }
    const idsAccessibles = new Set(this.permissionsUi.boutiqueIds());
    return boutiques.filter(boutique => idsAccessibles.has(boutique.id));
  }

  /**
   * Le locataire peut affecter Manager Boutique et Vendeur ; un Manager Boutique (non locataire)
   * ne peut affecter que Vendeur. Admin et Manager ADM gardent un acces complet.
   */
  protected restreindreProfils(profilMetiers: IProfilMetier[]): IProfilMetier[] {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.estProfilSupervision()) {
      return profilMetiers;
    }
    const codesAutorises = this.permissionsUi.estLocataire() ? ['MANAGER_BOUTIQUE', 'VENDEUR'] : ['VENDEUR'];
    return profilMetiers.filter(profilMetier => codesAutorises.includes((profilMetier.code ?? '').toUpperCase()));
  }
}
