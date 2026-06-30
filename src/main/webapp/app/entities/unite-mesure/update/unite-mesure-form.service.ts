import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUniteMesure, NewUniteMesure } from '../unite-mesure.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUniteMesure for edit and NewUniteMesureFormGroupInput for create.
 */
type UniteMesureFormGroupInput = IUniteMesure | PartialWithRequiredKeyOf<NewUniteMesure>;

type UniteMesureFormDefaults = Pick<NewUniteMesure, 'id'>;

type UniteMesureFormGroupContent = {
  id: FormControl<IUniteMesure['id'] | NewUniteMesure['id']>;
  code: FormControl<IUniteMesure['code']>;
  libelle: FormControl<IUniteMesure['libelle']>;
  symbole: FormControl<IUniteMesure['symbole']>;
};

export type UniteMesureFormGroup = FormGroup<UniteMesureFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UniteMesureFormService {
  createUniteMesureFormGroup(uniteMesure?: UniteMesureFormGroupInput): UniteMesureFormGroup {
    const uniteMesureRawValue = {
      ...this.getFormDefaults(),
      ...(uniteMesure ?? { id: null }),
    };
    return new FormGroup<UniteMesureFormGroupContent>({
      id: new FormControl(
        { value: uniteMesureRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(uniteMesureRawValue.code, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      libelle: new FormControl(uniteMesureRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      symbole: new FormControl(uniteMesureRawValue.symbole, {
        validators: [Validators.maxLength(20)],
      }),
    });
  }

  getUniteMesure(form: UniteMesureFormGroup): IUniteMesure | NewUniteMesure {
    return form.getRawValue() as IUniteMesure | NewUniteMesure;
  }

  resetForm(form: UniteMesureFormGroup, uniteMesure: UniteMesureFormGroupInput): void {
    const uniteMesureRawValue = { ...this.getFormDefaults(), ...uniteMesure };
    form.reset({
      ...uniteMesureRawValue,
      id: { value: uniteMesureRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): UniteMesureFormDefaults {
    return {
      id: null,
    };
  }
}
