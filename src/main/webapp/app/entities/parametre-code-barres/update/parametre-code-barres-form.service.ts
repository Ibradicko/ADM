import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IParametreCodeBarres, NewParametreCodeBarres } from '../parametre-code-barres.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParametreCodeBarres for edit and NewParametreCodeBarresFormGroupInput for create.
 */
type ParametreCodeBarresFormGroupInput = IParametreCodeBarres | PartialWithRequiredKeyOf<NewParametreCodeBarres>;

type ParametreCodeBarresFormDefaults = Pick<NewParametreCodeBarres, 'id' | 'actif'>;

type ParametreCodeBarresFormGroupContent = {
  id: FormControl<IParametreCodeBarres['id'] | NewParametreCodeBarres['id']>;
  formatParDefaut: FormControl<IParametreCodeBarres['formatParDefaut']>;
  prefixe: FormControl<IParametreCodeBarres['prefixe']>;
  longueur: FormControl<IParametreCodeBarres['longueur']>;
  actif: FormControl<IParametreCodeBarres['actif']>;
};

export type ParametreCodeBarresFormGroup = FormGroup<ParametreCodeBarresFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParametreCodeBarresFormService {
  createParametreCodeBarresFormGroup(parametreCodeBarres?: ParametreCodeBarresFormGroupInput): ParametreCodeBarresFormGroup {
    const parametreCodeBarresRawValue = {
      ...this.getFormDefaults(),
      ...(parametreCodeBarres ?? { id: null }),
    };
    return new FormGroup<ParametreCodeBarresFormGroupContent>({
      id: new FormControl(
        { value: parametreCodeBarresRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      formatParDefaut: new FormControl(parametreCodeBarresRawValue.formatParDefaut, {
        validators: [Validators.required],
      }),
      prefixe: new FormControl(parametreCodeBarresRawValue.prefixe, {
        validators: [Validators.maxLength(20)],
      }),
      longueur: new FormControl(parametreCodeBarresRawValue.longueur, {
        validators: [Validators.min(8), Validators.max(30)],
      }),
      actif: new FormControl(parametreCodeBarresRawValue.actif, {
        validators: [Validators.required],
      }),
    });
  }

  getParametreCodeBarres(form: ParametreCodeBarresFormGroup): IParametreCodeBarres | NewParametreCodeBarres {
    return form.getRawValue() as IParametreCodeBarres | NewParametreCodeBarres;
  }

  resetForm(form: ParametreCodeBarresFormGroup, parametreCodeBarres: ParametreCodeBarresFormGroupInput): void {
    const parametreCodeBarresRawValue = { ...this.getFormDefaults(), ...parametreCodeBarres };
    form.reset({
      ...parametreCodeBarresRawValue,
      id: { value: parametreCodeBarresRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ParametreCodeBarresFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
