import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IScanInconnu, NewScanInconnu } from '../scan-inconnu.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IScanInconnu for edit and NewScanInconnuFormGroupInput for create.
 */
type ScanInconnuFormGroupInput = IScanInconnu | PartialWithRequiredKeyOf<NewScanInconnu>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IScanInconnu | NewScanInconnu> = Omit<T, 'dateScan'> & {
  dateScan?: string | null;
};

type ScanInconnuFormRawValue = FormValueOf<IScanInconnu>;

type NewScanInconnuFormRawValue = FormValueOf<NewScanInconnu>;

type ScanInconnuFormDefaults = Pick<NewScanInconnu, 'id' | 'dateScan' | 'resolu'>;

type ScanInconnuFormGroupContent = {
  id: FormControl<ScanInconnuFormRawValue['id'] | NewScanInconnu['id']>;
  codeScanne: FormControl<ScanInconnuFormRawValue['codeScanne']>;
  ecranOrigine: FormControl<ScanInconnuFormRawValue['ecranOrigine']>;
  dateScan: FormControl<ScanInconnuFormRawValue['dateScan']>;
  commentaire: FormControl<ScanInconnuFormRawValue['commentaire']>;
  resolu: FormControl<ScanInconnuFormRawValue['resolu']>;
  boutique: FormControl<ScanInconnuFormRawValue['boutique']>;
  produitAffecte: FormControl<ScanInconnuFormRawValue['produitAffecte']>;
};

export type ScanInconnuFormGroup = FormGroup<ScanInconnuFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ScanInconnuFormService {
  createScanInconnuFormGroup(scanInconnu?: ScanInconnuFormGroupInput): ScanInconnuFormGroup {
    const scanInconnuRawValue = this.convertScanInconnuToScanInconnuRawValue({
      ...this.getFormDefaults(),
      ...(scanInconnu ?? { id: null }),
    });
    return new FormGroup<ScanInconnuFormGroupContent>({
      id: new FormControl(
        { value: scanInconnuRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      codeScanne: new FormControl(scanInconnuRawValue.codeScanne, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      ecranOrigine: new FormControl(scanInconnuRawValue.ecranOrigine, {
        validators: [Validators.maxLength(80)],
      }),
      dateScan: new FormControl(scanInconnuRawValue.dateScan, {
        validators: [Validators.required],
      }),
      commentaire: new FormControl(scanInconnuRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      resolu: new FormControl(scanInconnuRawValue.resolu, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(scanInconnuRawValue.boutique, {
        validators: [Validators.required],
      }),
      produitAffecte: new FormControl(scanInconnuRawValue.produitAffecte),
    });
  }

  getScanInconnu(form: ScanInconnuFormGroup): IScanInconnu | NewScanInconnu {
    return this.convertScanInconnuRawValueToScanInconnu(form.getRawValue() as ScanInconnuFormRawValue | NewScanInconnuFormRawValue);
  }

  resetForm(form: ScanInconnuFormGroup, scanInconnu: ScanInconnuFormGroupInput): void {
    const scanInconnuRawValue = this.convertScanInconnuToScanInconnuRawValue({ ...this.getFormDefaults(), ...scanInconnu });
    form.reset({
      ...scanInconnuRawValue,
      id: { value: scanInconnuRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ScanInconnuFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateScan: currentTime,
      resolu: false,
    };
  }

  private convertScanInconnuRawValueToScanInconnu(
    rawScanInconnu: ScanInconnuFormRawValue | NewScanInconnuFormRawValue,
  ): IScanInconnu | NewScanInconnu {
    return {
      ...rawScanInconnu,
      dateScan: dayjs(rawScanInconnu.dateScan, DATE_TIME_FORMAT),
    };
  }

  private convertScanInconnuToScanInconnuRawValue(
    scanInconnu: IScanInconnu | (Partial<NewScanInconnu> & ScanInconnuFormDefaults),
  ): ScanInconnuFormRawValue | PartialWithRequiredKeyOf<NewScanInconnuFormRawValue> {
    return {
      ...scanInconnu,
      dateScan: scanInconnu.dateScan ? scanInconnu.dateScan.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
