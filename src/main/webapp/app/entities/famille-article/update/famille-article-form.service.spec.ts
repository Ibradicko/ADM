import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../famille-article.test-samples';

import { FamilleArticleFormService } from './famille-article-form.service';

describe('FamilleArticle Form Service', () => {
  let service: FamilleArticleFormService;

  beforeEach(() => {
    service = TestBed.inject(FamilleArticleFormService);
  });

  describe('Service methods', () => {
    describe('createFamilleArticleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFamilleArticleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            groupeArticle: expect.any(Object),
          }),
        );
      });

      it('passing IFamilleArticle should create a new form with FormGroup', () => {
        const formGroup = service.createFamilleArticleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            groupeArticle: expect.any(Object),
          }),
        );
      });
    });

    describe('getFamilleArticle', () => {
      it('should return NewFamilleArticle for default FamilleArticle initial value', () => {
        const formGroup = service.createFamilleArticleFormGroup(sampleWithNewData);

        const familleArticle = service.getFamilleArticle(formGroup);

        expect(familleArticle).toMatchObject(sampleWithNewData);
      });

      it('should return NewFamilleArticle for empty FamilleArticle initial value', () => {
        const formGroup = service.createFamilleArticleFormGroup();

        const familleArticle = service.getFamilleArticle(formGroup);

        expect(familleArticle).toMatchObject({});
      });

      it('should return IFamilleArticle', () => {
        const formGroup = service.createFamilleArticleFormGroup(sampleWithRequiredData);

        const familleArticle = service.getFamilleArticle(formGroup);

        expect(familleArticle).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFamilleArticle should not enable id FormControl', () => {
        const formGroup = service.createFamilleArticleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFamilleArticle should disable id FormControl', () => {
        const formGroup = service.createFamilleArticleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
