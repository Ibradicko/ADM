import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../calcul-redevance.test-samples';

import { CalculRedevanceFormService } from './calcul-redevance-form.service';

describe('CalculRedevance Form Service', () => {
  let service: CalculRedevanceFormService;

  beforeEach(() => {
    service = TestBed.inject(CalculRedevanceFormService);
  });

  describe('Service methods', () => {
    describe('createCalculRedevanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCalculRedevanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            periodeDebut: expect.any(Object),
            periodeFin: expect.any(Object),
            chiffreAffaires: expect.any(Object),
            montantRedevance: expect.any(Object),
            statut: expect.any(Object),
            dateCalcul: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
          }),
        );
      });

      it('passing ICalculRedevance should create a new form with FormGroup', () => {
        const formGroup = service.createCalculRedevanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            periodeDebut: expect.any(Object),
            periodeFin: expect.any(Object),
            chiffreAffaires: expect.any(Object),
            montantRedevance: expect.any(Object),
            statut: expect.any(Object),
            dateCalcul: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
          }),
        );
      });
    });

    describe('getCalculRedevance', () => {
      it('should return NewCalculRedevance for default CalculRedevance initial value', () => {
        const formGroup = service.createCalculRedevanceFormGroup(sampleWithNewData);

        const calculRedevance = service.getCalculRedevance(formGroup);

        expect(calculRedevance).toMatchObject(sampleWithNewData);
      });

      it('should return NewCalculRedevance for empty CalculRedevance initial value', () => {
        const formGroup = service.createCalculRedevanceFormGroup();

        const calculRedevance = service.getCalculRedevance(formGroup);

        expect(calculRedevance).toMatchObject({});
      });

      it('should return ICalculRedevance', () => {
        const formGroup = service.createCalculRedevanceFormGroup(sampleWithRequiredData);

        const calculRedevance = service.getCalculRedevance(formGroup);

        expect(calculRedevance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICalculRedevance should not enable id FormControl', () => {
        const formGroup = service.createCalculRedevanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCalculRedevance should disable id FormControl', () => {
        const formGroup = service.createCalculRedevanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
