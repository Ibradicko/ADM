import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../regularisation-redevance.test-samples';

import { RegularisationRedevanceFormService } from './regularisation-redevance-form.service';

describe('RegularisationRedevance Form Service', () => {
  let service: RegularisationRedevanceFormService;

  beforeEach(() => {
    service = TestBed.inject(RegularisationRedevanceFormService);
  });

  describe('Service methods', () => {
    describe('createRegularisationRedevanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            montant: expect.any(Object),
            motif: expect.any(Object),
            dateRegularisation: expect.any(Object),
            calcul: expect.any(Object),
          }),
        );
      });

      it('passing IRegularisationRedevance should create a new form with FormGroup', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            montant: expect.any(Object),
            motif: expect.any(Object),
            dateRegularisation: expect.any(Object),
            calcul: expect.any(Object),
          }),
        );
      });
    });

    describe('getRegularisationRedevance', () => {
      it('should return NewRegularisationRedevance for default RegularisationRedevance initial value', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup(sampleWithNewData);

        const regularisationRedevance = service.getRegularisationRedevance(formGroup);

        expect(regularisationRedevance).toMatchObject(sampleWithNewData);
      });

      it('should return NewRegularisationRedevance for empty RegularisationRedevance initial value', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup();

        const regularisationRedevance = service.getRegularisationRedevance(formGroup);

        expect(regularisationRedevance).toMatchObject({});
      });

      it('should return IRegularisationRedevance', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup(sampleWithRequiredData);

        const regularisationRedevance = service.getRegularisationRedevance(formGroup);

        expect(regularisationRedevance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRegularisationRedevance should not enable id FormControl', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRegularisationRedevance should disable id FormControl', () => {
        const formGroup = service.createRegularisationRedevanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
