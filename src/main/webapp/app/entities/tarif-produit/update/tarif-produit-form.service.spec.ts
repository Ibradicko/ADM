import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tarif-produit.test-samples';

import { TarifProduitFormService } from './tarif-produit-form.service';

describe('TarifProduit Form Service', () => {
  let service: TarifProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(TarifProduitFormService);
  });

  describe('Service methods', () => {
    describe('createTarifProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTarifProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            typePrix: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            actif: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ITarifProduit should create a new form with FormGroup', () => {
        const formGroup = service.createTarifProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            typePrix: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            actif: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getTarifProduit', () => {
      it('should return NewTarifProduit for default TarifProduit initial value', () => {
        const formGroup = service.createTarifProduitFormGroup(sampleWithNewData);

        const tarifProduit = service.getTarifProduit(formGroup);

        expect(tarifProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewTarifProduit for empty TarifProduit initial value', () => {
        const formGroup = service.createTarifProduitFormGroup();

        const tarifProduit = service.getTarifProduit(formGroup);

        expect(tarifProduit).toMatchObject({});
      });

      it('should return ITarifProduit', () => {
        const formGroup = service.createTarifProduitFormGroup(sampleWithRequiredData);

        const tarifProduit = service.getTarifProduit(formGroup);

        expect(tarifProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITarifProduit should not enable id FormControl', () => {
        const formGroup = service.createTarifProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTarifProduit should disable id FormControl', () => {
        const formGroup = service.createTarifProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
