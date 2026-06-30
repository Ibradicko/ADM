import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneMouvementStock, NewLigneMouvementStock } from '../ligne-mouvement-stock.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneMouvementStock for edit and NewLigneMouvementStockFormGroupInput for create.
 */
type LigneMouvementStockFormGroupInput = ILigneMouvementStock | PartialWithRequiredKeyOf<NewLigneMouvementStock>;

type LigneMouvementStockFormDefaults = Pick<NewLigneMouvementStock, 'id'>;

type LigneMouvementStockFormGroupContent = {
  id: FormControl<ILigneMouvementStock['id'] | NewLigneMouvementStock['id']>;
  quantite: FormControl<ILigneMouvementStock['quantite']>;
  stockAvant: FormControl<ILigneMouvementStock['stockAvant']>;
  stockApres: FormControl<ILigneMouvementStock['stockApres']>;
  commentaire: FormControl<ILigneMouvementStock['commentaire']>;
  mouvement: FormControl<ILigneMouvementStock['mouvement']>;
  produit: FormControl<ILigneMouvementStock['produit']>;
  depot: FormControl<ILigneMouvementStock['depot']>;
};

export type LigneMouvementStockFormGroup = FormGroup<LigneMouvementStockFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneMouvementStockFormService {
  createLigneMouvementStockFormGroup(ligneMouvementStock?: LigneMouvementStockFormGroupInput): LigneMouvementStockFormGroup {
    const ligneMouvementStockRawValue = {
      ...this.getFormDefaults(),
      ...(ligneMouvementStock ?? { id: null }),
    };
    return new FormGroup<LigneMouvementStockFormGroupContent>({
      id: new FormControl(
        { value: ligneMouvementStockRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(ligneMouvementStockRawValue.quantite, {
        validators: [Validators.required, Validators.min(0)],
      }),
      stockAvant: new FormControl(ligneMouvementStockRawValue.stockAvant),
      stockApres: new FormControl(ligneMouvementStockRawValue.stockApres),
      commentaire: new FormControl(ligneMouvementStockRawValue.commentaire, {
        validators: [Validators.maxLength(255)],
      }),
      mouvement: new FormControl(ligneMouvementStockRawValue.mouvement, {
        validators: [Validators.required],
      }),
      produit: new FormControl(ligneMouvementStockRawValue.produit, {
        validators: [Validators.required],
      }),
      depot: new FormControl(ligneMouvementStockRawValue.depot, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneMouvementStock(form: LigneMouvementStockFormGroup): ILigneMouvementStock | NewLigneMouvementStock {
    return form.getRawValue() as ILigneMouvementStock | NewLigneMouvementStock;
  }

  resetForm(form: LigneMouvementStockFormGroup, ligneMouvementStock: LigneMouvementStockFormGroupInput): void {
    const ligneMouvementStockRawValue = { ...this.getFormDefaults(), ...ligneMouvementStock };
    form.reset({
      ...ligneMouvementStockRawValue,
      id: { value: ligneMouvementStockRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneMouvementStockFormDefaults {
    return {
      id: null,
    };
  }
}
