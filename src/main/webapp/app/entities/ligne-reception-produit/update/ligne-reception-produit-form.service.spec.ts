import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-reception-produit.test-samples';

import { LigneReceptionProduitFormService } from './ligne-reception-produit-form.service';

describe('LigneReceptionProduit Form Service', () => {
  let service: LigneReceptionProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneReceptionProduitFormService);
  });

  describe('Service methods', () => {
    describe('createLigneReceptionProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteAttendue: expect.any(Object),
            quantiteRecue: expect.any(Object),
            ecart: expect.any(Object),
            codeBarresScanne: expect.any(Object),
            reception: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ILigneReceptionProduit should create a new form with FormGroup', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteAttendue: expect.any(Object),
            quantiteRecue: expect.any(Object),
            ecart: expect.any(Object),
            codeBarresScanne: expect.any(Object),
            reception: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneReceptionProduit', () => {
      it('should return NewLigneReceptionProduit for default LigneReceptionProduit initial value', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup(sampleWithNewData);

        const ligneReceptionProduit = service.getLigneReceptionProduit(formGroup);

        expect(ligneReceptionProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneReceptionProduit for empty LigneReceptionProduit initial value', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup();

        const ligneReceptionProduit = service.getLigneReceptionProduit(formGroup);

        expect(ligneReceptionProduit).toMatchObject({});
      });

      it('should return ILigneReceptionProduit', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup(sampleWithRequiredData);

        const ligneReceptionProduit = service.getLigneReceptionProduit(formGroup);

        expect(ligneReceptionProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneReceptionProduit should not enable id FormControl', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneReceptionProduit should disable id FormControl', () => {
        const formGroup = service.createLigneReceptionProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
