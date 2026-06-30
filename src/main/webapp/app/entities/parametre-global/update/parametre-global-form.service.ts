import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IParametreGlobal, NewParametreGlobal } from '../parametre-global.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParametreGlobal for edit and NewParametreGlobalFormGroupInput for create.
 */
type ParametreGlobalFormGroupInput = IParametreGlobal | PartialWithRequiredKeyOf<NewParametreGlobal>;

type ParametreGlobalFormDefaults = Pick<NewParametreGlobal, 'id' | 'actif'>;

type ParametreGlobalFormGroupContent = {
  id: FormControl<IParametreGlobal['id'] | NewParametreGlobal['id']>;
  code: FormControl<IParametreGlobal['code']>;
  valeur: FormControl<IParametreGlobal['valeur']>;
  description: FormControl<IParametreGlobal['description']>;
  actif: FormControl<IParametreGlobal['actif']>;
};

export type ParametreGlobalFormGroup = FormGroup<ParametreGlobalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParametreGlobalFormService {
  createParametreGlobalFormGroup(parametreGlobal?: ParametreGlobalFormGroupInput): ParametreGlobalFormGroup {
    const parametreGlobalRawValue = {
      ...this.getFormDefaults(),
      ...(parametreGlobal ?? { id: null }),
    };
    return new FormGroup<ParametreGlobalFormGroupContent>({
      id: new FormControl(
        { value: parametreGlobalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(parametreGlobalRawValue.code, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      valeur: new FormControl(parametreGlobalRawValue.valeur, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      description: new FormControl(parametreGlobalRawValue.description),
      actif: new FormControl(parametreGlobalRawValue.actif, {
        validators: [Validators.required],
      }),
    });
  }

  getParametreGlobal(form: ParametreGlobalFormGroup): IParametreGlobal | NewParametreGlobal {
    return form.getRawValue() as IParametreGlobal | NewParametreGlobal;
  }

  resetForm(form: ParametreGlobalFormGroup, parametreGlobal: ParametreGlobalFormGroupInput): void {
    const parametreGlobalRawValue = { ...this.getFormDefaults(), ...parametreGlobal };
    form.reset({
      ...parametreGlobalRawValue,
      id: { value: parametreGlobalRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ParametreGlobalFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
