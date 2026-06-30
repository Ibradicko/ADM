import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-transfert-stock.test-samples';

import { LigneTransfertStockFormService } from './ligne-transfert-stock-form.service';

describe('LigneTransfertStock Form Service', () => {
  let service: LigneTransfertStockFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneTransfertStockFormService);
  });

  describe('Service methods', () => {
    describe('createLigneTransfertStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneTransfertStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            commentaire: expect.any(Object),
            transfert: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ILigneTransfertStock should create a new form with FormGroup', () => {
        const formGroup = service.createLigneTransfertStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            commentaire: expect.any(Object),
            transfert: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneTransfertStock', () => {
      it('should return NewLigneTransfertStock for default LigneTransfertStock initial value', () => {
        const formGroup = service.createLigneTransfertStockFormGroup(sampleWithNewData);

        const ligneTransfertStock = service.getLigneTransfertStock(formGroup);

        expect(ligneTransfertStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneTransfertStock for empty LigneTransfertStock initial value', () => {
        const formGroup = service.createLigneTransfertStockFormGroup();

        const ligneTransfertStock = service.getLigneTransfertStock(formGroup);

        expect(ligneTransfertStock).toMatchObject({});
      });

      it('should return ILigneTransfertStock', () => {
        const formGroup = service.createLigneTransfertStockFormGroup(sampleWithRequiredData);

        const ligneTransfertStock = service.getLigneTransfertStock(formGroup);

        expect(ligneTransfertStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneTransfertStock should not enable id FormControl', () => {
        const formGroup = service.createLigneTransfertStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneTransfertStock should disable id FormControl', () => {
        const formGroup = service.createLigneTransfertStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
