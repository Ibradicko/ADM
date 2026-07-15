import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProduit, NewProduit } from '../produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduit for edit and NewProduitFormGroupInput for create.
 */
type ProduitFormGroupInput = IProduit | PartialWithRequiredKeyOf<NewProduit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProduit | NewProduit> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

type ProduitFormRawValue = FormValueOf<IProduit>;

type NewProduitFormRawValue = FormValueOf<NewProduit>;

type ProduitFormDefaults = Pick<NewProduit, 'id' | 'dateCreation'>;

type ProduitFormGroupContent = {
  id: FormControl<ProduitFormRawValue['id'] | NewProduit['id']>;
  codeInterne: FormControl<ProduitFormRawValue['codeInterne']>;
  designation: FormControl<ProduitFormRawValue['designation']>;
  description: FormControl<ProduitFormRawValue['description']>;
  image: FormControl<ProduitFormRawValue['image']>;
  imageContentType: FormControl<ProduitFormRawValue['imageContentType']>;
  typePrix: FormControl<ProduitFormRawValue['typePrix']>;
  prixVente: FormControl<ProduitFormRawValue['prixVente']>;
  tauxRedevanceApplicable: FormControl<ProduitFormRawValue['tauxRedevanceApplicable']>;
  statut: FormControl<ProduitFormRawValue['statut']>;
  dateCreation: FormControl<ProduitFormRawValue['dateCreation']>;
  boutique: FormControl<ProduitFormRawValue['boutique']>;
  groupeArticle: FormControl<ProduitFormRawValue['groupeArticle']>;
  familleArticle: FormControl<ProduitFormRawValue['familleArticle']>;
  sousFamilleArticle: FormControl<ProduitFormRawValue['sousFamilleArticle']>;
  uniteMesure: FormControl<ProduitFormRawValue['uniteMesure']>;
};

export type ProduitFormGroup = FormGroup<ProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProduitFormService {
  createProduitFormGroup(produit?: ProduitFormGroupInput): ProduitFormGroup {
    const produitRawValue = this.convertProduitToProduitRawValue({
      ...this.getFormDefaults(),
      ...(produit ?? { id: null }),
    });
    return new FormGroup<ProduitFormGroupContent>({
      id: new FormControl(
        { value: produitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      codeInterne: new FormControl(produitRawValue.codeInterne, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      designation: new FormControl(produitRawValue.designation, {
        validators: [Validators.required, Validators.maxLength(200)],
      }),
      description: new FormControl(produitRawValue.description),
      image: new FormControl(produitRawValue.image),
      imageContentType: new FormControl(produitRawValue.imageContentType),
      typePrix: new FormControl(produitRawValue.typePrix, {
        validators: [Validators.required],
      }),
      prixVente: new FormControl(produitRawValue.prixVente, {
        validators: [Validators.required, Validators.min(0)],
      }),
      tauxRedevanceApplicable: new FormControl(produitRawValue.tauxRedevanceApplicable, {
        validators: [Validators.min(0), Validators.max(100)],
      }),
      statut: new FormControl(produitRawValue.statut, {
        validators: [Validators.required],
      }),
      dateCreation: new FormControl(produitRawValue.dateCreation, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(produitRawValue.boutique, {
        validators: [Validators.required],
      }),
      groupeArticle: new FormControl(produitRawValue.groupeArticle, {
        validators: [Validators.required],
      }),
      familleArticle: new FormControl(produitRawValue.familleArticle),
      sousFamilleArticle: new FormControl(produitRawValue.sousFamilleArticle),
      uniteMesure: new FormControl(produitRawValue.uniteMesure, {
        validators: [Validators.required],
      }),
    });
  }

  getProduit(form: ProduitFormGroup): IProduit | NewProduit {
    return this.convertProduitRawValueToProduit(form.getRawValue() as ProduitFormRawValue | NewProduitFormRawValue);
  }

  resetForm(form: ProduitFormGroup, produit: ProduitFormGroupInput): void {
    const produitRawValue = this.convertProduitToProduitRawValue({ ...this.getFormDefaults(), ...produit });
    form.reset({
      ...produitRawValue,
      id: { value: produitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProduitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
    };
  }

  private convertProduitRawValueToProduit(rawProduit: ProduitFormRawValue | NewProduitFormRawValue): IProduit | NewProduit {
    return {
      ...rawProduit,
      dateCreation: dayjs(rawProduit.dateCreation, DATE_TIME_FORMAT),
    };
  }

  private convertProduitToProduitRawValue(
    produit: IProduit | (Partial<NewProduit> & ProduitFormDefaults),
  ): ProduitFormRawValue | PartialWithRequiredKeyOf<NewProduitFormRawValue> {
    return {
      ...produit,
      dateCreation: produit.dateCreation ? produit.dateCreation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
