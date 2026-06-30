import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILocataire, NewLocataire } from '../locataire.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocataire for edit and NewLocataireFormGroupInput for create.
 */
type LocataireFormGroupInput = ILocataire | PartialWithRequiredKeyOf<NewLocataire>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILocataire | NewLocataire> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

type LocataireFormRawValue = FormValueOf<ILocataire>;

type NewLocataireFormRawValue = FormValueOf<NewLocataire>;

type LocataireFormDefaults = Pick<NewLocataire, 'id' | 'dateCreation'>;

type LocataireFormGroupContent = {
  id: FormControl<LocataireFormRawValue['id'] | NewLocataire['id']>;
  code: FormControl<LocataireFormRawValue['code']>;
  nom: FormControl<LocataireFormRawValue['nom']>;
  typeLocataire: FormControl<LocataireFormRawValue['typeLocataire']>;
  numeroIdentification: FormControl<LocataireFormRawValue['numeroIdentification']>;
  telephone: FormControl<LocataireFormRawValue['telephone']>;
  email: FormControl<LocataireFormRawValue['email']>;
  adresse: FormControl<LocataireFormRawValue['adresse']>;
  statut: FormControl<LocataireFormRawValue['statut']>;
  dateCreation: FormControl<LocataireFormRawValue['dateCreation']>;
};

export type LocataireFormGroup = FormGroup<LocataireFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocataireFormService {
  createLocataireFormGroup(locataire?: LocataireFormGroupInput): LocataireFormGroup {
    const locataireRawValue = this.convertLocataireToLocataireRawValue({
      ...this.getFormDefaults(),
      ...(locataire ?? { id: null }),
    });
    return new FormGroup<LocataireFormGroupContent>({
      id: new FormControl(
        { value: locataireRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(locataireRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      nom: new FormControl(locataireRawValue.nom, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      typeLocataire: new FormControl(locataireRawValue.typeLocataire, {
        validators: [Validators.required],
      }),
      numeroIdentification: new FormControl(locataireRawValue.numeroIdentification, {
        validators: [Validators.maxLength(80)],
      }),
      telephone: new FormControl(locataireRawValue.telephone, {
        validators: [Validators.maxLength(30)],
      }),
      email: new FormControl(locataireRawValue.email, {
        validators: [Validators.maxLength(120)],
      }),
      adresse: new FormControl(locataireRawValue.adresse, {
        validators: [Validators.maxLength(255)],
      }),
      statut: new FormControl(locataireRawValue.statut, {
        validators: [Validators.required],
      }),
      dateCreation: new FormControl(locataireRawValue.dateCreation),
    });
  }

  getLocataire(form: LocataireFormGroup): ILocataire | NewLocataire {
    return this.convertLocataireRawValueToLocataire(form.getRawValue() as LocataireFormRawValue | NewLocataireFormRawValue);
  }

  resetForm(form: LocataireFormGroup, locataire: LocataireFormGroupInput): void {
    const locataireRawValue = this.convertLocataireToLocataireRawValue({ ...this.getFormDefaults(), ...locataire });
    form.reset({
      ...locataireRawValue,
      id: { value: locataireRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LocataireFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
    };
  }

  private convertLocataireRawValueToLocataire(rawLocataire: LocataireFormRawValue | NewLocataireFormRawValue): ILocataire | NewLocataire {
    return {
      ...rawLocataire,
      dateCreation: dayjs(rawLocataire.dateCreation, DATE_TIME_FORMAT),
    };
  }

  private convertLocataireToLocataireRawValue(
    locataire: ILocataire | (Partial<NewLocataire> & LocataireFormDefaults),
  ): LocataireFormRawValue | PartialWithRequiredKeyOf<NewLocataireFormRawValue> {
    return {
      ...locataire,
      dateCreation: locataire.dateCreation ? locataire.dateCreation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
