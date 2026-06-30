import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-inventaire-stock.test-samples';

import { LigneInventaireStockFormService } from './ligne-inventaire-stock-form.service';

describe('LigneInventaireStock Form Service', () => {
  let service: LigneInventaireStockFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneInventaireStockFormService);
  });

  describe('Service methods', () => {
    describe('createLigneInventaireStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneInventaireStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteTheorique: expect.any(Object),
            quantiteComptee: expect.any(Object),
            ecart: expect.any(Object),
            commentaire: expect.any(Object),
            inventaire: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ILigneInventaireStock should create a new form with FormGroup', () => {
        const formGroup = service.createLigneInventaireStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantiteTheorique: expect.any(Object),
            quantiteComptee: expect.any(Object),
            ecart: expect.any(Object),
            commentaire: expect.any(Object),
            inventaire: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneInventaireStock', () => {
      it('should return NewLigneInventaireStock for default LigneInventaireStock initial value', () => {
        const formGroup = service.createLigneInventaireStockFormGroup(sampleWithNewData);

        const ligneInventaireStock = service.getLigneInventaireStock(formGroup);

        expect(ligneInventaireStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneInventaireStock for empty LigneInventaireStock initial value', () => {
        const formGroup = service.createLigneInventaireStockFormGroup();

        const ligneInventaireStock = service.getLigneInventaireStock(formGroup);

        expect(ligneInventaireStock).toMatchObject({});
      });

      it('should return ILigneInventaireStock', () => {
        const formGroup = service.createLigneInventaireStockFormGroup(sampleWithRequiredData);

        const ligneInventaireStock = service.getLigneInventaireStock(formGroup);

        expect(ligneInventaireStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneInventaireStock should not enable id FormControl', () => {
        const formGroup = service.createLigneInventaireStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneInventaireStock should disable id FormControl', () => {
        const formGroup = service.createLigneInventaireStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
