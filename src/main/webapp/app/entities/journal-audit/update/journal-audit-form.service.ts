import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IJournalAudit, NewJournalAudit } from '../journal-audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJournalAudit for edit and NewJournalAuditFormGroupInput for create.
 */
type JournalAuditFormGroupInput = IJournalAudit | PartialWithRequiredKeyOf<NewJournalAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IJournalAudit | NewJournalAudit> = Omit<T, 'dateAction'> & {
  dateAction?: string | null;
};

type JournalAuditFormRawValue = FormValueOf<IJournalAudit>;

type NewJournalAuditFormRawValue = FormValueOf<NewJournalAudit>;

type JournalAuditFormDefaults = Pick<NewJournalAudit, 'id' | 'dateAction'>;

type JournalAuditFormGroupContent = {
  id: FormControl<JournalAuditFormRawValue['id'] | NewJournalAudit['id']>;
  typeAction: FormControl<JournalAuditFormRawValue['typeAction']>;
  entiteConcernee: FormControl<JournalAuditFormRawValue['entiteConcernee']>;
  identifiantEntite: FormControl<JournalAuditFormRawValue['identifiantEntite']>;
  description: FormControl<JournalAuditFormRawValue['description']>;
  adresseIp: FormControl<JournalAuditFormRawValue['adresseIp']>;
  dateAction: FormControl<JournalAuditFormRawValue['dateAction']>;
  boutique: FormControl<JournalAuditFormRawValue['boutique']>;
  utilisateur: FormControl<JournalAuditFormRawValue['utilisateur']>;
};

export type JournalAuditFormGroup = FormGroup<JournalAuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JournalAuditFormService {
  createJournalAuditFormGroup(journalAudit?: JournalAuditFormGroupInput): JournalAuditFormGroup {
    const journalAuditRawValue = this.convertJournalAuditToJournalAuditRawValue({
      ...this.getFormDefaults(),
      ...(journalAudit ?? { id: null }),
    });
    return new FormGroup<JournalAuditFormGroupContent>({
      id: new FormControl(
        { value: journalAuditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      typeAction: new FormControl(journalAuditRawValue.typeAction, {
        validators: [Validators.required],
      }),
      entiteConcernee: new FormControl(journalAuditRawValue.entiteConcernee, {
        validators: [Validators.maxLength(100)],
      }),
      identifiantEntite: new FormControl(journalAuditRawValue.identifiantEntite, {
        validators: [Validators.maxLength(100)],
      }),
      description: new FormControl(journalAuditRawValue.description),
      adresseIp: new FormControl(journalAuditRawValue.adresseIp, {
        validators: [Validators.maxLength(80)],
      }),
      dateAction: new FormControl(journalAuditRawValue.dateAction, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(journalAuditRawValue.boutique),
      utilisateur: new FormControl(journalAuditRawValue.utilisateur),
    });
  }

  getJournalAudit(form: JournalAuditFormGroup): IJournalAudit | NewJournalAudit {
    return this.convertJournalAuditRawValueToJournalAudit(form.getRawValue() as JournalAuditFormRawValue | NewJournalAuditFormRawValue);
  }

  resetForm(form: JournalAuditFormGroup, journalAudit: JournalAuditFormGroupInput): void {
    const journalAuditRawValue = this.convertJournalAuditToJournalAuditRawValue({ ...this.getFormDefaults(), ...journalAudit });
    form.reset({
      ...journalAuditRawValue,
      id: { value: journalAuditRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): JournalAuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateAction: currentTime,
    };
  }

  private convertJournalAuditRawValueToJournalAudit(
    rawJournalAudit: JournalAuditFormRawValue | NewJournalAuditFormRawValue,
  ): IJournalAudit | NewJournalAudit {
    return {
      ...rawJournalAudit,
      dateAction: dayjs(rawJournalAudit.dateAction, DATE_TIME_FORMAT),
    };
  }

  private convertJournalAuditToJournalAuditRawValue(
    journalAudit: IJournalAudit | (Partial<NewJournalAudit> & JournalAuditFormDefaults),
  ): JournalAuditFormRawValue | PartialWithRequiredKeyOf<NewJournalAuditFormRawValue> {
    return {
      ...journalAudit,
      dateAction: journalAudit.dateAction ? journalAudit.dateAction.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
