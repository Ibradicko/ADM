import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInventaireStock, NewInventaireStock } from '../inventaire-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInventaireStock for edit and NewInventaireStockFormGroupInput for create.
 */
type InventaireStockFormGroupInput = IInventaireStock | PartialWithRequiredKeyOf<NewInventaireStock>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInventaireStock | NewInventaireStock> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

type InventaireStockFormRawValue = FormValueOf<IInventaireStock>;

type NewInventaireStockFormRawValue = FormValueOf<NewInventaireStock>;

type InventaireStockFormDefaults = Pick<NewInventaireStock, 'id' | 'dateDebut' | 'dateFin'>;

type InventaireStockFormGroupContent = {
  id: FormControl<InventaireStockFormRawValue['id'] | NewInventaireStock['id']>;
  reference: FormControl<InventaireStockFormRawValue['reference']>;
  typeInventaire: FormControl<InventaireStockFormRawValue['typeInventaire']>;
  statut: FormControl<InventaireStockFormRawValue['statut']>;
  dateDebut: FormControl<InventaireStockFormRawValue['dateDebut']>;
  dateFin: FormControl<InventaireStockFormRawValue['dateFin']>;
  boutique: FormControl<InventaireStockFormRawValue['boutique']>;
  depot: FormControl<InventaireStockFormRawValue['depot']>;
  utilisateur: FormControl<InventaireStockFormRawValue['utilisateur']>;
};

export type InventaireStockFormGroup = FormGroup<InventaireStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InventaireStockFormService {
  createInventaireStockFormGroup(inventaireStock?: InventaireStockFormGroupInput): InventaireStockFormGroup {
    const inventaireStockRawValue = this.convertInventaireStockToInventaireStockRawValue({
      ...this.getFormDefaults(),
      ...(inventaireStock ?? { id: null }),
    });
    return new FormGroup<InventaireStockFormGroupContent>({
      id: new FormControl(
        { value: inventaireStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(inventaireStockRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      typeInventaire: new FormControl(inventaireStockRawValue.typeInventaire, {
        validators: [Validators.required],
      }),
      statut: new FormControl(inventaireStockRawValue.statut, {
        validators: [Validators.required],
      }),
      dateDebut: new FormControl(inventaireStockRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFin: new FormControl(inventaireStockRawValue.dateFin),
      boutique: new FormControl(inventaireStockRawValue.boutique, {
        validators: [Validators.required],
      }),
      depot: new FormControl(inventaireStockRawValue.depot),
      utilisateur: new FormControl(inventaireStockRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getInventaireStock(form: InventaireStockFormGroup): IInventaireStock | NewInventaireStock {
    return this.convertInventaireStockRawValueToInventaireStock(
      form.getRawValue() as InventaireStockFormRawValue | NewInventaireStockFormRawValue,
    );
  }

  resetForm(form: InventaireStockFormGroup, inventaireStock: InventaireStockFormGroupInput): void {
    const inventaireStockRawValue = this.convertInventaireStockToInventaireStockRawValue({ ...this.getFormDefaults(), ...inventaireStock });
    form.reset({
      ...inventaireStockRawValue,
      id: { value: inventaireStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): InventaireStockFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateDebut: currentTime,
      dateFin: currentTime,
    };
  }

  private convertInventaireStockRawValueToInventaireStock(
    rawInventaireStock: InventaireStockFormRawValue | NewInventaireStockFormRawValue,
  ): IInventaireStock | NewInventaireStock {
    return {
      ...rawInventaireStock,
      dateDebut: dayjs(rawInventaireStock.dateDebut, DATE_TIME_FORMAT),
      dateFin: dayjs(rawInventaireStock.dateFin, DATE_TIME_FORMAT),
    };
  }

  private convertInventaireStockToInventaireStockRawValue(
    inventaireStock: IInventaireStock | (Partial<NewInventaireStock> & InventaireStockFormDefaults),
  ): InventaireStockFormRawValue | PartialWithRequiredKeyOf<NewInventaireStockFormRawValue> {
    return {
      ...inventaireStock,
      dateDebut: inventaireStock.dateDebut ? inventaireStock.dateDebut.format(DATE_TIME_FORMAT) : undefined,
      dateFin: inventaireStock.dateFin ? inventaireStock.dateFin.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
