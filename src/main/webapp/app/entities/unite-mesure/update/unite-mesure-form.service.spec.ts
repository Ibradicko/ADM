import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../unite-mesure.test-samples';

import { UniteMesureFormService } from './unite-mesure-form.service';

describe('UniteMesure Form Service', () => {
  let service: UniteMesureFormService;

  beforeEach(() => {
    service = TestBed.inject(UniteMesureFormService);
  });

  describe('Service methods', () => {
    describe('createUniteMesureFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUniteMesureFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            symbole: expect.any(Object),
          }),
        );
      });

      it('passing IUniteMesure should create a new form with FormGroup', () => {
        const formGroup = service.createUniteMesureFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            symbole: expect.any(Object),
          }),
        );
      });
    });

    describe('getUniteMesure', () => {
      it('should return NewUniteMesure for default UniteMesure initial value', () => {
        const formGroup = service.createUniteMesureFormGroup(sampleWithNewData);

        const uniteMesure = service.getUniteMesure(formGroup);

        expect(uniteMesure).toMatchObject(sampleWithNewData);
      });

      it('should return NewUniteMesure for empty UniteMesure initial value', () => {
        const formGroup = service.createUniteMesureFormGroup();

        const uniteMesure = service.getUniteMesure(formGroup);

        expect(uniteMesure).toMatchObject({});
      });

      it('should return IUniteMesure', () => {
        const formGroup = service.createUniteMesureFormGroup(sampleWithRequiredData);

        const uniteMesure = service.getUniteMesure(formGroup);

        expect(uniteMesure).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUniteMesure should not enable id FormControl', () => {
        const formGroup = service.createUniteMesureFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUniteMesure should disable id FormControl', () => {
        const formGroup = service.createUniteMesureFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
