import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILotEtiquettes, NewLotEtiquettes } from '../lot-etiquettes.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILotEtiquettes for edit and NewLotEtiquettesFormGroupInput for create.
 */
type LotEtiquettesFormGroupInput = ILotEtiquettes | PartialWithRequiredKeyOf<NewLotEtiquettes>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILotEtiquettes | NewLotEtiquettes> = Omit<T, 'dateGeneration'> & {
  dateGeneration?: string | null;
};

type LotEtiquettesFormRawValue = FormValueOf<ILotEtiquettes>;

type NewLotEtiquettesFormRawValue = FormValueOf<NewLotEtiquettes>;

type LotEtiquettesFormDefaults = Pick<NewLotEtiquettes, 'id' | 'dateGeneration'>;

type LotEtiquettesFormGroupContent = {
  id: FormControl<LotEtiquettesFormRawValue['id'] | NewLotEtiquettes['id']>;
  reference: FormControl<LotEtiquettesFormRawValue['reference']>;
  dateGeneration: FormControl<LotEtiquettesFormRawValue['dateGeneration']>;
  formatImpression: FormControl<LotEtiquettesFormRawValue['formatImpression']>;
  nombreEtiquettes: FormControl<LotEtiquettesFormRawValue['nombreEtiquettes']>;
};

export type LotEtiquettesFormGroup = FormGroup<LotEtiquettesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LotEtiquettesFormService {
  createLotEtiquettesFormGroup(lotEtiquettes?: LotEtiquettesFormGroupInput): LotEtiquettesFormGroup {
    const lotEtiquettesRawValue = this.convertLotEtiquettesToLotEtiquettesRawValue({
      ...this.getFormDefaults(),
      ...(lotEtiquettes ?? { id: null }),
    });
    return new FormGroup<LotEtiquettesFormGroupContent>({
      id: new FormControl(
        { value: lotEtiquettesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(lotEtiquettesRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      dateGeneration: new FormControl(lotEtiquettesRawValue.dateGeneration, {
        validators: [Validators.required],
      }),
      formatImpression: new FormControl(lotEtiquettesRawValue.formatImpression, {
        validators: [Validators.maxLength(80)],
      }),
      nombreEtiquettes: new FormControl(lotEtiquettesRawValue.nombreEtiquettes, {
        validators: [Validators.required, Validators.min(1)],
      }),
    });
  }

  getLotEtiquettes(form: LotEtiquettesFormGroup): ILotEtiquettes | NewLotEtiquettes {
    return this.convertLotEtiquettesRawValueToLotEtiquettes(form.getRawValue() as LotEtiquettesFormRawValue | NewLotEtiquettesFormRawValue);
  }

  resetForm(form: LotEtiquettesFormGroup, lotEtiquettes: LotEtiquettesFormGroupInput): void {
    const lotEtiquettesRawValue = this.convertLotEtiquettesToLotEtiquettesRawValue({ ...this.getFormDefaults(), ...lotEtiquettes });
    form.reset({
      ...lotEtiquettesRawValue,
      id: { value: lotEtiquettesRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LotEtiquettesFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateGeneration: currentTime,
    };
  }

  private convertLotEtiquettesRawValueToLotEtiquettes(
    rawLotEtiquettes: LotEtiquettesFormRawValue | NewLotEtiquettesFormRawValue,
  ): ILotEtiquettes | NewLotEtiquettes {
    return {
      ...rawLotEtiquettes,
      dateGeneration: dayjs(rawLotEtiquettes.dateGeneration, DATE_TIME_FORMAT),
    };
  }

  private convertLotEtiquettesToLotEtiquettesRawValue(
    lotEtiquettes: ILotEtiquettes | (Partial<NewLotEtiquettes> & LotEtiquettesFormDefaults),
  ): LotEtiquettesFormRawValue | PartialWithRequiredKeyOf<NewLotEtiquettesFormRawValue> {
    return {
      ...lotEtiquettes,
      dateGeneration: lotEtiquettes.dateGeneration ? lotEtiquettes.dateGeneration.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
