import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IModePaiementRef, NewModePaiementRef } from '../mode-paiement-ref.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IModePaiementRef for edit and NewModePaiementRefFormGroupInput for create.
 */
type ModePaiementRefFormGroupInput = IModePaiementRef | PartialWithRequiredKeyOf<NewModePaiementRef>;

type ModePaiementRefFormDefaults = Pick<NewModePaiementRef, 'id' | 'actif'>;

type ModePaiementRefFormGroupContent = {
  id: FormControl<IModePaiementRef['id'] | NewModePaiementRef['id']>;
  code: FormControl<IModePaiementRef['code']>;
  libelle: FormControl<IModePaiementRef['libelle']>;
  actif: FormControl<IModePaiementRef['actif']>;
};

export type ModePaiementRefFormGroup = FormGroup<ModePaiementRefFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ModePaiementRefFormService {
  createModePaiementRefFormGroup(modePaiementRef?: ModePaiementRefFormGroupInput): ModePaiementRefFormGroup {
    const modePaiementRefRawValue = {
      ...this.getFormDefaults(),
      ...(modePaiementRef ?? { id: null }),
    };
    return new FormGroup<ModePaiementRefFormGroupContent>({
      id: new FormControl(
        { value: modePaiementRefRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(modePaiementRefRawValue.code, {
        validators: [Validators.required, Validators.maxLength(40)],
      }),
      libelle: new FormControl(modePaiementRefRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      actif: new FormControl(modePaiementRefRawValue.actif, {
        validators: [Validators.required],
      }),
    });
  }

  getModePaiementRef(form: ModePaiementRefFormGroup): IModePaiementRef | NewModePaiementRef {
    return form.getRawValue() as IModePaiementRef | NewModePaiementRef;
  }

  resetForm(form: ModePaiementRefFormGroup, modePaiementRef: ModePaiementRefFormGroupInput): void {
    const modePaiementRefRawValue = { ...this.getFormDefaults(), ...modePaiementRef };
    form.reset({
      ...modePaiementRefRawValue,
      id: { value: modePaiementRefRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ModePaiementRefFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
