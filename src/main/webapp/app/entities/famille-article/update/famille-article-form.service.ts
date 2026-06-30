import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFamilleArticle, NewFamilleArticle } from '../famille-article.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFamilleArticle for edit and NewFamilleArticleFormGroupInput for create.
 */
type FamilleArticleFormGroupInput = IFamilleArticle | PartialWithRequiredKeyOf<NewFamilleArticle>;

type FamilleArticleFormDefaults = Pick<NewFamilleArticle, 'id'>;

type FamilleArticleFormGroupContent = {
  id: FormControl<IFamilleArticle['id'] | NewFamilleArticle['id']>;
  code: FormControl<IFamilleArticle['code']>;
  libelle: FormControl<IFamilleArticle['libelle']>;
  statut: FormControl<IFamilleArticle['statut']>;
  groupeArticle: FormControl<IFamilleArticle['groupeArticle']>;
};

export type FamilleArticleFormGroup = FormGroup<FamilleArticleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FamilleArticleFormService {
  createFamilleArticleFormGroup(familleArticle?: FamilleArticleFormGroupInput): FamilleArticleFormGroup {
    const familleArticleRawValue = {
      ...this.getFormDefaults(),
      ...(familleArticle ?? { id: null }),
    };
    return new FormGroup<FamilleArticleFormGroupContent>({
      id: new FormControl(
        { value: familleArticleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(familleArticleRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      libelle: new FormControl(familleArticleRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      statut: new FormControl(familleArticleRawValue.statut, {
        validators: [Validators.required],
      }),
      groupeArticle: new FormControl(familleArticleRawValue.groupeArticle, {
        validators: [Validators.required],
      }),
    });
  }

  getFamilleArticle(form: FamilleArticleFormGroup): IFamilleArticle | NewFamilleArticle {
    return form.getRawValue() as IFamilleArticle | NewFamilleArticle;
  }

  resetForm(form: FamilleArticleFormGroup, familleArticle: FamilleArticleFormGroupInput): void {
    const familleArticleRawValue = { ...this.getFormDefaults(), ...familleArticle };
    form.reset({
      ...familleArticleRawValue,
      id: { value: familleArticleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FamilleArticleFormDefaults {
    return {
      id: null,
    };
  }
}
