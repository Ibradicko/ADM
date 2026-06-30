import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../operation-corrective-vente.test-samples';

import { OperationCorrectiveVenteFormService } from './operation-corrective-vente-form.service';

describe('OperationCorrectiveVente Form Service', () => {
  let service: OperationCorrectiveVenteFormService;

  beforeEach(() => {
    service = TestBed.inject(OperationCorrectiveVenteFormService);
  });

  describe('Service methods', () => {
    describe('createOperationCorrectiveVenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            typeOperation: expect.any(Object),
            motif: expect.any(Object),
            montantImpact: expect.any(Object),
            dateOperation: expect.any(Object),
            vente: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IOperationCorrectiveVente should create a new form with FormGroup', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            typeOperation: expect.any(Object),
            motif: expect.any(Object),
            montantImpact: expect.any(Object),
            dateOperation: expect.any(Object),
            vente: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getOperationCorrectiveVente', () => {
      it('should return NewOperationCorrectiveVente for default OperationCorrectiveVente initial value', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup(sampleWithNewData);

        const operationCorrectiveVente = service.getOperationCorrectiveVente(formGroup);

        expect(operationCorrectiveVente).toMatchObject(sampleWithNewData);
      });

      it('should return NewOperationCorrectiveVente for empty OperationCorrectiveVente initial value', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup();

        const operationCorrectiveVente = service.getOperationCorrectiveVente(formGroup);

        expect(operationCorrectiveVente).toMatchObject({});
      });

      it('should return IOperationCorrectiveVente', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup(sampleWithRequiredData);

        const operationCorrectiveVente = service.getOperationCorrectiveVente(formGroup);

        expect(operationCorrectiveVente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOperationCorrectiveVente should not enable id FormControl', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOperationCorrectiveVente should disable id FormControl', () => {
        const formGroup = service.createOperationCorrectiveVenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
