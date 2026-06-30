import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPaiementVente, NewPaiementVente } from '../paiement-vente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaiementVente for edit and NewPaiementVenteFormGroupInput for create.
 */
type PaiementVenteFormGroupInput = IPaiementVente | PartialWithRequiredKeyOf<NewPaiementVente>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPaiementVente | NewPaiementVente> = Omit<T, 'datePaiement'> & {
  datePaiement?: string | null;
};

type PaiementVenteFormRawValue = FormValueOf<IPaiementVente>;

type NewPaiementVenteFormRawValue = FormValueOf<NewPaiementVente>;

type PaiementVenteFormDefaults = Pick<NewPaiementVente, 'id' | 'datePaiement'>;

type PaiementVenteFormGroupContent = {
  id: FormControl<PaiementVenteFormRawValue['id'] | NewPaiementVente['id']>;
  montant: FormControl<PaiementVenteFormRawValue['montant']>;
  statut: FormControl<PaiementVenteFormRawValue['statut']>;
  referencePaiement: FormControl<PaiementVenteFormRawValue['referencePaiement']>;
  datePaiement: FormControl<PaiementVenteFormRawValue['datePaiement']>;
  vente: FormControl<PaiementVenteFormRawValue['vente']>;
  modePaiement: FormControl<PaiementVenteFormRawValue['modePaiement']>;
};

export type PaiementVenteFormGroup = FormGroup<PaiementVenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaiementVenteFormService {
  createPaiementVenteFormGroup(paiementVente?: PaiementVenteFormGroupInput): PaiementVenteFormGroup {
    const paiementVenteRawValue = this.convertPaiementVenteToPaiementVenteRawValue({
      ...this.getFormDefaults(),
      ...(paiementVente ?? { id: null }),
    });
    return new FormGroup<PaiementVenteFormGroupContent>({
      id: new FormControl(
        { value: paiementVenteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      montant: new FormControl(paiementVenteRawValue.montant, {
        validators: [Validators.required, Validators.min(0)],
      }),
      statut: new FormControl(paiementVenteRawValue.statut, {
        validators: [Validators.required],
      }),
      referencePaiement: new FormControl(paiementVenteRawValue.referencePaiement, {
        validators: [Validators.maxLength(100)],
      }),
      datePaiement: new FormControl(paiementVenteRawValue.datePaiement, {
        validators: [Validators.required],
      }),
      vente: new FormControl(paiementVenteRawValue.vente, {
        validators: [Validators.required],
      }),
      modePaiement: new FormControl(paiementVenteRawValue.modePaiement, {
        validators: [Validators.required],
      }),
    });
  }

  getPaiementVente(form: PaiementVenteFormGroup): IPaiementVente | NewPaiementVente {
    return this.convertPaiementVenteRawValueToPaiementVente(form.getRawValue() as PaiementVenteFormRawValue | NewPaiementVenteFormRawValue);
  }

  resetForm(form: PaiementVenteFormGroup, paiementVente: PaiementVenteFormGroupInput): void {
    const paiementVenteRawValue = this.convertPaiementVenteToPaiementVenteRawValue({ ...this.getFormDefaults(), ...paiementVente });
    form.reset({
      ...paiementVenteRawValue,
      id: { value: paiementVenteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PaiementVenteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      datePaiement: currentTime,
    };
  }

  private convertPaiementVenteRawValueToPaiementVente(
    rawPaiementVente: PaiementVenteFormRawValue | NewPaiementVenteFormRawValue,
  ): IPaiementVente | NewPaiementVente {
    return {
      ...rawPaiementVente,
      datePaiement: dayjs(rawPaiementVente.datePaiement, DATE_TIME_FORMAT),
    };
  }

  private convertPaiementVenteToPaiementVenteRawValue(
    paiementVente: IPaiementVente | (Partial<NewPaiementVente> & PaiementVenteFormDefaults),
  ): PaiementVenteFormRawValue | PartialWithRequiredKeyOf<NewPaiementVenteFormRawValue> {
    return {
      ...paiementVente,
      datePaiement: paiementVente.datePaiement ? paiementVente.datePaiement.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
