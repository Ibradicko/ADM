import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneReceptionProduit, NewLigneReceptionProduit } from '../ligne-reception-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneReceptionProduit for edit and NewLigneReceptionProduitFormGroupInput for create.
 */
type LigneReceptionProduitFormGroupInput = ILigneReceptionProduit | PartialWithRequiredKeyOf<NewLigneReceptionProduit>;

type LigneReceptionProduitFormDefaults = Pick<NewLigneReceptionProduit, 'id'>;

type LigneReceptionProduitFormGroupContent = {
  id: FormControl<ILigneReceptionProduit['id'] | NewLigneReceptionProduit['id']>;
  quantiteAttendue: FormControl<ILigneReceptionProduit['quantiteAttendue']>;
  quantiteRecue: FormControl<ILigneReceptionProduit['quantiteRecue']>;
  ecart: FormControl<ILigneReceptionProduit['ecart']>;
  codeBarresScanne: FormControl<ILigneReceptionProduit['codeBarresScanne']>;
  reception: FormControl<ILigneReceptionProduit['reception']>;
  produit: FormControl<ILigneReceptionProduit['produit']>;
};

export type LigneReceptionProduitFormGroup = FormGroup<LigneReceptionProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneReceptionProduitFormService {
  createLigneReceptionProduitFormGroup(ligneReceptionProduit?: LigneReceptionProduitFormGroupInput): LigneReceptionProduitFormGroup {
    const ligneReceptionProduitRawValue = {
      ...this.getFormDefaults(),
      ...(ligneReceptionProduit ?? { id: null }),
    };
    return new FormGroup<LigneReceptionProduitFormGroupContent>({
      id: new FormControl(
        { value: ligneReceptionProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantiteAttendue: new FormControl(ligneReceptionProduitRawValue.quantiteAttendue, {
        validators: [Validators.min(0)],
      }),
      quantiteRecue: new FormControl(ligneReceptionProduitRawValue.quantiteRecue, {
        validators: [Validators.required, Validators.min(0)],
      }),
      ecart: new FormControl(ligneReceptionProduitRawValue.ecart),
      codeBarresScanne: new FormControl(ligneReceptionProduitRawValue.codeBarresScanne, {
        validators: [Validators.maxLength(80)],
      }),
      reception: new FormControl(ligneReceptionProduitRawValue.reception, {
        validators: [Validators.required],
      }),
      produit: new FormControl(ligneReceptionProduitRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneReceptionProduit(form: LigneReceptionProduitFormGroup): ILigneReceptionProduit | NewLigneReceptionProduit {
    return form.getRawValue() as ILigneReceptionProduit | NewLigneReceptionProduit;
  }

  resetForm(form: LigneReceptionProduitFormGroup, ligneReceptionProduit: LigneReceptionProduitFormGroupInput): void {
    const ligneReceptionProduitRawValue = { ...this.getFormDefaults(), ...ligneReceptionProduit };
    form.reset({
      ...ligneReceptionProduitRawValue,
      id: { value: ligneReceptionProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneReceptionProduitFormDefaults {
    return {
      id: null,
    };
  }
}
