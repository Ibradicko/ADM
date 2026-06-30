import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IVente, NewVente } from '../vente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVente for edit and NewVenteFormGroupInput for create.
 */
type VenteFormGroupInput = IVente | PartialWithRequiredKeyOf<NewVente>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IVente | NewVente> = Omit<T, 'dateHeure'> & {
  dateHeure?: string | null;
};

type VenteFormRawValue = FormValueOf<IVente>;

type NewVenteFormRawValue = FormValueOf<NewVente>;

type VenteFormDefaults = Pick<NewVente, 'id' | 'dateHeure'>;

type VenteFormGroupContent = {
  id: FormControl<VenteFormRawValue['id'] | NewVente['id']>;
  numeroTicket: FormControl<VenteFormRawValue['numeroTicket']>;
  dateHeure: FormControl<VenteFormRawValue['dateHeure']>;
  statut: FormControl<VenteFormRawValue['statut']>;
  referencePassager: FormControl<VenteFormRawValue['referencePassager']>;
  referenceCarteEmbarquement: FormControl<VenteFormRawValue['referenceCarteEmbarquement']>;
  montantBrut: FormControl<VenteFormRawValue['montantBrut']>;
  montantRemise: FormControl<VenteFormRawValue['montantRemise']>;
  montantNet: FormControl<VenteFormRawValue['montantNet']>;
  commentaire: FormControl<VenteFormRawValue['commentaire']>;
  boutique: FormControl<VenteFormRawValue['boutique']>;
  locataire: FormControl<VenteFormRawValue['locataire']>;
  vendeur: FormControl<VenteFormRawValue['vendeur']>;
};

export type VenteFormGroup = FormGroup<VenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VenteFormService {
  createVenteFormGroup(vente?: VenteFormGroupInput): VenteFormGroup {
    const venteRawValue = this.convertVenteToVenteRawValue({
      ...this.getFormDefaults(),
      ...(vente ?? { id: null }),
    });
    return new FormGroup<VenteFormGroupContent>({
      id: new FormControl(
        { value: venteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numeroTicket: new FormControl(venteRawValue.numeroTicket, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      dateHeure: new FormControl(venteRawValue.dateHeure, {
        validators: [Validators.required],
      }),
      statut: new FormControl(venteRawValue.statut, {
        validators: [Validators.required],
      }),
      referencePassager: new FormControl(venteRawValue.referencePassager, {
        validators: [Validators.maxLength(80)],
      }),
      referenceCarteEmbarquement: new FormControl(venteRawValue.referenceCarteEmbarquement, {
        validators: [Validators.maxLength(80)],
      }),
      montantBrut: new FormControl(venteRawValue.montantBrut, {
        validators: [Validators.required, Validators.min(0)],
      }),
      montantRemise: new FormControl(venteRawValue.montantRemise, {
        validators: [Validators.min(0)],
      }),
      montantNet: new FormControl(venteRawValue.montantNet, {
        validators: [Validators.required, Validators.min(0)],
      }),
      commentaire: new FormControl(venteRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      boutique: new FormControl(venteRawValue.boutique, {
        validators: [Validators.required],
      }),
      locataire: new FormControl(venteRawValue.locataire, {
        validators: [Validators.required],
      }),
      vendeur: new FormControl(venteRawValue.vendeur, {
        validators: [Validators.required],
      }),
    });
  }

  getVente(form: VenteFormGroup): IVente | NewVente {
    return this.convertVenteRawValueToVente(form.getRawValue() as VenteFormRawValue | NewVenteFormRawValue);
  }

  resetForm(form: VenteFormGroup, vente: VenteFormGroupInput): void {
    const venteRawValue = this.convertVenteToVenteRawValue({ ...this.getFormDefaults(), ...vente });
    form.reset({
      ...venteRawValue,
      id: { value: venteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VenteFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateHeure: currentTime,
    };
  }

  private convertVenteRawValueToVente(rawVente: VenteFormRawValue | NewVenteFormRawValue): IVente | NewVente {
    return {
      ...rawVente,
      dateHeure: dayjs(rawVente.dateHeure, DATE_TIME_FORMAT),
    };
  }

  private convertVenteToVenteRawValue(
    vente: IVente | (Partial<NewVente> & VenteFormDefaults),
  ): VenteFormRawValue | PartialWithRequiredKeyOf<NewVenteFormRawValue> {
    return {
      ...vente,
      dateHeure: vente.dateHeure ? vente.dateHeure.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
