import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReceptionProduit, NewReceptionProduit } from '../reception-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReceptionProduit for edit and NewReceptionProduitFormGroupInput for create.
 */
type ReceptionProduitFormGroupInput = IReceptionProduit | PartialWithRequiredKeyOf<NewReceptionProduit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReceptionProduit | NewReceptionProduit> = Omit<T, 'dateReception'> & {
  dateReception?: string | null;
};

type ReceptionProduitFormRawValue = FormValueOf<IReceptionProduit>;

type NewReceptionProduitFormRawValue = FormValueOf<NewReceptionProduit>;

type ReceptionProduitFormDefaults = Pick<NewReceptionProduit, 'id' | 'dateReception'>;

type ReceptionProduitFormGroupContent = {
  id: FormControl<ReceptionProduitFormRawValue['id'] | NewReceptionProduit['id']>;
  reference: FormControl<ReceptionProduitFormRawValue['reference']>;
  dateReception: FormControl<ReceptionProduitFormRawValue['dateReception']>;
  fournisseur: FormControl<ReceptionProduitFormRawValue['fournisseur']>;
  commentaire: FormControl<ReceptionProduitFormRawValue['commentaire']>;
  boutique: FormControl<ReceptionProduitFormRawValue['boutique']>;
  utilisateur: FormControl<ReceptionProduitFormRawValue['utilisateur']>;
};

export type ReceptionProduitFormGroup = FormGroup<ReceptionProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReceptionProduitFormService {
  createReceptionProduitFormGroup(receptionProduit?: ReceptionProduitFormGroupInput): ReceptionProduitFormGroup {
    const receptionProduitRawValue = this.convertReceptionProduitToReceptionProduitRawValue({
      ...this.getFormDefaults(),
      ...(receptionProduit ?? { id: null }),
    });
    return new FormGroup<ReceptionProduitFormGroupContent>({
      id: new FormControl(
        { value: receptionProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(receptionProduitRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      dateReception: new FormControl(receptionProduitRawValue.dateReception, {
        validators: [Validators.required],
      }),
      fournisseur: new FormControl(receptionProduitRawValue.fournisseur, {
        validators: [Validators.maxLength(150)],
      }),
      commentaire: new FormControl(receptionProduitRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      boutique: new FormControl(receptionProduitRawValue.boutique, {
        validators: [Validators.required],
      }),
      utilisateur: new FormControl(receptionProduitRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getReceptionProduit(form: ReceptionProduitFormGroup): IReceptionProduit | NewReceptionProduit {
    return this.convertReceptionProduitRawValueToReceptionProduit(
      form.getRawValue() as ReceptionProduitFormRawValue | NewReceptionProduitFormRawValue,
    );
  }

  resetForm(form: ReceptionProduitFormGroup, receptionProduit: ReceptionProduitFormGroupInput): void {
    const receptionProduitRawValue = this.convertReceptionProduitToReceptionProduitRawValue({
      ...this.getFormDefaults(),
      ...receptionProduit,
    });
    form.reset({
      ...receptionProduitRawValue,
      id: { value: receptionProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ReceptionProduitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateReception: currentTime,
    };
  }

  private convertReceptionProduitRawValueToReceptionProduit(
    rawReceptionProduit: ReceptionProduitFormRawValue | NewReceptionProduitFormRawValue,
  ): IReceptionProduit | NewReceptionProduit {
    return {
      ...rawReceptionProduit,
      dateReception: dayjs(rawReceptionProduit.dateReception, DATE_TIME_FORMAT),
    };
  }

  private convertReceptionProduitToReceptionProduitRawValue(
    receptionProduit: IReceptionProduit | (Partial<NewReceptionProduit> & ReceptionProduitFormDefaults),
  ): ReceptionProduitFormRawValue | PartialWithRequiredKeyOf<NewReceptionProduitFormRawValue> {
    return {
      ...receptionProduit,
      dateReception: receptionProduit.dateReception ? receptionProduit.dateReception.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
