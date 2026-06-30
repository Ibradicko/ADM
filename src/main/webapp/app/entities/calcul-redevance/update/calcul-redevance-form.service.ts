import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICalculRedevance, NewCalculRedevance } from '../calcul-redevance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICalculRedevance for edit and NewCalculRedevanceFormGroupInput for create.
 */
type CalculRedevanceFormGroupInput = ICalculRedevance | PartialWithRequiredKeyOf<NewCalculRedevance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICalculRedevance | NewCalculRedevance> = Omit<T, 'dateCalcul'> & {
  dateCalcul?: string | null;
};

type CalculRedevanceFormRawValue = FormValueOf<ICalculRedevance>;

type NewCalculRedevanceFormRawValue = FormValueOf<NewCalculRedevance>;

type CalculRedevanceFormDefaults = Pick<NewCalculRedevance, 'id' | 'dateCalcul'>;

type CalculRedevanceFormGroupContent = {
  id: FormControl<CalculRedevanceFormRawValue['id'] | NewCalculRedevance['id']>;
  reference: FormControl<CalculRedevanceFormRawValue['reference']>;
  periodeDebut: FormControl<CalculRedevanceFormRawValue['periodeDebut']>;
  periodeFin: FormControl<CalculRedevanceFormRawValue['periodeFin']>;
  chiffreAffaires: FormControl<CalculRedevanceFormRawValue['chiffreAffaires']>;
  montantRedevance: FormControl<CalculRedevanceFormRawValue['montantRedevance']>;
  statut: FormControl<CalculRedevanceFormRawValue['statut']>;
  dateCalcul: FormControl<CalculRedevanceFormRawValue['dateCalcul']>;
  boutique: FormControl<CalculRedevanceFormRawValue['boutique']>;
  locataire: FormControl<CalculRedevanceFormRawValue['locataire']>;
};

export type CalculRedevanceFormGroup = FormGroup<CalculRedevanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CalculRedevanceFormService {
  createCalculRedevanceFormGroup(calculRedevance?: CalculRedevanceFormGroupInput): CalculRedevanceFormGroup {
    const calculRedevanceRawValue = this.convertCalculRedevanceToCalculRedevanceRawValue({
      ...this.getFormDefaults(),
      ...(calculRedevance ?? { id: null }),
    });
    return new FormGroup<CalculRedevanceFormGroupContent>({
      id: new FormControl(
        { value: calculRedevanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(calculRedevanceRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      periodeDebut: new FormControl(calculRedevanceRawValue.periodeDebut, {
        validators: [Validators.required],
      }),
      periodeFin: new FormControl(calculRedevanceRawValue.periodeFin, {
        validators: [Validators.required],
      }),
      chiffreAffaires: new FormControl(calculRedevanceRawValue.chiffreAffaires, {
        validators: [Validators.required, Validators.min(0)],
      }),
      montantRedevance: new FormControl(calculRedevanceRawValue.montantRedevance, {
        validators: [Validators.required, Validators.min(0)],
      }),
      statut: new FormControl(calculRedevanceRawValue.statut, {
        validators: [Validators.required],
      }),
      dateCalcul: new FormControl(calculRedevanceRawValue.dateCalcul, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(calculRedevanceRawValue.boutique, {
        validators: [Validators.required],
      }),
      locataire: new FormControl(calculRedevanceRawValue.locataire, {
        validators: [Validators.required],
      }),
    });
  }

  getCalculRedevance(form: CalculRedevanceFormGroup): ICalculRedevance | NewCalculRedevance {
    return this.convertCalculRedevanceRawValueToCalculRedevance(
      form.getRawValue() as CalculRedevanceFormRawValue | NewCalculRedevanceFormRawValue,
    );
  }

  resetForm(form: CalculRedevanceFormGroup, calculRedevance: CalculRedevanceFormGroupInput): void {
    const calculRedevanceRawValue = this.convertCalculRedevanceToCalculRedevanceRawValue({ ...this.getFormDefaults(), ...calculRedevance });
    form.reset({
      ...calculRedevanceRawValue,
      id: { value: calculRedevanceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CalculRedevanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCalcul: currentTime,
    };
  }

  private convertCalculRedevanceRawValueToCalculRedevance(
    rawCalculRedevance: CalculRedevanceFormRawValue | NewCalculRedevanceFormRawValue,
  ): ICalculRedevance | NewCalculRedevance {
    return {
      ...rawCalculRedevance,
      dateCalcul: dayjs(rawCalculRedevance.dateCalcul, DATE_TIME_FORMAT),
    };
  }

  private convertCalculRedevanceToCalculRedevanceRawValue(
    calculRedevance: ICalculRedevance | (Partial<NewCalculRedevance> & CalculRedevanceFormDefaults),
  ): CalculRedevanceFormRawValue | PartialWithRequiredKeyOf<NewCalculRedevanceFormRawValue> {
    return {
      ...calculRedevance,
      dateCalcul: calculRedevance.dateCalcul ? calculRedevance.dateCalcul.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
