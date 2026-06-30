import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRegularisationRedevance, NewRegularisationRedevance } from '../regularisation-redevance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRegularisationRedevance for edit and NewRegularisationRedevanceFormGroupInput for create.
 */
type RegularisationRedevanceFormGroupInput = IRegularisationRedevance | PartialWithRequiredKeyOf<NewRegularisationRedevance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRegularisationRedevance | NewRegularisationRedevance> = Omit<T, 'dateRegularisation'> & {
  dateRegularisation?: string | null;
};

type RegularisationRedevanceFormRawValue = FormValueOf<IRegularisationRedevance>;

type NewRegularisationRedevanceFormRawValue = FormValueOf<NewRegularisationRedevance>;

type RegularisationRedevanceFormDefaults = Pick<NewRegularisationRedevance, 'id' | 'dateRegularisation'>;

type RegularisationRedevanceFormGroupContent = {
  id: FormControl<RegularisationRedevanceFormRawValue['id'] | NewRegularisationRedevance['id']>;
  reference: FormControl<RegularisationRedevanceFormRawValue['reference']>;
  montant: FormControl<RegularisationRedevanceFormRawValue['montant']>;
  motif: FormControl<RegularisationRedevanceFormRawValue['motif']>;
  dateRegularisation: FormControl<RegularisationRedevanceFormRawValue['dateRegularisation']>;
  calcul: FormControl<RegularisationRedevanceFormRawValue['calcul']>;
};

export type RegularisationRedevanceFormGroup = FormGroup<RegularisationRedevanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RegularisationRedevanceFormService {
  createRegularisationRedevanceFormGroup(
    regularisationRedevance?: RegularisationRedevanceFormGroupInput,
  ): RegularisationRedevanceFormGroup {
    const regularisationRedevanceRawValue = this.convertRegularisationRedevanceToRegularisationRedevanceRawValue({
      ...this.getFormDefaults(),
      ...(regularisationRedevance ?? { id: null }),
    });
    return new FormGroup<RegularisationRedevanceFormGroupContent>({
      id: new FormControl(
        { value: regularisationRedevanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(regularisationRedevanceRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      montant: new FormControl(regularisationRedevanceRawValue.montant, {
        validators: [Validators.required],
      }),
      motif: new FormControl(regularisationRedevanceRawValue.motif, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      dateRegularisation: new FormControl(regularisationRedevanceRawValue.dateRegularisation, {
        validators: [Validators.required],
      }),
      calcul: new FormControl(regularisationRedevanceRawValue.calcul, {
        validators: [Validators.required],
      }),
    });
  }

  getRegularisationRedevance(form: RegularisationRedevanceFormGroup): IRegularisationRedevance | NewRegularisationRedevance {
    return this.convertRegularisationRedevanceRawValueToRegularisationRedevance(
      form.getRawValue() as RegularisationRedevanceFormRawValue | NewRegularisationRedevanceFormRawValue,
    );
  }

  resetForm(form: RegularisationRedevanceFormGroup, regularisationRedevance: RegularisationRedevanceFormGroupInput): void {
    const regularisationRedevanceRawValue = this.convertRegularisationRedevanceToRegularisationRedevanceRawValue({
      ...this.getFormDefaults(),
      ...regularisationRedevance,
    });
    form.reset({
      ...regularisationRedevanceRawValue,
      id: { value: regularisationRedevanceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RegularisationRedevanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateRegularisation: currentTime,
    };
  }

  private convertRegularisationRedevanceRawValueToRegularisationRedevance(
    rawRegularisationRedevance: RegularisationRedevanceFormRawValue | NewRegularisationRedevanceFormRawValue,
  ): IRegularisationRedevance | NewRegularisationRedevance {
    return {
      ...rawRegularisationRedevance,
      dateRegularisation: dayjs(rawRegularisationRedevance.dateRegularisation, DATE_TIME_FORMAT),
    };
  }

  private convertRegularisationRedevanceToRegularisationRedevanceRawValue(
    regularisationRedevance: IRegularisationRedevance | (Partial<NewRegularisationRedevance> & RegularisationRedevanceFormDefaults),
  ): RegularisationRedevanceFormRawValue | PartialWithRequiredKeyOf<NewRegularisationRedevanceFormRawValue> {
    return {
      ...regularisationRedevance,
      dateRegularisation: regularisationRedevance.dateRegularisation
        ? regularisationRedevance.dateRegularisation.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
