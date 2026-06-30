import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../groupe-article.test-samples';

import { GroupeArticleFormService } from './groupe-article-form.service';

describe('GroupeArticle Form Service', () => {
  let service: GroupeArticleFormService;

  beforeEach(() => {
    service = TestBed.inject(GroupeArticleFormService);
  });

  describe('Service methods', () => {
    describe('createGroupeArticleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGroupeArticleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            boutique: expect.any(Object),
          }),
        );
      });

      it('passing IGroupeArticle should create a new form with FormGroup', () => {
        const formGroup = service.createGroupeArticleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            statut: expect.any(Object),
            boutique: expect.any(Object),
          }),
        );
      });
    });

    describe('getGroupeArticle', () => {
      it('should return NewGroupeArticle for default GroupeArticle initial value', () => {
        const formGroup = service.createGroupeArticleFormGroup(sampleWithNewData);

        const groupeArticle = service.getGroupeArticle(formGroup);

        expect(groupeArticle).toMatchObject(sampleWithNewData);
      });

      it('should return NewGroupeArticle for empty GroupeArticle initial value', () => {
        const formGroup = service.createGroupeArticleFormGroup();

        const groupeArticle = service.getGroupeArticle(formGroup);

        expect(groupeArticle).toMatchObject({});
      });

      it('should return IGroupeArticle', () => {
        const formGroup = service.createGroupeArticleFormGroup(sampleWithRequiredData);

        const groupeArticle = service.getGroupeArticle(formGroup);

        expect(groupeArticle).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGroupeArticle should not enable id FormControl', () => {
        const formGroup = service.createGroupeArticleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGroupeArticle should disable id FormControl', () => {
        const formGroup = service.createGroupeArticleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
