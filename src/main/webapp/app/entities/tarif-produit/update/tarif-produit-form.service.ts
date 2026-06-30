import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITarifProduit, NewTarifProduit } from '../tarif-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITarifProduit for edit and NewTarifProduitFormGroupInput for create.
 */
type TarifProduitFormGroupInput = ITarifProduit | PartialWithRequiredKeyOf<NewTarifProduit>;

type TarifProduitFormDefaults = Pick<NewTarifProduit, 'id' | 'actif'>;

type TarifProduitFormGroupContent = {
  id: FormControl<ITarifProduit['id'] | NewTarifProduit['id']>;
  montant: FormControl<ITarifProduit['montant']>;
  typePrix: FormControl<ITarifProduit['typePrix']>;
  dateDebut: FormControl<ITarifProduit['dateDebut']>;
  dateFin: FormControl<ITarifProduit['dateFin']>;
  actif: FormControl<ITarifProduit['actif']>;
  produit: FormControl<ITarifProduit['produit']>;
};

export type TarifProduitFormGroup = FormGroup<TarifProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TarifProduitFormService {
  createTarifProduitFormGroup(tarifProduit?: TarifProduitFormGroupInput): TarifProduitFormGroup {
    const tarifProduitRawValue = {
      ...this.getFormDefaults(),
      ...(tarifProduit ?? { id: null }),
    };
    return new FormGroup<TarifProduitFormGroupContent>({
      id: new FormControl(
        { value: tarifProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      montant: new FormControl(tarifProduitRawValue.montant, {
        validators: [Validators.required, Validators.min(0)],
      }),
      typePrix: new FormControl(tarifProduitRawValue.typePrix, {
        validators: [Validators.required],
      }),
      dateDebut: new FormControl(tarifProduitRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFin: new FormControl(tarifProduitRawValue.dateFin),
      actif: new FormControl(tarifProduitRawValue.actif, {
        validators: [Validators.required],
      }),
      produit: new FormControl(tarifProduitRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getTarifProduit(form: TarifProduitFormGroup): ITarifProduit | NewTarifProduit {
    return form.getRawValue() as ITarifProduit | NewTarifProduit;
  }

  resetForm(form: TarifProduitFormGroup, tarifProduit: TarifProduitFormGroupInput): void {
    const tarifProduitRawValue = { ...this.getFormDefaults(), ...tarifProduit };
    form.reset({
      ...tarifProduitRawValue,
      id: { value: tarifProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TarifProduitFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
