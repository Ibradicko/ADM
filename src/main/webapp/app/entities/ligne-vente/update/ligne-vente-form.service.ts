import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneVente, NewLigneVente } from '../ligne-vente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneVente for edit and NewLigneVenteFormGroupInput for create.
 */
type LigneVenteFormGroupInput = ILigneVente | PartialWithRequiredKeyOf<NewLigneVente>;

type LigneVenteFormDefaults = Pick<NewLigneVente, 'id'>;

type LigneVenteFormGroupContent = {
  id: FormControl<ILigneVente['id'] | NewLigneVente['id']>;
  quantite: FormControl<ILigneVente['quantite']>;
  prixUnitaire: FormControl<ILigneVente['prixUnitaire']>;
  remise: FormControl<ILigneVente['remise']>;
  montantLigne: FormControl<ILigneVente['montantLigne']>;
  codeBarresScanne: FormControl<ILigneVente['codeBarresScanne']>;
  vente: FormControl<ILigneVente['vente']>;
  produit: FormControl<ILigneVente['produit']>;
};

export type LigneVenteFormGroup = FormGroup<LigneVenteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneVenteFormService {
  createLigneVenteFormGroup(ligneVente?: LigneVenteFormGroupInput): LigneVenteFormGroup {
    const ligneVenteRawValue = {
      ...this.getFormDefaults(),
      ...(ligneVente ?? { id: null }),
    };
    return new FormGroup<LigneVenteFormGroupContent>({
      id: new FormControl(
        { value: ligneVenteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(ligneVenteRawValue.quantite, {
        validators: [Validators.required, Validators.min(0)],
      }),
      prixUnitaire: new FormControl(ligneVenteRawValue.prixUnitaire, {
        validators: [Validators.required, Validators.min(0)],
      }),
      remise: new FormControl(ligneVenteRawValue.remise, {
        validators: [Validators.min(0)],
      }),
      montantLigne: new FormControl(ligneVenteRawValue.montantLigne, {
        validators: [Validators.required, Validators.min(0)],
      }),
      codeBarresScanne: new FormControl(ligneVenteRawValue.codeBarresScanne, {
        validators: [Validators.maxLength(80)],
      }),
      vente: new FormControl(ligneVenteRawValue.vente, {
        validators: [Validators.required],
      }),
      produit: new FormControl(ligneVenteRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneVente(form: LigneVenteFormGroup): ILigneVente | NewLigneVente {
    return form.getRawValue() as ILigneVente | NewLigneVente;
  }

  resetForm(form: LigneVenteFormGroup, ligneVente: LigneVenteFormGroupInput): void {
    const ligneVenteRawValue = { ...this.getFormDefaults(), ...ligneVente };
    form.reset({
      ...ligneVenteRawValue,
      id: { value: ligneVenteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneVenteFormDefaults {
    return {
      id: null,
    };
  }
}
