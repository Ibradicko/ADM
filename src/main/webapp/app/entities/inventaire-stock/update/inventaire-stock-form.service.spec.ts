import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../inventaire-stock.test-samples';

import { InventaireStockFormService } from './inventaire-stock-form.service';

describe('InventaireStock Form Service', () => {
  let service: InventaireStockFormService;

  beforeEach(() => {
    service = TestBed.inject(InventaireStockFormService);
  });

  describe('Service methods', () => {
    describe('createInventaireStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInventaireStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeInventaire: expect.any(Object),
            statut: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            boutique: expect.any(Object),
            depot: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IInventaireStock should create a new form with FormGroup', () => {
        const formGroup = service.createInventaireStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeInventaire: expect.any(Object),
            statut: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            boutique: expect.any(Object),
            depot: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getInventaireStock', () => {
      it('should return NewInventaireStock for default InventaireStock initial value', () => {
        const formGroup = service.createInventaireStockFormGroup(sampleWithNewData);

        const inventaireStock = service.getInventaireStock(formGroup);

        expect(inventaireStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewInventaireStock for empty InventaireStock initial value', () => {
        const formGroup = service.createInventaireStockFormGroup();

        const inventaireStock = service.getInventaireStock(formGroup);

        expect(inventaireStock).toMatchObject({});
      });

      it('should return IInventaireStock', () => {
        const formGroup = service.createInventaireStockFormGroup(sampleWithRequiredData);

        const inventaireStock = service.getInventaireStock(formGroup);

        expect(inventaireStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInventaireStock should not enable id FormControl', () => {
        const formGroup = service.createInventaireStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInventaireStock should disable id FormControl', () => {
        const formGroup = service.createInventaireStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
