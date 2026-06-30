import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICodeBarresProduit, NewCodeBarresProduit } from '../code-barres-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICodeBarresProduit for edit and NewCodeBarresProduitFormGroupInput for create.
 */
type CodeBarresProduitFormGroupInput = ICodeBarresProduit | PartialWithRequiredKeyOf<NewCodeBarresProduit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICodeBarresProduit | NewCodeBarresProduit> = Omit<T, 'dateAffectation'> & {
  dateAffectation?: string | null;
};

type CodeBarresProduitFormRawValue = FormValueOf<ICodeBarresProduit>;

type NewCodeBarresProduitFormRawValue = FormValueOf<NewCodeBarresProduit>;

type CodeBarresProduitFormDefaults = Pick<NewCodeBarresProduit, 'id' | 'principal' | 'genereParSysteme' | 'actif' | 'dateAffectation'>;

type CodeBarresProduitFormGroupContent = {
  id: FormControl<CodeBarresProduitFormRawValue['id'] | NewCodeBarresProduit['id']>;
  code: FormControl<CodeBarresProduitFormRawValue['code']>;
  type: FormControl<CodeBarresProduitFormRawValue['type']>;
  principal: FormControl<CodeBarresProduitFormRawValue['principal']>;
  genereParSysteme: FormControl<CodeBarresProduitFormRawValue['genereParSysteme']>;
  actif: FormControl<CodeBarresProduitFormRawValue['actif']>;
  dateAffectation: FormControl<CodeBarresProduitFormRawValue['dateAffectation']>;
  produit: FormControl<CodeBarresProduitFormRawValue['produit']>;
};

export type CodeBarresProduitFormGroup = FormGroup<CodeBarresProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CodeBarresProduitFormService {
  createCodeBarresProduitFormGroup(codeBarresProduit?: CodeBarresProduitFormGroupInput): CodeBarresProduitFormGroup {
    const codeBarresProduitRawValue = this.convertCodeBarresProduitToCodeBarresProduitRawValue({
      ...this.getFormDefaults(),
      ...(codeBarresProduit ?? { id: null }),
    });
    return new FormGroup<CodeBarresProduitFormGroupContent>({
      id: new FormControl(
        { value: codeBarresProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(codeBarresProduitRawValue.code, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      type: new FormControl(codeBarresProduitRawValue.type, {
        validators: [Validators.required],
      }),
      principal: new FormControl(codeBarresProduitRawValue.principal, {
        validators: [Validators.required],
      }),
      genereParSysteme: new FormControl(codeBarresProduitRawValue.genereParSysteme, {
        validators: [Validators.required],
      }),
      actif: new FormControl(codeBarresProduitRawValue.actif, {
        validators: [Validators.required],
      }),
      dateAffectation: new FormControl(codeBarresProduitRawValue.dateAffectation, {
        validators: [Validators.required],
      }),
      produit: new FormControl(codeBarresProduitRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getCodeBarresProduit(form: CodeBarresProduitFormGroup): ICodeBarresProduit | NewCodeBarresProduit {
    return this.convertCodeBarresProduitRawValueToCodeBarresProduit(
      form.getRawValue() as CodeBarresProduitFormRawValue | NewCodeBarresProduitFormRawValue,
    );
  }

  resetForm(form: CodeBarresProduitFormGroup, codeBarresProduit: CodeBarresProduitFormGroupInput): void {
    const codeBarresProduitRawValue = this.convertCodeBarresProduitToCodeBarresProduitRawValue({
      ...this.getFormDefaults(),
      ...codeBarresProduit,
    });
    form.reset({
      ...codeBarresProduitRawValue,
      id: { value: codeBarresProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CodeBarresProduitFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      principal: false,
      genereParSysteme: false,
      actif: false,
      dateAffectation: currentTime,
    };
  }

  private convertCodeBarresProduitRawValueToCodeBarresProduit(
    rawCodeBarresProduit: CodeBarresProduitFormRawValue | NewCodeBarresProduitFormRawValue,
  ): ICodeBarresProduit | NewCodeBarresProduit {
    return {
      ...rawCodeBarresProduit,
      dateAffectation: dayjs(rawCodeBarresProduit.dateAffectation, DATE_TIME_FORMAT),
    };
  }

  private convertCodeBarresProduitToCodeBarresProduitRawValue(
    codeBarresProduit: ICodeBarresProduit | (Partial<NewCodeBarresProduit> & CodeBarresProduitFormDefaults),
  ): CodeBarresProduitFormRawValue | PartialWithRequiredKeyOf<NewCodeBarresProduitFormRawValue> {
    return {
      ...codeBarresProduit,
      dateAffectation: codeBarresProduit.dateAffectation ? codeBarresProduit.dateAffectation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
