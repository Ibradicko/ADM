import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mode-paiement-ref.test-samples';

import { ModePaiementRefFormService } from './mode-paiement-ref-form.service';

describe('ModePaiementRef Form Service', () => {
  let service: ModePaiementRefFormService;

  beforeEach(() => {
    service = TestBed.inject(ModePaiementRefFormService);
  });

  describe('Service methods', () => {
    describe('createModePaiementRefFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createModePaiementRefFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });

      it('passing IModePaiementRef should create a new form with FormGroup', () => {
        const formGroup = service.createModePaiementRefFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });
    });

    describe('getModePaiementRef', () => {
      it('should return NewModePaiementRef for default ModePaiementRef initial value', () => {
        const formGroup = service.createModePaiementRefFormGroup(sampleWithNewData);

        const modePaiementRef = service.getModePaiementRef(formGroup);

        expect(modePaiementRef).toMatchObject(sampleWithNewData);
      });

      it('should return NewModePaiementRef for empty ModePaiementRef initial value', () => {
        const formGroup = service.createModePaiementRefFormGroup();

        const modePaiementRef = service.getModePaiementRef(formGroup);

        expect(modePaiementRef).toMatchObject({});
      });

      it('should return IModePaiementRef', () => {
        const formGroup = service.createModePaiementRefFormGroup(sampleWithRequiredData);

        const modePaiementRef = service.getModePaiementRef(formGroup);

        expect(modePaiementRef).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IModePaiementRef should not enable id FormControl', () => {
        const formGroup = service.createModePaiementRefFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewModePaiementRef should disable id FormControl', () => {
        const formGroup = service.createModePaiementRefFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
