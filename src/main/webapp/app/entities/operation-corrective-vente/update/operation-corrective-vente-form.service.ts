import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOperationCorrectiveVente, NewOperationCorrectiveVente } from '../operation-corrective-vente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOperationCorrectiveVente for edit and NewOperationCorrectiveVenteFormGroupInput for create.
 */
type OperationCorrectiveVenteFormGroupInput = IOperationCorrectiveVente | PartialWithRequiredKeyOf<NewOperationCorrectiveVente>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOperationCorrectiveVente | NewOperationCorrectiveVente> = Omit<T, 'dateOperation'> & {
  dateOperation?: string | null;
};

type OperationCorrectiveVenteFormRawValue = FormValueOf<IOperationCorrectiveVente>;

type NewOperationCorrectiveVenteFormRawValue = FormValueOf<NewOperationCorrectiveVente>;

type OperationCorrectiveVenteFormDefaults = Pick<NewOperationCorrectiveVente, 'id' | 'dateOperation'>;

type OperationCorrectiveVenteFormGroupContent = {
  id: FormControl<OperationCorrectiveVenteFormRawValue['id'] | NewOperationCorrectiveVente['id']>;
  typeOperation: FormControl<OperationCorrectiveVenteFormRawValue['typeOperation']>;
  motif: FormControl<OperationCorrectiveVenteFormRawValue['motif']>;
  montantImpact: FormControl<OperationCorrectiveVenteFormRawValue['montantImpact']>;
  dateOperation: FormControl<OperationCorrectiveVenteFormRawValue['dateOperation']>;
  vente: FormControl<OperationCorrectiveVenteFormRawValue['vente']>;
  utilisateur: FormControl<OperationCorrectiveVenteFormRawValue['utilisateur']>;
};

export type OperationCorrectiveVenteFormGroup = FormGroup<OperationCorrectiveVenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OperationCorrectiveVenteFormService {
  createOperationCorrectiveVenteFormGroup(
    operationCorrectiveVente?: OperationCorrectiveVenteFormGroupInput,
  ): OperationCorrectiveVenteFormGroup {
    const operationCorrectiveVenteRawValue = this.convertOperationCorrectiveVenteToOperationCorrectiveVenteRawValue({
      ...this.getFormDefaults(),
      ...(operationCorrectiveVente ?? { id: null }),
    });
    return new FormGroup<OperationCorrectiveVenteFormGroupContent>({
      id: new FormControl(
        { value: operationCorrectiveVenteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      typeOperation: new FormControl(operationCorrectiveVenteRawValue.typeOperation, {
        validators: [Validators.required],
      }),
      motif: new FormControl(operationCorrectiveVenteRawValue.motif, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      montantImpact: new FormControl(operationCorrectiveVenteRawValue.montantImpact, {
        validators: [Validators.min(0)],
      }),
      dateOperation: new FormControl(operationCorrectiveVenteRawValue.dateOperation, {
        validators: [Validators.required],
      }),
      vente: new FormControl(operationCorrectiveVenteRawValue.vente, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(operationCorrectiveVenteRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getOperationCorrectiveVente(form: OperationCorrectiveVenteFormGroup): IOperationCorrectiveVente | NewOperationCorrectiveVente {
    return this.convertOperationCorrectiveVenteRawValueToOperationCorrectiveVente(
      form.getRawValue() as OperationCorrectiveVenteFormRawValue | NewOperationCorrectiveVenteFormRawValue,
    );
  }

  resetForm(form: OperationCorrectiveVenteFormGroup, operationCorrectiveVente: OperationCorrectiveVenteFormGroupInput): void {
    const operationCorrectiveVenteRawValue = this.convertOperationCorrectiveVenteToOperationCorrectiveVenteRawValue({
      ...this.getFormDefaults(),
      ...operationCorrectiveVente,
    });
    form.reset({
      ...operationCorrectiveVenteRawValue,
      id: { value: operationCorrectiveVenteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): OperationCorrectiveVenteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateOperation: currentTime,
    };
  }

  private convertOperationCorrectiveVenteRawValueToOperationCorrectiveVente(
    rawOperationCorrectiveVente: OperationCorrectiveVenteFormRawValue | NewOperationCorrectiveVenteFormRawValue,
  ): IOperationCorrectiveVente | NewOperationCorrectiveVente {
    return {
      ...rawOperationCorrectiveVente,
      dateOperation: dayjs(rawOperationCorrectiveVente.dateOperation, DATE_TIME_FORMAT),
    };
  }

  private convertOperationCorrectiveVenteToOperationCorrectiveVenteRawValue(
    operationCorrectiveVente: IOperationCorrectiveVente | (Partial<NewOperationCorrectiveVente> & OperationCorrectiveVenteFormDefaults),
  ): OperationCorrectiveVenteFormRawValue | PartialWithRequiredKeyOf<NewOperationCorrectiveVenteFormRawValue> {
    return {
      ...operationCorrectiveVente,
      dateOperation: operationCorrectiveVente.dateOperation ? operationCorrectiveVente.dateOperation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
