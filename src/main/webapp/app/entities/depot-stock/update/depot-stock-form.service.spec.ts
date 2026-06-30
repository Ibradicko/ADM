import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../depot-stock.test-samples';

import { DepotStockFormService } from './depot-stock-form.service';

describe('DepotStock Form Service', () => {
  let service: DepotStockFormService;

  beforeEach(() => {
    service = TestBed.inject(DepotStockFormService);
  });

  describe('Service methods', () => {
    describe('createDepotStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDepotStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            emplacement: expect.any(Object),
            actif: expect.any(Object),
            boutique: expect.any(Object),
          }),
        );
      });

      it('passing IDepotStock should create a new form with FormGroup', () => {
        const formGroup = service.createDepotStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            emplacement: expect.any(Object),
            actif: expect.any(Object),
            boutique: expect.any(Object),
          }),
        );
      });
    });

    describe('getDepotStock', () => {
      it('should return NewDepotStock for default DepotStock initial value', () => {
        const formGroup = service.createDepotStockFormGroup(sampleWithNewData);

        const depotStock = service.getDepotStock(formGroup);

        expect(depotStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewDepotStock for empty DepotStock initial value', () => {
        const formGroup = service.createDepotStockFormGroup();

        const depotStock = service.getDepotStock(formGroup);

        expect(depotStock).toMatchObject({});
      });

      it('should return IDepotStock', () => {
        const formGroup = service.createDepotStockFormGroup(sampleWithRequiredData);

        const depotStock = service.getDepotStock(formGroup);

        expect(depotStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDepotStock should not enable id FormControl', () => {
        const formGroup = service.createDepotStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDepotStock should disable id FormControl', () => {
        const formGroup = service.createDepotStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
