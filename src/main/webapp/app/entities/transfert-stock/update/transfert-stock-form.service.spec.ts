import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../transfert-stock.test-samples';

import { TransfertStockFormService } from './transfert-stock-form.service';

describe('TransfertStock Form Service', () => {
  let service: TransfertStockFormService;

  beforeEach(() => {
    service = TestBed.inject(TransfertStockFormService);
  });

  describe('Service methods', () => {
    describe('createTransfertStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTransfertStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateTransfert: expect.any(Object),
            statut: expect.any(Object),
            motif: expect.any(Object),
            boutiqueOrigine: expect.any(Object),
            boutiqueDestination: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing ITransfertStock should create a new form with FormGroup', () => {
        const formGroup = service.createTransfertStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateTransfert: expect.any(Object),
            statut: expect.any(Object),
            motif: expect.any(Object),
            boutiqueOrigine: expect.any(Object),
            boutiqueDestination: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getTransfertStock', () => {
      it('should return NewTransfertStock for default TransfertStock initial value', () => {
        const formGroup = service.createTransfertStockFormGroup(sampleWithNewData);

        const transfertStock = service.getTransfertStock(formGroup);

        expect(transfertStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewTransfertStock for empty TransfertStock initial value', () => {
        const formGroup = service.createTransfertStockFormGroup();

        const transfertStock = service.getTransfertStock(formGroup);

        expect(transfertStock).toMatchObject({});
      });

      it('should return ITransfertStock', () => {
        const formGroup = service.createTransfertStockFormGroup(sampleWithRequiredData);

        const transfertStock = service.getTransfertStock(formGroup);

        expect(transfertStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITransfertStock should not enable id FormControl', () => {
        const formGroup = service.createTransfertStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTransfertStock should disable id FormControl', () => {
        const formGroup = service.createTransfertStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
