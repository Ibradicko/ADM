import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAffectationUtilisateur, NewAffectationUtilisateur } from '../affectation-utilisateur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAffectationUtilisateur for edit and NewAffectationUtilisateurFormGroupInput for create.
 */
type AffectationUtilisateurFormGroupInput = IAffectationUtilisateur | PartialWithRequiredKeyOf<NewAffectationUtilisateur>;

type AffectationUtilisateurFormDefaults = Pick<NewAffectationUtilisateur, 'id' | 'actif'>;

type AffectationUtilisateurFormGroupContent = {
  id: FormControl<IAffectationUtilisateur['id'] | NewAffectationUtilisateur['id']>;
  dateDebut: FormControl<IAffectationUtilisateur['dateDebut']>;
  dateFin: FormControl<IAffectationUtilisateur['dateFin']>;
  actif: FormControl<IAffectationUtilisateur['actif']>;
  user: FormControl<IAffectationUtilisateur['user']>;
  boutique: FormControl<IAffectationUtilisateur['boutique']>;
  profil: FormControl<IAffectationUtilisateur['profil']>;
};

export type AffectationUtilisateurFormGroup = FormGroup<AffectationUtilisateurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AffectationUtilisateurFormService {
  createAffectationUtilisateurFormGroup(affectationUtilisateur?: AffectationUtilisateurFormGroupInput): AffectationUtilisateurFormGroup {
    const affectationUtilisateurRawValue = {
      ...this.getFormDefaults(),
      ...(affectationUtilisateur ?? { id: null }),
    };
    return new FormGroup<AffectationUtilisateurFormGroupContent>({
      id: new FormControl(
        { value: affectationUtilisateurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dateDebut: new FormControl(affectationUtilisateurRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFin: new FormControl(affectationUtilisateurRawValue.dateFin),
      actif: new FormControl(affectationUtilisateurRawValue.actif, {
        validators: [Validators.required],
      }),
      user: new FormControl(affectationUtilisateurRawValue.user, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(affectationUtilisateurRawValue.boutique, {
        validators: [Validators.required],
      }),
      profil: new FormControl(affectationUtilisateurRawValue.profil, {
        validators: [Validators.required],
      }),
    });
  }

  getAffectationUtilisateur(form: AffectationUtilisateurFormGroup): IAffectationUtilisateur | NewAffectationUtilisateur {
    return form.getRawValue() as IAffectationUtilisateur | NewAffectationUtilisateur;
  }

  resetForm(form: AffectationUtilisateurFormGroup, affectationUtilisateur: AffectationUtilisateurFormGroupInput): void {
    const affectationUtilisateurRawValue = { ...this.getFormDefaults(), ...affectationUtilisateur };
    form.reset({
      ...affectationUtilisateurRawValue,
      id: { value: affectationUtilisateurRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AffectationUtilisateurFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
