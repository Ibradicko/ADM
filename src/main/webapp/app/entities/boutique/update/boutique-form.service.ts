import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBoutique, NewBoutique } from '../boutique.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBoutique for edit and NewBoutiqueFormGroupInput for create.
 */
type BoutiqueFormGroupInput = IBoutique | PartialWithRequiredKeyOf<NewBoutique>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBoutique | NewBoutique> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

type BoutiqueFormRawValue = FormValueOf<IBoutique>;

type NewBoutiqueFormRawValue = FormValueOf<NewBoutique>;

type BoutiqueFormDefaults = Pick<NewBoutique, 'id' | 'dateCreation'>;

type BoutiqueFormGroupContent = {
  id: FormControl<BoutiqueFormRawValue['id'] | NewBoutique['id']>;
  code: FormControl<BoutiqueFormRawValue['code']>;
  nom: FormControl<BoutiqueFormRawValue['nom']>;
  type: FormControl<BoutiqueFormRawValue['type']>;
  emplacement: FormControl<BoutiqueFormRawValue['emplacement']>;
  telephone: FormControl<BoutiqueFormRawValue['telephone']>;
  statut: FormControl<BoutiqueFormRawValue['statut']>;
  dateCreation: FormControl<BoutiqueFormRawValue['dateCreation']>;
};

export type BoutiqueFormGroup = FormGroup<BoutiqueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BoutiqueFormService {
  createBoutiqueFormGroup(boutique?: BoutiqueFormGroupInput): BoutiqueFormGroup {
    const boutiqueRawValue = this.convertBoutiqueToBoutiqueRawValue({
      ...this.getFormDefaults(),
      ...(boutique ?? { id: null }),
    });
    return new FormGroup<BoutiqueFormGroupContent>({
      id: new FormControl(
        { value: boutiqueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(boutiqueRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      nom: new FormControl(boutiqueRawValue.nom, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      type: new FormControl(boutiqueRawValue.type),
      emplacement: new FormControl(boutiqueRawValue.emplacement, {
        validators: [Validators.maxLength(255)],
      }),
      telephone: new FormControl(boutiqueRawValue.telephone, {
        validators: [Validators.pattern(/^\d{8}$/)],
      }),
      statut: new FormControl(boutiqueRawValue.statut, {
        validators: [Validators.required],
      }),
      dateCreation: new FormControl(boutiqueRawValue.dateCreation, {
        validators: [Validators.required],
      }),
    });
  }

  getBoutique(form: BoutiqueFormGroup): IBoutique | NewBoutique {
    return this.convertBoutiqueRawValueToBoutique(form.getRawValue() as BoutiqueFormRawValue | NewBoutiqueFormRawValue);
  }

  resetForm(form: BoutiqueFormGroup, boutique: BoutiqueFormGroupInput): void {
    const boutiqueRawValue = this.convertBoutiqueToBoutiqueRawValue({ ...this.getFormDefaults(), ...boutique });
    form.reset({
      ...boutiqueRawValue,
      id: { value: boutiqueRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BoutiqueFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
    };
  }

  private convertBoutiqueRawValueToBoutique(rawBoutique: BoutiqueFormRawValue | NewBoutiqueFormRawValue): IBoutique | NewBoutique {
    return {
      ...rawBoutique,
      dateCreation: dayjs(rawBoutique.dateCreation, DATE_TIME_FORMAT),
    };
  }

  private convertBoutiqueToBoutiqueRawValue(
    boutique: IBoutique | (Partial<NewBoutique> & BoutiqueFormDefaults),
  ): BoutiqueFormRawValue | PartialWithRequiredKeyOf<NewBoutiqueFormRawValue> {
    return {
      ...boutique,
      dateCreation: boutique.dateCreation ? boutique.dateCreation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
