import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfilMetier, NewProfilMetier } from '../profil-metier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfilMetier for edit and NewProfilMetierFormGroupInput for create.
 */
type ProfilMetierFormGroupInput = IProfilMetier | PartialWithRequiredKeyOf<NewProfilMetier>;

type ProfilMetierFormDefaults = Pick<NewProfilMetier, 'id' | 'permissionses'>;

type ProfilMetierFormGroupContent = {
  id: FormControl<IProfilMetier['id'] | NewProfilMetier['id']>;
  code: FormControl<IProfilMetier['code']>;
  libelle: FormControl<IProfilMetier['libelle']>;
  description: FormControl<IProfilMetier['description']>;
  statut: FormControl<IProfilMetier['statut']>;
  permissionses: FormControl<IProfilMetier['permissionses']>;
};

export type ProfilMetierFormGroup = FormGroup<ProfilMetierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfilMetierFormService {
  createProfilMetierFormGroup(profilMetier?: ProfilMetierFormGroupInput): ProfilMetierFormGroup {
    const profilMetierRawValue = {
      ...this.getFormDefaults(),
      ...(profilMetier ?? { id: null }),
    };
    return new FormGroup<ProfilMetierFormGroupContent>({
      id: new FormControl(
        { value: profilMetierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(profilMetierRawValue.code, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      libelle: new FormControl(profilMetierRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      description: new FormControl(profilMetierRawValue.description),
      statut: new FormControl(profilMetierRawValue.statut, {
        validators: [Validators.required],
      }),
      permissionses: new FormControl(profilMetierRawValue.permissionses ?? []),
    });
  }

  getProfilMetier(form: ProfilMetierFormGroup): IProfilMetier | NewProfilMetier {
    return form.getRawValue() as IProfilMetier | NewProfilMetier;
  }

  resetForm(form: ProfilMetierFormGroup, profilMetier: ProfilMetierFormGroupInput): void {
    const profilMetierRawValue = { ...this.getFormDefaults(), ...profilMetier };
    form.reset({
      ...profilMetierRawValue,
      id: { value: profilMetierRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfilMetierFormDefaults {
    return {
      id: null,
      permissionses: [],
    };
  }
}
