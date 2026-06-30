import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IHistoriqueCodeBarres, NewHistoriqueCodeBarres } from '../historique-code-barres.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHistoriqueCodeBarres for edit and NewHistoriqueCodeBarresFormGroupInput for create.
 */
type HistoriqueCodeBarresFormGroupInput = IHistoriqueCodeBarres | PartialWithRequiredKeyOf<NewHistoriqueCodeBarres>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IHistoriqueCodeBarres | NewHistoriqueCodeBarres> = Omit<T, 'dateChangement'> & {
  dateChangement?: string | null;
};

type HistoriqueCodeBarresFormRawValue = FormValueOf<IHistoriqueCodeBarres>;

type NewHistoriqueCodeBarresFormRawValue = FormValueOf<NewHistoriqueCodeBarres>;

type HistoriqueCodeBarresFormDefaults = Pick<NewHistoriqueCodeBarres, 'id' | 'dateChangement'>;

type HistoriqueCodeBarresFormGroupContent = {
  id: FormControl<HistoriqueCodeBarresFormRawValue['id'] | NewHistoriqueCodeBarres['id']>;
  ancienCode: FormControl<HistoriqueCodeBarresFormRawValue['ancienCode']>;
  nouveauCode: FormControl<HistoriqueCodeBarresFormRawValue['nouveauCode']>;
  motif: FormControl<HistoriqueCodeBarresFormRawValue['motif']>;
  dateChangement: FormControl<HistoriqueCodeBarresFormRawValue['dateChangement']>;
  produit: FormControl<HistoriqueCodeBarresFormRawValue['produit']>;
  utilisateur: FormControl<HistoriqueCodeBarresFormRawValue['utilisateur']>;
};

export type HistoriqueCodeBarresFormGroup = FormGroup<HistoriqueCodeBarresFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HistoriqueCodeBarresFormService {
  createHistoriqueCodeBarresFormGroup(historiqueCodeBarres?: HistoriqueCodeBarresFormGroupInput): HistoriqueCodeBarresFormGroup {
    const historiqueCodeBarresRawValue = this.convertHistoriqueCodeBarresToHistoriqueCodeBarresRawValue({
      ...this.getFormDefaults(),
      ...(historiqueCodeBarres ?? { id: null }),
    });
    return new FormGroup<HistoriqueCodeBarresFormGroupContent>({
      id: new FormControl(
        { value: historiqueCodeBarresRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      ancienCode: new FormControl(historiqueCodeBarresRawValue.ancienCode, {
        validators: [Validators.maxLength(80)],
      }),
      nouveauCode: new FormControl(historiqueCodeBarresRawValue.nouveauCode, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      motif: new FormControl(historiqueCodeBarresRawValue.motif, {
        validators: [Validators.maxLength(255)],
      }),
      dateChangement: new FormControl(historiqueCodeBarresRawValue.dateChangement, {
        validators: [Validators.required],
      }),
      produit: new FormControl(historiqueCodeBarresRawValue.produit, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(historiqueCodeBarresRawValue.utilisateur),
    });
  }

  getHistoriqueCodeBarres(form: HistoriqueCodeBarresFormGroup): IHistoriqueCodeBarres | NewHistoriqueCodeBarres {
    return this.convertHistoriqueCodeBarresRawValueToHistoriqueCodeBarres(
      form.getRawValue() as HistoriqueCodeBarresFormRawValue | NewHistoriqueCodeBarresFormRawValue,
    );
  }

  resetForm(form: HistoriqueCodeBarresFormGroup, historiqueCodeBarres: HistoriqueCodeBarresFormGroupInput): void {
    const historiqueCodeBarresRawValue = this.convertHistoriqueCodeBarresToHistoriqueCodeBarresRawValue({
      ...this.getFormDefaults(),
      ...historiqueCodeBarres,
    });
    form.reset({
      ...historiqueCodeBarresRawValue,
      id: { value: historiqueCodeBarresRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): HistoriqueCodeBarresFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateChangement: currentTime,
    };
  }

  private convertHistoriqueCodeBarresRawValueToHistoriqueCodeBarres(
    rawHistoriqueCodeBarres: HistoriqueCodeBarresFormRawValue | NewHistoriqueCodeBarresFormRawValue,
  ): IHistoriqueCodeBarres | NewHistoriqueCodeBarres {
    return {
      ...rawHistoriqueCodeBarres,
      dateChangement: dayjs(rawHistoriqueCodeBarres.dateChangement, DATE_TIME_FORMAT),
    };
  }

  private convertHistoriqueCodeBarresToHistoriqueCodeBarresRawValue(
    historiqueCodeBarres: IHistoriqueCodeBarres | (Partial<NewHistoriqueCodeBarres> & HistoriqueCodeBarresFormDefaults),
  ): HistoriqueCodeBarresFormRawValue | PartialWithRequiredKeyOf<NewHistoriqueCodeBarresFormRawValue> {
    return {
      ...historiqueCodeBarres,
      dateChangement: historiqueCodeBarres.dateChangement ? historiqueCodeBarres.dateChangement.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
