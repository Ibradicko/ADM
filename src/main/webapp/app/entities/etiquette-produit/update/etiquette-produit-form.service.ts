import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEtiquetteProduit, NewEtiquetteProduit } from '../etiquette-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEtiquetteProduit for edit and NewEtiquetteProduitFormGroupInput for create.
 */
type EtiquetteProduitFormGroupInput = IEtiquetteProduit | PartialWithRequiredKeyOf<NewEtiquetteProduit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEtiquetteProduit | NewEtiquetteProduit> = Omit<T, 'dateImpression'> & {
  dateImpression?: string | null;
};

type EtiquetteProduitFormRawValue = FormValueOf<IEtiquetteProduit>;

type NewEtiquetteProduitFormRawValue = FormValueOf<NewEtiquetteProduit>;

type EtiquetteProduitFormDefaults = Pick<NewEtiquetteProduit, 'id' | 'imprimee' | 'dateImpression'>;

type EtiquetteProduitFormGroupContent = {
  id: FormControl<EtiquetteProduitFormRawValue['id'] | NewEtiquetteProduit['id']>;
  quantite: FormControl<EtiquetteProduitFormRawValue['quantite']>;
  imprimee: FormControl<EtiquetteProduitFormRawValue['imprimee']>;
  dateImpression: FormControl<EtiquetteProduitFormRawValue['dateImpression']>;
  produit: FormControl<EtiquetteProduitFormRawValue['produit']>;
  lot: FormControl<EtiquetteProduitFormRawValue['lot']>;
};

export type EtiquetteProduitFormGroup = FormGroup<EtiquetteProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EtiquetteProduitFormService {
  createEtiquetteProduitFormGroup(etiquetteProduit?: EtiquetteProduitFormGroupInput): EtiquetteProduitFormGroup {
    const etiquetteProduitRawValue = this.convertEtiquetteProduitToEtiquetteProduitRawValue({
      ...this.getFormDefaults(),
      ...(etiquetteProduit ?? { id: null }),
    });
    return new FormGroup<EtiquetteProduitFormGroupContent>({
      id: new FormControl(
        { value: etiquetteProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(etiquetteProduitRawValue.quantite, {
        validators: [Validators.required, Validators.min(1)],
      }),
      imprimee: new FormControl(etiquetteProduitRawValue.imprimee, {
        validators: [Validators.required],
      }),
      dateImpression: new FormControl(etiquetteProduitRawValue.dateImpression),
      produit: new FormControl(etiquetteProduitRawValue.produit, {
        validators: [Validators.required],
      }),
      lot: new FormControl(etiquetteProduitRawValue.lot, {
        validators: [Validators.required],
      }),
    });
  }

  getEtiquetteProduit(form: EtiquetteProduitFormGroup): IEtiquetteProduit | NewEtiquetteProduit {
    return this.convertEtiquetteProduitRawValueToEtiquetteProduit(
      form.getRawValue() as EtiquetteProduitFormRawValue | NewEtiquetteProduitFormRawValue,
    );
  }

  resetForm(form: EtiquetteProduitFormGroup, etiquetteProduit: EtiquetteProduitFormGroupInput): void {
    const etiquetteProduitRawValue = this.convertEtiquetteProduitToEtiquetteProduitRawValue({
      ...this.getFormDefaults(),
      ...etiquetteProduit,
    });
    form.reset({
      ...etiquetteProduitRawValue,
      id: { value: etiquetteProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EtiquetteProduitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      imprimee: false,
      dateImpression: currentTime,
    };
  }

  private convertEtiquetteProduitRawValueToEtiquetteProduit(
    rawEtiquetteProduit: EtiquetteProduitFormRawValue | NewEtiquetteProduitFormRawValue,
  ): IEtiquetteProduit | NewEtiquetteProduit {
    return {
      ...rawEtiquetteProduit,
      dateImpression: dayjs(rawEtiquetteProduit.dateImpression, DATE_TIME_FORMAT),
    };
  }

  private convertEtiquetteProduitToEtiquetteProduitRawValue(
    etiquetteProduit: IEtiquetteProduit | (Partial<NewEtiquetteProduit> & EtiquetteProduitFormDefaults),
  ): EtiquetteProduitFormRawValue | PartialWithRequiredKeyOf<NewEtiquetteProduitFormRawValue> {
    return {
      ...etiquetteProduit,
      dateImpression: etiquetteProduit.dateImpression ? etiquetteProduit.dateImpression.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
