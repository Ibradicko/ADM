import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITicketCaisse, NewTicketCaisse } from '../ticket-caisse.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicketCaisse for edit and NewTicketCaisseFormGroupInput for create.
 */
type TicketCaisseFormGroupInput = ITicketCaisse | PartialWithRequiredKeyOf<NewTicketCaisse>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITicketCaisse | NewTicketCaisse> = Omit<T, 'dateEmission'> & {
  dateEmission?: string | null;
};

type TicketCaisseFormRawValue = FormValueOf<ITicketCaisse>;

type NewTicketCaisseFormRawValue = FormValueOf<NewTicketCaisse>;

type TicketCaisseFormDefaults = Pick<NewTicketCaisse, 'id' | 'dateEmission'>;

type TicketCaisseFormGroupContent = {
  id: FormControl<TicketCaisseFormRawValue['id'] | NewTicketCaisse['id']>;
  numero: FormControl<TicketCaisseFormRawValue['numero']>;
  dateEmission: FormControl<TicketCaisseFormRawValue['dateEmission']>;
  nombreImpressions: FormControl<TicketCaisseFormRawValue['nombreImpressions']>;
  contenu: FormControl<TicketCaisseFormRawValue['contenu']>;
  vente: FormControl<TicketCaisseFormRawValue['vente']>;
};

export type TicketCaisseFormGroup = FormGroup<TicketCaisseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketCaisseFormService {
  createTicketCaisseFormGroup(ticketCaisse?: TicketCaisseFormGroupInput): TicketCaisseFormGroup {
    const ticketCaisseRawValue = this.convertTicketCaisseToTicketCaisseRawValue({
      ...this.getFormDefaults(),
      ...(ticketCaisse ?? { id: null }),
    });
    return new FormGroup<TicketCaisseFormGroupContent>({
      id: new FormControl(
        { value: ticketCaisseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numero: new FormControl(ticketCaisseRawValue.numero, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      dateEmission: new FormControl(ticketCaisseRawValue.dateEmission, {
        validators: [Validators.required],
      }),
      nombreImpressions: new FormControl(ticketCaisseRawValue.nombreImpressions, {
        validators: [Validators.required, Validators.min(1)],
      }),
      contenu: new FormControl(ticketCaisseRawValue.contenu),
      vente: new FormControl(ticketCaisseRawValue.vente, {
        validators: [Validators.required],
      }),
    });
  }

  getTicketCaisse(form: TicketCaisseFormGroup): ITicketCaisse | NewTicketCaisse {
    return this.convertTicketCaisseRawValueToTicketCaisse(form.getRawValue() as TicketCaisseFormRawValue | NewTicketCaisseFormRawValue);
  }

  resetForm(form: TicketCaisseFormGroup, ticketCaisse: TicketCaisseFormGroupInput): void {
    const ticketCaisseRawValue = this.convertTicketCaisseToTicketCaisseRawValue({ ...this.getFormDefaults(), ...ticketCaisse });
    form.reset({
      ...ticketCaisseRawValue,
      id: { value: ticketCaisseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TicketCaisseFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateEmission: currentTime,
    };
  }

  private convertTicketCaisseRawValueToTicketCaisse(
    rawTicketCaisse: TicketCaisseFormRawValue | NewTicketCaisseFormRawValue,
  ): ITicketCaisse | NewTicketCaisse {
    return {
      ...rawTicketCaisse,
      dateEmission: dayjs(rawTicketCaisse.dateEmission, DATE_TIME_FORMAT),
    };
  }

  private convertTicketCaisseToTicketCaisseRawValue(
    ticketCaisse: ITicketCaisse | (Partial<NewTicketCaisse> & TicketCaisseFormDefaults),
  ): TicketCaisseFormRawValue | PartialWithRequiredKeyOf<NewTicketCaisseFormRawValue> {
    return {
      ...ticketCaisse,
      dateEmission: ticketCaisse.dateEmission ? ticketCaisse.dateEmission.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
