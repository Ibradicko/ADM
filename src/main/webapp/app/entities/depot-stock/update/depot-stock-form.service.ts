import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDepotStock, NewDepotStock } from '../depot-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDepotStock for edit and NewDepotStockFormGroupInput for create.
 */
type DepotStockFormGroupInput = IDepotStock | PartialWithRequiredKeyOf<NewDepotStock>;

type DepotStockFormDefaults = Pick<NewDepotStock, 'id' | 'actif'>;

type DepotStockFormGroupContent = {
  id: FormControl<IDepotStock['id'] | NewDepotStock['id']>;
  code: FormControl<IDepotStock['code']>;
  libelle: FormControl<IDepotStock['libelle']>;
  emplacement: FormControl<IDepotStock['emplacement']>;
  actif: FormControl<IDepotStock['actif']>;
  boutique: FormControl<IDepotStock['boutique']>;
};

export type DepotStockFormGroup = FormGroup<DepotStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DepotStockFormService {
  createDepotStockFormGroup(depotStock?: DepotStockFormGroupInput): DepotStockFormGroup {
    const depotStockRawValue = {
      ...this.getFormDefaults(),
      ...(depotStock ?? { id: null }),
    };
    return new FormGroup<DepotStockFormGroupContent>({
      id: new FormControl(
        { value: depotStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(depotStockRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      libelle: new FormControl(depotStockRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      emplacement: new FormControl(depotStockRawValue.emplacement, {
        validators: [Validators.maxLength(255)],
      }),
      actif: new FormControl(depotStockRawValue.actif, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(depotStockRawValue.boutique, {
        validators: [Validators.required],
      }),
    });
  }

  getDepotStock(form: DepotStockFormGroup): IDepotStock | NewDepotStock {
    return form.getRawValue() as IDepotStock | NewDepotStock;
  }

  resetForm(form: DepotStockFormGroup, depotStock: DepotStockFormGroupInput): void {
    const depotStockRawValue = { ...this.getFormDefaults(), ...depotStock };
    form.reset({
      ...depotStockRawValue,
      id: { value: depotStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DepotStockFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
