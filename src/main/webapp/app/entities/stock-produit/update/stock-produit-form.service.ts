import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStockProduit, NewStockProduit } from '../stock-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStockProduit for edit and NewStockProduitFormGroupInput for create.
 */
type StockProduitFormGroupInput = IStockProduit | PartialWithRequiredKeyOf<NewStockProduit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStockProduit | NewStockProduit> = Omit<T, 'dateDernierMouvement'> & {
  dateDernierMouvement?: string | null;
};

type StockProduitFormRawValue = FormValueOf<IStockProduit>;

type NewStockProduitFormRawValue = FormValueOf<NewStockProduit>;

type StockProduitFormDefaults = Pick<NewStockProduit, 'id' | 'dateDernierMouvement'>;

type StockProduitFormGroupContent = {
  id: FormControl<StockProduitFormRawValue['id'] | NewStockProduit['id']>;
  quantiteTheorique: FormControl<StockProduitFormRawValue['quantiteTheorique']>;
  stockAlerte: FormControl<StockProduitFormRawValue['stockAlerte']>;
  dateDernierMouvement: FormControl<StockProduitFormRawValue['dateDernierMouvement']>;
  produit: FormControl<StockProduitFormRawValue['produit']>;
  depot: FormControl<StockProduitFormRawValue['depot']>;
};

export type StockProduitFormGroup = FormGroup<StockProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StockProduitFormService {
  createStockProduitFormGroup(stockProduit?: StockProduitFormGroupInput): StockProduitFormGroup {
    const stockProduitRawValue = this.convertStockProduitToStockProduitRawValue({
      ...this.getFormDefaults(),
      ...(stockProduit ?? { id: null }),
    });
    return new FormGroup<StockProduitFormGroupContent>({
      id: new FormControl(
        { value: stockProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantiteTheorique: new FormControl(stockProduitRawValue.quantiteTheorique, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockAlerte: new FormControl(stockProduitRawValue.stockAlerte, {
        validators: [Validators.min(0)],
      }),
      dateDernierMouvement: new FormControl(stockProduitRawValue.dateDernierMouvement),
      produit: new FormControl(stockProduitRawValue.produit, {
        validators: [Validators.required],
      }),
      depot: new FormControl(stockProduitRawValue.depot, {
        validators: [Validators.required],
      }),
    });
  }

  getStockProduit(form: StockProduitFormGroup): IStockProduit | NewStockProduit {
    return this.convertStockProduitRawValueToStockProduit(form.getRawValue() as StockProduitFormRawValue | NewStockProduitFormRawValue);
  }

  resetForm(form: StockProduitFormGroup, stockProduit: StockProduitFormGroupInput): void {
    const stockProduitRawValue = this.convertStockProduitToStockProduitRawValue({ ...this.getFormDefaults(), ...stockProduit });
    form.reset({
      ...stockProduitRawValue,
      id: { value: stockProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): StockProduitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateDernierMouvement: currentTime,
    };
  }

  private convertStockProduitRawValueToStockProduit(
    rawStockProduit: StockProduitFormRawValue | NewStockProduitFormRawValue,
  ): IStockProduit | NewStockProduit {
    return {
      ...rawStockProduit,
      dateDernierMouvement: dayjs(rawStockProduit.dateDernierMouvement, DATE_TIME_FORMAT),
    };
  }

  private convertStockProduitToStockProduitRawValue(
    stockProduit: IStockProduit | (Partial<NewStockProduit> & StockProduitFormDefaults),
  ): StockProduitFormRawValue | PartialWithRequiredKeyOf<NewStockProduitFormRawValue> {
    return {
      ...stockProduit,
      dateDernierMouvement: stockProduit.dateDernierMouvement ? stockProduit.dateDernierMouvement.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
