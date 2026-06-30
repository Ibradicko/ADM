import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sous-famille-article.test-samples';

import { SousFamilleArticleFormService } from './sous-famille-article-form.service';

describe('SousFamilleArticle Form Service', () => {
  let service: SousFamilleArticleFormService;

  beforeEach(() => {
    service = TestBed.inject(SousFamilleArticleFormService);
  });

  describe('Service methods', () => {
    describe('createSousFamilleArticleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSousFamilleArticleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            familleArticle: expect.any(Object),
          }),
        );
      });

      it('passing ISousFamilleArticle should create a new form with FormGroup', () => {
        const formGroup = service.createSousFamilleArticleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            familleArticle: expect.any(Object),
          }),
        );
      });
    });

    describe('getSousFamilleArticle', () => {
      it('should return NewSousFamilleArticle for default SousFamilleArticle initial value', () => {
        const formGroup = service.createSousFamilleArticleFormGroup(sampleWithNewData);

        const sousFamilleArticle = service.getSousFamilleArticle(formGroup);

        expect(sousFamilleArticle).toMatchObject(sampleWithNewData);
      });

      it('should return NewSousFamilleArticle for empty SousFamilleArticle initial value', () => {
        const formGroup = service.createSousFamilleArticleFormGroup();

        const sousFamilleArticle = service.getSousFamilleArticle(formGroup);

        expect(sousFamilleArticle).toMatchObject({});
      });

      it('should return ISousFamilleArticle', () => {
        const formGroup = service.createSousFamilleArticleFormGroup(sampleWithRequiredData);

        const sousFamilleArticle = service.getSousFamilleArticle(formGroup);

        expect(sousFamilleArticle).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISousFamilleArticle should not enable id FormControl', () => {
        const formGroup = service.createSousFamilleArticleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSousFamilleArticle should disable id FormControl', () => {
        const formGroup = service.createSousFamilleArticleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
