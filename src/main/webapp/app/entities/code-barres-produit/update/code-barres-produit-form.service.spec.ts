import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../code-barres-produit.test-samples';

import { CodeBarresProduitFormService } from './code-barres-produit-form.service';

describe('CodeBarresProduit Form Service', () => {
  let service: CodeBarresProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(CodeBarresProduitFormService);
  });

  describe('Service methods', () => {
    describe('createCodeBarresProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCodeBarresProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            type: expect.any(Object),
            principal: expect.any(Object),
            genereParSysteme: expect.any(Object),
            actif: expect.any(Object),
            dateAffectation: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ICodeBarresProduit should create a new form with FormGroup', () => {
        const formGroup = service.createCodeBarresProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            type: expect.any(Object),
            principal: expect.any(Object),
            genereParSysteme: expect.any(Object),
            actif: expect.any(Object),
            dateAffectation: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getCodeBarresProduit', () => {
      it('should return NewCodeBarresProduit for default CodeBarresProduit initial value', () => {
        const formGroup = service.createCodeBarresProduitFormGroup(sampleWithNewData);

        const codeBarresProduit = service.getCodeBarresProduit(formGroup);

        expect(codeBarresProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewCodeBarresProduit for empty CodeBarresProduit initial value', () => {
        const formGroup = service.createCodeBarresProduitFormGroup();

        const codeBarresProduit = service.getCodeBarresProduit(formGroup);

        expect(codeBarresProduit).toMatchObject({});
      });

      it('should return ICodeBarresProduit', () => {
        const formGroup = service.createCodeBarresProduitFormGroup(sampleWithRequiredData);

        const codeBarresProduit = service.getCodeBarresProduit(formGroup);

        expect(codeBarresProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICodeBarresProduit should not enable id FormControl', () => {
        const formGroup = service.createCodeBarresProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCodeBarresProduit should disable id FormControl', () => {
        const formGroup = service.createCodeBarresProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
