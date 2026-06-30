import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reception-produit.test-samples';

import { ReceptionProduitFormService } from './reception-produit-form.service';

describe('ReceptionProduit Form Service', () => {
  let service: ReceptionProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(ReceptionProduitFormService);
  });

  describe('Service methods', () => {
    describe('createReceptionProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReceptionProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateReception: expect.any(Object),
            fournisseur: expect.any(Object),
            commentaire: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IReceptionProduit should create a new form with FormGroup', () => {
        const formGroup = service.createReceptionProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateReception: expect.any(Object),
            fournisseur: expect.any(Object),
            commentaire: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getReceptionProduit', () => {
      it('should return NewReceptionProduit for default ReceptionProduit initial value', () => {
        const formGroup = service.createReceptionProduitFormGroup(sampleWithNewData);

        const receptionProduit = service.getReceptionProduit(formGroup);

        expect(receptionProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewReceptionProduit for empty ReceptionProduit initial value', () => {
        const formGroup = service.createReceptionProduitFormGroup();

        const receptionProduit = service.getReceptionProduit(formGroup);

        expect(receptionProduit).toMatchObject({});
      });

      it('should return IReceptionProduit', () => {
        const formGroup = service.createReceptionProduitFormGroup(sampleWithRequiredData);

        const receptionProduit = service.getReceptionProduit(formGroup);

        expect(receptionProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReceptionProduit should not enable id FormControl', () => {
        const formGroup = service.createReceptionProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReceptionProduit should disable id FormControl', () => {
        const formGroup = service.createReceptionProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
