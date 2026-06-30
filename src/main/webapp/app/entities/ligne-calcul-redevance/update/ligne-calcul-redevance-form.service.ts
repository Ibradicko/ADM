import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneCalculRedevance, NewLigneCalculRedevance } from '../ligne-calcul-redevance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneCalculRedevance for edit and NewLigneCalculRedevanceFormGroupInput for create.
 */
type LigneCalculRedevanceFormGroupInput = ILigneCalculRedevance | PartialWithRequiredKeyOf<NewLigneCalculRedevance>;

type LigneCalculRedevanceFormDefaults = Pick<NewLigneCalculRedevance, 'id'>;

type LigneCalculRedevanceFormGroupContent = {
  id: FormControl<ILigneCalculRedevance['id'] | NewLigneCalculRedevance['id']>;
  baseCalcul: FormControl<ILigneCalculRedevance['baseCalcul']>;
  tauxApplique: FormControl<ILigneCalculRedevance['tauxApplique']>;
  montantRedevance: FormControl<ILigneCalculRedevance['montantRedevance']>;
  calcul: FormControl<ILigneCalculRedevance['calcul']>;
  vente: FormControl<ILigneCalculRedevance['vente']>;
};

export type LigneCalculRedevanceFormGroup = FormGroup<LigneCalculRedevanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneCalculRedevanceFormService {
  createLigneCalculRedevanceFormGroup(ligneCalculRedevance?: LigneCalculRedevanceFormGroupInput): LigneCalculRedevanceFormGroup {
    const ligneCalculRedevanceRawValue = {
      ...this.getFormDefaults(),
      ...(ligneCalculRedevance ?? { id: null }),
    };
    return new FormGroup<LigneCalculRedevanceFormGroupContent>({
      id: new FormControl(
        { value: ligneCalculRedevanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      baseCalcul: new FormControl(ligneCalculRedevanceRawValue.baseCalcul, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tauxApplique: new FormControl(ligneCalculRedevanceRawValue.tauxApplique, {
        validators: [Validators.required, Validators.min(0), Validators.max(100)],
      }),
      montantRedevance: new FormControl(ligneCalculRedevanceRawValue.montantRedevance, {
        validators: [Validators.required, Validators.min(0)],
      }),
      calcul: new FormControl(ligneCalculRedevanceRawValue.calcul, {
        validators: [Validators.required],
      }),
      vente: new FormControl(ligneCalculRedevanceRawValue.vente),
    });
  }

  getLigneCalculRedevance(form: LigneCalculRedevanceFormGroup): ILigneCalculRedevance | NewLigneCalculRedevance {
    return form.getRawValue() as ILigneCalculRedevance | NewLigneCalculRedevance;
  }

  resetForm(form: LigneCalculRedevanceFormGroup, ligneCalculRedevance: LigneCalculRedevanceFormGroupInput): void {
    const ligneCalculRedevanceRawValue = { ...this.getFormDefaults(), ...ligneCalculRedevance };
    form.reset({
      ...ligneCalculRedevanceRawValue,
      id: { value: ligneCalculRedevanceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneCalculRedevanceFormDefaults {
    return {
      id: null,
    };
  }
}
