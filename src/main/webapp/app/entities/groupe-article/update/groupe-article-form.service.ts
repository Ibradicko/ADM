import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IGroupeArticle, NewGroupeArticle } from '../groupe-article.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGroupeArticle for edit and NewGroupeArticleFormGroupInput for create.
 */
type GroupeArticleFormGroupInput = IGroupeArticle | PartialWithRequiredKeyOf<NewGroupeArticle>;

type GroupeArticleFormDefaults = Pick<NewGroupeArticle, 'id'>;

type GroupeArticleFormGroupContent = {
  id: FormControl<IGroupeArticle['id'] | NewGroupeArticle['id']>;
  code: FormControl<IGroupeArticle['code']>;
  libelle: FormControl<IGroupeArticle['libelle']>;
  statut: FormControl<IGroupeArticle['statut']>;
  boutique: FormControl<IGroupeArticle['boutique']>;
};

export type GroupeArticleFormGroup = FormGroup<GroupeArticleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GroupeArticleFormService {
  createGroupeArticleFormGroup(groupeArticle?: GroupeArticleFormGroupInput): GroupeArticleFormGroup {
    const groupeArticleRawValue = {
      ...this.getFormDefaults(),
      ...(groupeArticle ?? { id: null }),
    };
    return new FormGroup<GroupeArticleFormGroupContent>({
      id: new FormControl(
        { value: groupeArticleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(groupeArticleRawValue.code, {
        validators: [Validators.required, Validators.maxLength(30)],
      }),
      libelle: new FormControl(groupeArticleRawValue.libelle, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      statut: new FormControl(groupeArticleRawValue.statut, {
        validators: [Validators.required],
      }),
      boutique: new FormControl(groupeArticleRawValue.boutique, {
        validators: [Validators.required],
      }),
    });
  }

  getGroupeArticle(form: GroupeArticleFormGroup): IGroupeArticle | NewGroupeArticle {
    return form.getRawValue() as IGroupeArticle | NewGroupeArticle;
  }

  resetForm(form: GroupeArticleFormGroup, groupeArticle: GroupeArticleFormGroupInput): void {
    const groupeArticleRawValue = { ...this.getFormDefaults(), ...groupeArticle };
    form.reset({
      ...groupeArticleRawValue,
      id: { value: groupeArticleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): GroupeArticleFormDefaults {
    return {
      id: null,
    };
  }
}
