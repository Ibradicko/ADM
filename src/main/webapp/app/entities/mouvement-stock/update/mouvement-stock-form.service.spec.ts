import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mouvement-stock.test-samples';

import { MouvementStockFormService } from './mouvement-stock-form.service';

describe('MouvementStock Form Service', () => {
  let service: MouvementStockFormService;

  beforeEach(() => {
    service = TestBed.inject(MouvementStockFormService);
  });

  describe('Service methods', () => {
    describe('createMouvementStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMouvementStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeMouvement: expect.any(Object),
            statut: expect.any(Object),
            dateMouvement: expect.any(Object),
            motif: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IMouvementStock should create a new form with FormGroup', () => {
        const formGroup = service.createMouvementStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeMouvement: expect.any(Object),
            statut: expect.any(Object),
            dateMouvement: expect.any(Object),
            motif: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getMouvementStock', () => {
      it('should return NewMouvementStock for default MouvementStock initial value', () => {
        const formGroup = service.createMouvementStockFormGroup(sampleWithNewData);

        const mouvementStock = service.getMouvementStock(formGroup);

        expect(mouvementStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewMouvementStock for empty MouvementStock initial value', () => {
        const formGroup = service.createMouvementStockFormGroup();

        const mouvementStock = service.getMouvementStock(formGroup);

        expect(mouvementStock).toMatchObject({});
      });

      it('should return IMouvementStock', () => {
        const formGroup = service.createMouvementStockFormGroup(sampleWithRequiredData);

        const mouvementStock = service.getMouvementStock(formGroup);

        expect(mouvementStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMouvementStock should not enable id FormControl', () => {
        const formGroup = service.createMouvementStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMouvementStock should disable id FormControl', () => {
        const formGroup = service.createMouvementStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
