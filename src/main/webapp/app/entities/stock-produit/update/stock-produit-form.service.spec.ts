import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../stock-produit.test-samples';

import { StockProduitFormService } from './stock-produit-form.service';

describe('StockProduit Form Service', () => {
  let service: StockProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(StockProduitFormService);
  });

  describe('Service methods', () => {
    describe('createStockProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStockProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteTheorique: expect.any(Object),
            stockAlerte: expect.any(Object),
            dateDernierMouvement: expect.any(Object),
            produit: expect.any(Object),
            depot: expect.any(Object),
          }),
        );
      });

      it('passing IStockProduit should create a new form with FormGroup', () => {
        const formGroup = service.createStockProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteTheorique: expect.any(Object),
            stockAlerte: expect.any(Object),
            dateDernierMouvement: expect.any(Object),
            produit: expect.any(Object),
            depot: expect.any(Object),
          }),
        );
      });
    });

    describe('getStockProduit', () => {
      it('should return NewStockProduit for default StockProduit initial value', () => {
        const formGroup = service.createStockProduitFormGroup(sampleWithNewData);

        const stockProduit = service.getStockProduit(formGroup);

        expect(stockProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewStockProduit for empty StockProduit initial value', () => {
        const formGroup = service.createStockProduitFormGroup();

        const stockProduit = service.getStockProduit(formGroup);

        expect(stockProduit).toMatchObject({});
      });

      it('should return IStockProduit', () => {
        const formGroup = service.createStockProduitFormGroup(sampleWithRequiredData);

        const stockProduit = service.getStockProduit(formGroup);

        expect(stockProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStockProduit should not enable id FormControl', () => {
        const formGroup = service.createStockProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStockProduit should disable id FormControl', () => {
        const formGroup = service.createStockProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
