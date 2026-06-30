import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-mouvement-stock.test-samples';

import { LigneMouvementStockFormService } from './ligne-mouvement-stock-form.service';

describe('LigneMouvementStock Form Service', () => {
  let service: LigneMouvementStockFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneMouvementStockFormService);
  });

  describe('Service methods', () => {
    describe('createLigneMouvementStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneMouvementStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            stockAvant: expect.any(Object),
            stockApres: expect.any(Object),
            commentaire: expect.any(Object),
            mouvement: expect.any(Object),
            produit: expect.any(Object),
            depot: expect.any(Object),
          }),
        );
      });

      it('passing ILigneMouvementStock should create a new form with FormGroup', () => {
        const formGroup = service.createLigneMouvementStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            stockAvant: expect.any(Object),
            stockApres: expect.any(Object),
            commentaire: expect.any(Object),
            mouvement: expect.any(Object),
            produit: expect.any(Object),
            depot: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneMouvementStock', () => {
      it('should return NewLigneMouvementStock for default LigneMouvementStock initial value', () => {
        const formGroup = service.createLigneMouvementStockFormGroup(sampleWithNewData);

        const ligneMouvementStock = service.getLigneMouvementStock(formGroup);

        expect(ligneMouvementStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneMouvementStock for empty LigneMouvementStock initial value', () => {
        const formGroup = service.createLigneMouvementStockFormGroup();

        const ligneMouvementStock = service.getLigneMouvementStock(formGroup);

        expect(ligneMouvementStock).toMatchObject({});
      });

      it('should return ILigneMouvementStock', () => {
        const formGroup = service.createLigneMouvementStockFormGroup(sampleWithRequiredData);

        const ligneMouvementStock = service.getLigneMouvementStock(formGroup);

        expect(ligneMouvementStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneMouvementStock should not enable id FormControl', () => {
        const formGroup = service.createLigneMouvementStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneMouvementStock should disable id FormControl', () => {
        const formGroup = service.createLigneMouvementStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
