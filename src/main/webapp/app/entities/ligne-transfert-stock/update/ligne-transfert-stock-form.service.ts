import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneTransfertStock, NewLigneTransfertStock } from '../ligne-transfert-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneTransfertStock for edit and NewLigneTransfertStockFormGroupInput for create.
 */
type LigneTransfertStockFormGroupInput = ILigneTransfertStock | PartialWithRequiredKeyOf<NewLigneTransfertStock>;

type LigneTransfertStockFormDefaults = Pick<NewLigneTransfertStock, 'id'>;

type LigneTransfertStockFormGroupContent = {
  id: FormControl<ILigneTransfertStock['id'] | NewLigneTransfertStock['id']>;
  quantite: FormControl<ILigneTransfertStock['quantite']>;
  commentaire: FormControl<ILigneTransfertStock['commentaire']>;
  transfert: FormControl<ILigneTransfertStock['transfert']>;
  produit: FormControl<ILigneTransfertStock['produit']>;
};

export type LigneTransfertStockFormGroup = FormGroup<LigneTransfertStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneTransfertStockFormService {
  createLigneTransfertStockFormGroup(ligneTransfertStock?: LigneTransfertStockFormGroupInput): LigneTransfertStockFormGroup {
    const ligneTransfertStockRawValue = {
      ...this.getFormDefaults(),
      ...(ligneTransfertStock ?? { id: null }),
    };
    return new FormGroup<LigneTransfertStockFormGroupContent>({
      id: new FormControl(
        { value: ligneTransfertStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(ligneTransfertStockRawValue.quantite, {
        validators: [Validators.required, Validators.min(0)],
      }),
      commentaire: new FormControl(ligneTransfertStockRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      transfert: new FormControl(ligneTransfertStockRawValue.transfert, {
        validators: [Validators.required],
      }),
      produit: new FormControl(ligneTransfertStockRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneTransfertStock(form: LigneTransfertStockFormGroup): ILigneTransfertStock | NewLigneTransfertStock {
    return form.getRawValue() as ILigneTransfertStock | NewLigneTransfertStock;
  }

  resetForm(form: LigneTransfertStockFormGroup, ligneTransfertStock: LigneTransfertStockFormGroupInput): void {
    const ligneTransfertStockRawValue = { ...this.getFormDefaults(), ...ligneTransfertStock };
    form.reset({
      ...ligneTransfertStockRawValue,
      id: { value: ligneTransfertStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneTransfertStockFormDefaults {
    return {
      id: null,
    };
  }
}
