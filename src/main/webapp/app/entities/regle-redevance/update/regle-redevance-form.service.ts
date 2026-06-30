import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRegleRedevance, NewRegleRedevance } from '../regle-redevance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRegleRedevance for edit and NewRegleRedevanceFormGroupInput for create.
 */
type RegleRedevanceFormGroupInput = IRegleRedevance | PartialWithRequiredKeyOf<NewRegleRedevance>;

type RegleRedevanceFormDefaults = Pick<NewRegleRedevance, 'id' | 'actif'>;

type RegleRedevanceFormGroupContent = {
  id: FormControl<IRegleRedevance['id'] | NewRegleRedevance['id']>;
  code: FormControl<IRegleRedevance['code']>;
  typeRegle: FormControl<IRegleRedevance['typeRegle']>;
  taux: FormControl<IRegleRedevance['taux']>;
  dateDebut: FormControl<IRegleRedevance['dateDebut']>;
  dateFin: FormControl<IRegleRedevance['dateFin']>;
  priorite: FormControl<IRegleRedevance['priorite']>;
  actif: FormControl<IRegleRedevance['actif']>;
  boutique: FormControl<IRegleRedevance['boutique']>;
  locataire: FormControl<IRegleRedevance['locataire']>;
  groupeArticle: FormControl<IRegleRedevance['groupeArticle']>;
  produit: FormControl<IRegleRedevance['produit']>;
};

export type RegleRedevanceFormGroup = FormGroup<RegleRedevanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RegleRedevanceFormService {
  createRegleRedevanceFormGroup(regleRedevance?: RegleRedevanceFormGroupInput): RegleRedevanceFormGroup {
    const regleRedevanceRawValue = {
      ...this.getFormDefaults(),
      ...(regleRedevance ?? { id: null }),
    };
    return new FormGroup<RegleRedevanceFormGroupContent>({
      id: new FormControl(
        { value: regleRedevanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(regleRedevanceRawValue.code, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      typeRegle: new FormControl(regleRedevanceRawValue.typeRegle, {
        validators: [Validators.required],
      }),
      taux: new FormControl(regleRedevanceRawValue.taux, {
        validators: [Validators.required, Validators.min(0), Validators.max(100)],
      }),
      dateDebut: new FormControl(regleRedevanceRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFin: new FormControl(regleRedevanceRawValue.dateFin),
      priorite: new FormControl(regleRedevanceRawValue.priorite, {
        validators: [Validators.min(1)],
      }),
      actif: new FormControl(regleRedevanceRawValue.actif, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(regleRedevanceRawValue.boutique),
      locataire: new FormControl(regleRedevanceRawValue.locataire),
      groupeArticle: new FormControl(regleRedevanceRawValue.groupeArticle),
      produit: new FormControl(regleRedevanceRawValue.produit),
    });
  }

  getRegleRedevance(form: RegleRedevanceFormGroup): IRegleRedevance | NewRegleRedevance {
    return form.getRawValue() as IRegleRedevance | NewRegleRedevance;
  }

  resetForm(form: RegleRedevanceFormGroup, regleRedevance: RegleRedevanceFormGroupInput): void {
    const regleRedevanceRawValue = { ...this.getFormDefaults(), ...regleRedevance };
    form.reset({
      ...regleRedevanceRawValue,
      id: { value: regleRedevanceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RegleRedevanceFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
