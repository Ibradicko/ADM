import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITransfertStock, NewTransfertStock } from '../transfert-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransfertStock for edit and NewTransfertStockFormGroupInput for create.
 */
type TransfertStockFormGroupInput = ITransfertStock | PartialWithRequiredKeyOf<NewTransfertStock>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITransfertStock | NewTransfertStock> = Omit<T, 'dateTransfert'> & {
  dateTransfert?: string | null;
};

type TransfertStockFormRawValue = FormValueOf<ITransfertStock>;

type NewTransfertStockFormRawValue = FormValueOf<NewTransfertStock>;

type TransfertStockFormDefaults = Pick<NewTransfertStock, 'id' | 'dateTransfert'>;

type TransfertStockFormGroupContent = {
  id: FormControl<TransfertStockFormRawValue['id'] | NewTransfertStock['id']>;
  reference: FormControl<TransfertStockFormRawValue['reference']>;
  dateTransfert: FormControl<TransfertStockFormRawValue['dateTransfert']>;
  statut: FormControl<TransfertStockFormRawValue['statut']>;
  motif: FormControl<TransfertStockFormRawValue['motif']>;
  boutiqueOrigine: FormControl<TransfertStockFormRawValue['boutiqueOrigine']>;
  boutiqueDestination: FormControl<TransfertStockFormRawValue['boutiqueDestination']>;
  utilisateur: FormControl<TransfertStockFormRawValue['utilisateur']>;
};

export type TransfertStockFormGroup = FormGroup<TransfertStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransfertStockFormService {
  createTransfertStockFormGroup(transfertStock?: TransfertStockFormGroupInput): TransfertStockFormGroup {
    const transfertStockRawValue = this.convertTransfertStockToTransfertStockRawValue({
      ...this.getFormDefaults(),
      ...(transfertStock ?? { id: null }),
    });
    return new FormGroup<TransfertStockFormGroupContent>({
      id: new FormControl(
        { value: transfertStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(transfertStockRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      dateTransfert: new FormControl(transfertStockRawValue.dateTransfert, {
        validators: [Validators.required],
      }),
      statut: new FormControl(transfertStockRawValue.statut, {
        validators: [Validators.required],
      }),
      motif: new FormControl(transfertStockRawValue.motif, {
        validators: [Validators.maxLength(255)],
      }),
      boutiqueOrigine: new FormControl(transfertStockRawValue.boutiqueOrigine, {
        validators: [Validators.required],
      }),
      boutiqueDestination: new FormControl(transfertStockRawValue.boutiqueDestination, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(transfertStockRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getTransfertStock(form: TransfertStockFormGroup): ITransfertStock | NewTransfertStock {
    return this.convertTransfertStockRawValueToTransfertStock(
      form.getRawValue() as TransfertStockFormRawValue | NewTransfertStockFormRawValue,
    );
  }

  resetForm(form: TransfertStockFormGroup, transfertStock: TransfertStockFormGroupInput): void {
    const transfertStockRawValue = this.convertTransfertStockToTransfertStockRawValue({ ...this.getFormDefaults(), ...transfertStock });
    form.reset({
      ...transfertStockRawValue,
      id: { value: transfertStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TransfertStockFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateTransfert: currentTime,
    };
  }

  private convertTransfertStockRawValueToTransfertStock(
    rawTransfertStock: TransfertStockFormRawValue | NewTransfertStockFormRawValue,
  ): ITransfertStock | NewTransfertStock {
    return {
      ...rawTransfertStock,
      dateTransfert: dayjs(rawTransfertStock.dateTransfert, DATE_TIME_FORMAT),
    };
  }

  private convertTransfertStockToTransfertStockRawValue(
    transfertStock: ITransfertStock | (Partial<NewTransfertStock> & TransfertStockFormDefaults),
  ): TransfertStockFormRawValue | PartialWithRequiredKeyOf<NewTransfertStockFormRawValue> {
    return {
      ...transfertStock,
      dateTransfert: transfertStock.dateTransfert ? transfertStock.dateTransfert.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
