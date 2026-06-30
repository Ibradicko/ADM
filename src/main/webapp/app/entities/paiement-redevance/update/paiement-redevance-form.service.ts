import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPaiementRedevance, NewPaiementRedevance } from '../paiement-redevance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaiementRedevance for edit and NewPaiementRedevanceFormGroupInput for create.
 */
type PaiementRedevanceFormGroupInput = IPaiementRedevance | PartialWithRequiredKeyOf<NewPaiementRedevance>;

type PaiementRedevanceFormDefaults = Pick<NewPaiementRedevance, 'id'>;

type PaiementRedevanceFormGroupContent = {
  id: FormControl<IPaiementRedevance['id'] | NewPaiementRedevance['id']>;
  reference: FormControl<IPaiementRedevance['reference']>;
  montant: FormControl<IPaiementRedevance['montant']>;
  datePaiement: FormControl<IPaiementRedevance['datePaiement']>;
  modePaiement: FormControl<IPaiementRedevance['modePaiement']>;
  commentaire: FormControl<IPaiementRedevance['commentaire']>;
  calcul: FormControl<IPaiementRedevance['calcul']>;
};

export type PaiementRedevanceFormGroup = FormGroup<PaiementRedevanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaiementRedevanceFormService {
  createPaiementRedevanceFormGroup(paiementRedevance?: PaiementRedevanceFormGroupInput): PaiementRedevanceFormGroup {
    const paiementRedevanceRawValue = {
      ...this.getFormDefaults(),
      ...(paiementRedevance ?? { id: null }),
    };
    return new FormGroup<PaiementRedevanceFormGroupContent>({
      id: new FormControl(
        { value: paiementRedevanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(paiementRedevanceRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      montant: new FormControl(paiementRedevanceRawValue.montant, {
        validators: [Validators.required, Validators.min(0)],
      }),
      datePaiement: new FormControl(paiementRedevanceRawValue.datePaiement, {
        validators: [Validators.required],
      }),
      modePaiement: new FormControl(paiementRedevanceRawValue.modePaiement, {
        validators: [Validators.maxLength(80)],
      }),
      commentaire: new FormControl(paiementRedevanceRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      calcul: new FormControl(paiementRedevanceRawValue.calcul, {
        validators: [Validators.required],
      }),
    });
  }

  getPaiementRedevance(form: PaiementRedevanceFormGroup): IPaiementRedevance | NewPaiementRedevance {
    return form.getRawValue() as IPaiementRedevance | NewPaiementRedevance;
  }

  resetForm(form: PaiementRedevanceFormGroup, paiementRedevance: PaiementRedevanceFormGroupInput): void {
    const paiementRedevanceRawValue = { ...this.getFormDefaults(), ...paiementRedevance };
    form.reset({
      ...paiementRedevanceRawValue,
      id: { value: paiementRedevanceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PaiementRedevanceFormDefaults {
    return {
      id: null,
    };
  }
}
