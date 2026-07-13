import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { switchMap } from 'rxjs';

import { LANGUAGES } from 'app/config/language.constants';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';
import { AuthorityService } from '../../authority/service/authority.service';
import { UserManagementService } from '../service/user-management.service';
import { IUserManagement } from '../user-management.model';

const userTemplate = {} as IUserManagement;

const newUser: IUserManagement = {
  langKey: 'fr',
  activated: true,
  authorities: ['ROLE_USER', 'ROLE_MANAGER_ADM'],
} as IUserManagement;

const MANAGER_ADM_AUTHORITIES = ['ROLE_USER', 'ROLE_MANAGER_ADM'];
const MANAGER_ADM_PROFILE = 'MANAGER_ADM';

@Component({
  selector: 'jhi-user-management-update',
  templateUrl: './user-management-update.html',
  styleUrl: './user-management-update.scss',
  imports: [FindLanguageFromKeyPipe, TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class UserManagementUpdate implements OnInit {
  languages = LANGUAGES;
  readonly isSaving = signal(false);
  readonly boutiques = signal<IBoutique[]>([]);
  readonly managerAdmProfil = signal<IProfilMetier | null>(null);

  editForm = new FormGroup({
    id: new FormControl(userTemplate.id),
    login: new FormControl(userTemplate.login, {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    firstName: new FormControl(userTemplate.firstName, { validators: [Validators.maxLength(50)] }),
    lastName: new FormControl(userTemplate.lastName, { validators: [Validators.maxLength(50)] }),
    email: new FormControl(userTemplate.email, {
      nonNullable: true,
      validators: [Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    activated: new FormControl(userTemplate.activated, { nonNullable: true }),
    langKey: new FormControl(userTemplate.langKey, { nonNullable: true }),
    authorities: new FormControl(userTemplate.authorities, { nonNullable: true }),
    boutiqueId: new FormControl<number | null>(null),
  });

  protected readonly authorityService = inject(AuthorityService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly authorities = computed(() => this.authorityService.authorities().map(authority => authority.name));
  private readonly userService = inject(UserManagementService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);
  private readonly boutiqueService = inject(BoutiqueService);
  private readonly profilMetierService = inject(ProfilMetierService);
  private readonly route = inject(ActivatedRoute);

  constructor() {
    this.authorityService.authoritiesParams.set({});
  }

  ngOnInit(): void {
    this.loadManagerContext();
    this.route.data.subscribe(({ userManagement }) => {
      if (userManagement) {
        this.editForm.reset({ ...userManagement, authorities: this.normalizeManagerAdmAuthorities(userManagement.authorities) });
        this.editForm.controls.boutiqueId.clearValidators();
      } else {
        this.editForm.reset(newUser);
        this.editForm.controls.boutiqueId.setValidators([Validators.required]);
      }
      this.editForm.controls.boutiqueId.updateValueAndValidity();
      this.editForm.controls.authorities.disable();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const { boutiqueId: _boutiqueId, ...rawUser } = this.editForm.getRawValue();
    const user = { ...rawUser, authorities: MANAGER_ADM_AUTHORITIES };
    if (user.id === null) {
      const boutiqueId = this.editForm.controls.boutiqueId.value;
      const profil = this.managerAdmProfil();
      if (!boutiqueId || !profil) {
        this.onSaveError();
        return;
      }

      this.userService
        .create(user)
        .pipe(
          switchMap(createdUser =>
            this.affectationUtilisateurService.create({
              id: null,
              dateDebut: dayjs(),
              actif: true,
              user: {
                id: createdUser.id!,
                login: createdUser.login,
                firstName: createdUser.firstName,
                lastName: createdUser.lastName,
                email: createdUser.email,
              },
              boutique: { id: boutiqueId },
              profil: { id: profil.id, code: profil.code },
            }),
          ),
        )
        .subscribe({
          next: () => this.onSaveSuccess(),
          error: () => this.onSaveError(),
        });
    } else {
      this.userService.update(user).subscribe({
        next: () => this.onSaveSuccess(),
        error: () => this.onSaveError(),
      });
    }
  }

  private onSaveSuccess(): void {
    this.isSaving.set(false);
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving.set(false);
  }

  private normalizeManagerAdmAuthorities(authorities: string[] | undefined): string[] {
    return [...new Set([...(authorities ?? []), ...MANAGER_ADM_AUTHORITIES])];
  }

  private loadManagerContext(): void {
    this.boutiqueService.query({ size: 1000, sort: ['nom,asc'] }).subscribe(response => this.boutiques.set(response.body ?? []));
    this.profilMetierService
      .query({ size: 1000 })
      .subscribe(response => this.managerAdmProfil.set((response.body ?? []).find(profil => profil.code === MANAGER_ADM_PROFILE) ?? null));
  }
}
