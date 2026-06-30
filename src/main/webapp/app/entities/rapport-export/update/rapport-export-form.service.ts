import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRapportExport, NewRapportExport } from '../rapport-export.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRapportExport for edit and NewRapportExportFormGroupInput for create.
 */
type RapportExportFormGroupInput = IRapportExport | PartialWithRequiredKeyOf<NewRapportExport>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRapportExport | NewRapportExport> = Omit<T, 'dateGeneration'> & {
  dateGeneration?: string | null;
};

type RapportExportFormRawValue = FormValueOf<IRapportExport>;

type NewRapportExportFormRawValue = FormValueOf<NewRapportExport>;

type RapportExportFormDefaults = Pick<NewRapportExport, 'id' | 'dateGeneration'>;

type RapportExportFormGroupContent = {
  id: FormControl<RapportExportFormRawValue['id'] | NewRapportExport['id']>;
  reference: FormControl<RapportExportFormRawValue['reference']>;
  typeRapport: FormControl<RapportExportFormRawValue['typeRapport']>;
  format: FormControl<RapportExportFormRawValue['format']>;
  periodeDebut: FormControl<RapportExportFormRawValue['periodeDebut']>;
  periodeFin: FormControl<RapportExportFormRawValue['periodeFin']>;
  cheminFichier: FormControl<RapportExportFormRawValue['cheminFichier']>;
  dateGeneration: FormControl<RapportExportFormRawValue['dateGeneration']>;
  boutique: FormControl<RapportExportFormRawValue['boutique']>;
  locataire: FormControl<RapportExportFormRawValue['locataire']>;
  utilisateur: FormControl<RapportExportFormRawValue['utilisateur']>;
};

export type RapportExportFormGroup = FormGroup<RapportExportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RapportExportFormService {
  createRapportExportFormGroup(rapportExport?: RapportExportFormGroupInput): RapportExportFormGroup {
    const rapportExportRawValue = this.convertRapportExportToRapportExportRawValue({
      ...this.getFormDefaults(),
      ...(rapportExport ?? { id: null }),
    });
    return new FormGroup<RapportExportFormGroupContent>({
      id: new FormControl(
        { value: rapportExportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(rapportExportRawValue.reference, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      typeRapport: new FormControl(rapportExportRawValue.typeRapport, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      format: new FormControl(rapportExportRawValue.format, {
        validators: [Validators.required],
      }),
      periodeDebut: new FormControl(rapportExportRawValue.periodeDebut),
      periodeFin: new FormControl(rapportExportRawValue.periodeFin),
      cheminFichier: new FormControl(rapportExportRawValue.cheminFichier, {
        validators: [Validators.maxLength(255)],
      }),
      dateGeneration: new FormControl(rapportExportRawValue.dateGeneration, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(rapportExportRawValue.boutique),
      locataire: new FormControl(rapportExportRawValue.locataire),
      utilisateur: new FormControl(rapportExportRawValue.utilisateur, {
        validators: [Validators.required],
      }),
    });
  }

  getRapportExport(form: RapportExportFormGroup): IRapportExport | NewRapportExport {
    return this.convertRapportExportRawValueToRapportExport(form.getRawValue() as RapportExportFormRawValue | NewRapportExportFormRawValue);
  }

  resetForm(form: RapportExportFormGroup, rapportExport: RapportExportFormGroupInput): void {
    const rapportExportRawValue = this.convertRapportExportToRapportExportRawValue({ ...this.getFormDefaults(), ...rapportExport });
    form.reset({
      ...rapportExportRawValue,
      id: { value: rapportExportRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RapportExportFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateGeneration: currentTime,
    };
  }

  private convertRapportExportRawValueToRapportExport(
    rawRapportExport: RapportExportFormRawValue | NewRapportExportFormRawValue,
  ): IRapportExport | NewRapportExport {
    return {
      ...rawRapportExport,
      dateGeneration: dayjs(rawRapportExport.dateGeneration, DATE_TIME_FORMAT),
    };
  }

  private convertRapportExportToRapportExportRawValue(
    rapportExport: IRapportExport | (Partial<NewRapportExport> & RapportExportFormDefaults),
  ): RapportExportFormRawValue | PartialWithRequiredKeyOf<NewRapportExportFormRawValue> {
    return {
      ...rapportExport,
      dateGeneration: rapportExport.dateGeneration ? rapportExport.dateGeneration.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
