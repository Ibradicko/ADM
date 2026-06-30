import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../parametre-global.test-samples';

import { ParametreGlobalFormService } from './parametre-global-form.service';

describe('ParametreGlobal Form Service', () => {
  let service: ParametreGlobalFormService;

  beforeEach(() => {
    service = TestBed.inject(ParametreGlobalFormService);
  });

  describe('Service methods', () => {
    describe('createParametreGlobalFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createParametreGlobalFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            valeur: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });

      it('passing IParametreGlobal should create a new form with FormGroup', () => {
        const formGroup = service.createParametreGlobalFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            valeur: expect.any(Object),
            description: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });
    });

    describe('getParametreGlobal', () => {
      it('should return NewParametreGlobal for default ParametreGlobal initial value', () => {
        const formGroup = service.createParametreGlobalFormGroup(sampleWithNewData);

        const parametreGlobal = service.getParametreGlobal(formGroup);

        expect(parametreGlobal).toMatchObject(sampleWithNewData);
      });

      it('should return NewParametreGlobal for empty ParametreGlobal initial value', () => {
        const formGroup = service.createParametreGlobalFormGroup();

        const parametreGlobal = service.getParametreGlobal(formGroup);

        expect(parametreGlobal).toMatchObject({});
      });

      it('should return IParametreGlobal', () => {
        const formGroup = service.createParametreGlobalFormGroup(sampleWithRequiredData);

        const parametreGlobal = service.getParametreGlobal(formGroup);

        expect(parametreGlobal).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IParametreGlobal should not enable id FormControl', () => {
        const formGroup = service.createParametreGlobalFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewParametreGlobal should disable id FormControl', () => {
        const formGroup = service.createParametreGlobalFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
