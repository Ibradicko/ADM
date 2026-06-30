import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMouvementStock, NewMouvementStock } from '../mouvement-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMouvementStock for edit and NewMouvementStockFormGroupInput for create.
 */
type MouvementStockFormGroupInput = IMouvementStock | PartialWithRequiredKeyOf<NewMouvementStock>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMouvementStock | NewMouvementStock> = Omit<T, 'dateMouvement'> & {
  dateMouvement?: string | null;
};

type MouvementStockFormRawValue = FormValueOf<IMouvementStock>;

type NewMouvementStockFormRawValue = FormValueOf<NewMouvementStock>;

type MouvementStockFormDefaults = Pick<NewMouvementStock, 'id' | 'dateMouvement'>;

type MouvementStockFormGroupContent = {
  id: FormControl<MouvementStockFormRawValue['id'] | NewMouvementStock['id']>;
  reference: FormControl<MouvementStockFormRawValue['reference']>;
  typeMouvement: FormControl<MouvementStockFormRawValue['typeMouvement']>;
  statut: FormControl<MouvementStockFormRawValue['statut']>;
  dateMouvement: FormControl<MouvementStockFormRawValue['dateMouvement']>;
  motif: FormControl<MouvementStockFormRawValue['motif']>;
  boutique: FormControl<MouvementStockFormRawValue['boutique']>;
  utilisateur: FormControl<MouvementStockFormRawValue['utilisateur']>;
};

export type MouvementStockFormGroup = FormGroup<MouvementStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MouvementStockFormService {
  createMouvementStockFormGroup(mouvementStock?: MouvementStockFormGroupInput): MouvementStockFormGroup {
    const mouvementStockRawValue = this.convertMouvementStockToMouvementStockRawValue({
      ...this.getFormDefaults(),
      ...(mouvementStock ?? { id: null }),
    });
    return new FormGroup<MouvementStockFormGroupContent>({
      id: new FormControl(
        { value: mouvementStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(mouvementStockRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      typeMouvement: new FormControl(mouvementStockRawValue.typeMouvement, {
        validators: [Validators.required],
      }),
      statut: new FormControl(mouvementStockRawValue.statut, {
        validators: [Validators.required],
      }),
      dateMouvement: new FormControl(mouvementStockRawValue.dateMouvement, {
        validators: [Validators.required],
      }),
      motif: new FormControl(mouvementStockRawValue.motif, {
        validators: [Validators.maxLength(255)],
      }),
      boutique: new FormControl(mouvementStockRawValue.boutique, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(mouvementStockRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getMouvementStock(form: MouvementStockFormGroup): IMouvementStock | NewMouvementStock {
    return this.convertMouvementStockRawValueToMouvementStock(
      form.getRawValue() as MouvementStockFormRawValue | NewMouvementStockFormRawValue,
    );
  }

  resetForm(form: MouvementStockFormGroup, mouvementStock: MouvementStockFormGroupInput): void {
    const mouvementStockRawValue = this.convertMouvementStockToMouvementStockRawValue({ ...this.getFormDefaults(), ...mouvementStock });
    form.reset({
      ...mouvementStockRawValue,
      id: { value: mouvementStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MouvementStockFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateMouvement: currentTime,
    };
  }

  private convertMouvementStockRawValueToMouvementStock(
    rawMouvementStock: MouvementStockFormRawValue | NewMouvementStockFormRawValue,
  ): IMouvementStock | NewMouvementStock {
    return {
      ...rawMouvementStock,
      dateMouvement: dayjs(rawMouvementStock.dateMouvement, DATE_TIME_FORMAT),
    };
  }

  private convertMouvementStockToMouvementStockRawValue(
    mouvementStock: IMouvementStock | (Partial<NewMouvementStock> & MouvementStockFormDefaults),
  ): MouvementStockFormRawValue | PartialWithRequiredKeyOf<NewMouvementStockFormRawValue> {
    return {
      ...mouvementStock,
      dateMouvement: mouvementStock.dateMouvement ? mouvementStock.dateMouvement.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
