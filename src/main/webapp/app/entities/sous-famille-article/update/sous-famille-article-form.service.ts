import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISousFamilleArticle, NewSousFamilleArticle } from '../sous-famille-article.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISousFamilleArticle for edit and NewSousFamilleArticleFormGroupInput for create.
 */
type SousFamilleArticleFormGroupInput = ISousFamilleArticle | PartialWithRequiredKeyOf<NewSousFamilleArticle>;

type SousFamilleArticleFormDefaults = Pick<NewSousFamilleArticle, 'id'>;

type SousFamilleArticleFormGroupContent = {
  id: FormControl<ISousFamilleArticle['id'] | NewSousFamilleArticle['id']>;
  code: FormControl<ISousFamilleArticle['code']>;
  libelle: FormControl<ISousFamilleArticle['libelle']>;
  statut: FormControl<ISousFamilleArticle['statut']>;
  familleArticle: FormControl<ISousFamilleArticle['familleArticle']>;
};

export type SousFamilleArticleFormGroup = FormGroup<SousFamilleArticleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SousFamilleArticleFormService {
  createSousFamilleArticleFormGroup(sousFamilleArticle?: SousFamilleArticleFormGroupInput): SousFamilleArticleFormGroup {
    const sousFamilleArticleRawValue = {
      ...this.getFormDefaults(),
      ...(sousFamilleArticle ?? { id: null }),
    };
    return new FormGroup<SousFamilleArticleFormGroupContent>({
      id: new FormControl(
        { value: sousFamilleArticleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(sousFamilleArticleRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      libelle: new FormControl(sousFamilleArticleRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      statut: new FormControl(sousFamilleArticleRawValue.statut, {
        validators: [Validators.required],
      }),
      familleArticle: new FormControl(sousFamilleArticleRawValue.familleArticle, {
        validators: [Validators.required],
      }),
    });
  }

  getSousFamilleArticle(form: SousFamilleArticleFormGroup): ISousFamilleArticle | NewSousFamilleArticle {
    return form.getRawValue() as ISousFamilleArticle | NewSousFamilleArticle;
  }

  resetForm(form: SousFamilleArticleFormGroup, sousFamilleArticle: SousFamilleArticleFormGroupInput): void {
    const sousFamilleArticleRawValue = { ...this.getFormDefaults(), ...sousFamilleArticle };
    form.reset({
      ...sousFamilleArticleRawValue,
      id: { value: sousFamilleArticleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SousFamilleArticleFormDefaults {
    return {
      id: null,
    };
  }
}
