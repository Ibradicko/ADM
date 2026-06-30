import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPermissionMetier, NewPermissionMetier } from '../permission-metier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPermissionMetier for edit and NewPermissionMetierFormGroupInput for create.
 */
type PermissionMetierFormGroupInput = IPermissionMetier | PartialWithRequiredKeyOf<NewPermissionMetier>;

type PermissionMetierFormDefaults = Pick<NewPermissionMetier, 'id' | 'profilses'>;

type PermissionMetierFormGroupContent = {
  id: FormControl<IPermissionMetier['id'] | NewPermissionMetier['id']>;
  code: FormControl<IPermissionMetier['code']>;
  libelle: FormControl<IPermissionMetier['libelle']>;
  module: FormControl<IPermissionMetier['module']>;
  description: FormControl<IPermissionMetier['description']>;
  profilses: FormControl<IPermissionMetier['profilses']>;
};

export type PermissionMetierFormGroup = FormGroup<PermissionMetierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PermissionMetierFormService {
  createPermissionMetierFormGroup(permissionMetier?: PermissionMetierFormGroupInput): PermissionMetierFormGroup {
    const permissionMetierRawValue = {
      ...this.getFormDefaults(),
      ...(permissionMetier ?? { id: null }),
    };
    return new FormGroup<PermissionMetierFormGroupContent>({
      id: new FormControl(
        { value: permissionMetierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(permissionMetierRawValue.code, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      libelle: new FormControl(permissionMetierRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      module: new FormControl(permissionMetierRawValue.module, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      description: new FormControl(permissionMetierRawValue.description),
      profilses: new FormControl(permissionMetierRawValue.profilses ?? []),
    });
  }

  getPermissionMetier(form: PermissionMetierFormGroup): IPermissionMetier | NewPermissionMetier {
    return form.getRawValue() as IPermissionMetier | NewPermissionMetier;
  }

  resetForm(form: PermissionMetierFormGroup, permissionMetier: PermissionMetierFormGroupInput): void {
    const permissionMetierRawValue = { ...this.getFormDefaults(), ...permissionMetier };
    form.reset({
      ...permissionMetierRawValue,
      id: { value: permissionMetierRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PermissionMetierFormDefaults {
    return {
      id: null,
      profilses: [],
    };
  }
}
