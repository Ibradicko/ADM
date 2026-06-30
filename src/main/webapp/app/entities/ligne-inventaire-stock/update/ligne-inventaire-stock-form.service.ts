import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneInventaireStock, NewLigneInventaireStock } from '../ligne-inventaire-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneInventaireStock for edit and NewLigneInventaireStockFormGroupInput for create.
 */
type LigneInventaireStockFormGroupInput = ILigneInventaireStock | PartialWithRequiredKeyOf<NewLigneInventaireStock>;

type LigneInventaireStockFormDefaults = Pick<NewLigneInventaireStock, 'id'>;

type LigneInventaireStockFormGroupContent = {
  id: FormControl<ILigneInventaireStock['id'] | NewLigneInventaireStock['id']>;
  quantiteTheorique: FormControl<ILigneInventaireStock['quantiteTheorique']>;
  quantiteComptee: FormControl<ILigneInventaireStock['quantiteComptee']>;
  ecart: FormControl<ILigneInventaireStock['ecart']>;
  commentaire: FormControl<ILigneInventaireStock['commentaire']>;
  inventaire: FormControl<ILigneInventaireStock['inventaire']>;
  produit: FormControl<ILigneInventaireStock['produit']>;
};

export type LigneInventaireStockFormGroup = FormGroup<LigneInventaireStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneInventaireStockFormService {
  createLigneInventaireStockFormGroup(ligneInventaireStock?: LigneInventaireStockFormGroupInput): LigneInventaireStockFormGroup {
    const ligneInventaireStockRawValue = {
      ...this.getFormDefaults(),
      ...(ligneInventaireStock ?? { id: null }),
    };
    return new FormGroup<LigneInventaireStockFormGroupContent>({
      id: new FormControl(
        { value: ligneInventaireStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantiteTheorique: new FormControl(ligneInventaireStockRawValue.quantiteTheorique, {
        validators: [Validators.required, Validators.min(0)],
      }),
      quantiteComptee: new FormControl(ligneInventaireStockRawValue.quantiteComptee, {
        validators: [Validators.required, Validators.min(0)],
      }),
      ecart: new FormControl(ligneInventaireStockRawValue.ecart),
      commentaire: new FormControl(ligneInventaireStockRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      inventaire: new FormControl(ligneInventaireStockRawValue.inventaire, {
        validators: [Validators.required],
      }),
      produit: new FormControl(ligneInventaireStockRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneInventaireStock(form: LigneInventaireStockFormGroup): ILigneInventaireStock | NewLigneInventaireStock {
    return form.getRawValue() as ILigneInventaireStock | NewLigneInventaireStock;
  }

  resetForm(form: LigneInventaireStockFormGroup, ligneInventaireStock: LigneInventaireStockFormGroupInput): void {
    const ligneInventaireStockRawValue = { ...this.getFormDefaults(), ...ligneInventaireStock };
    form.reset({
      ...ligneInventaireStockRawValue,
      id: { value: ligneInventaireStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneInventaireStockFormDefaults {
    return {
      id: null,
    };
  }
}
