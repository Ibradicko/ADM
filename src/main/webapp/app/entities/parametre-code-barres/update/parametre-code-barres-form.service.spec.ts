import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../parametre-code-barres.test-samples';

import { ParametreCodeBarresFormService } from './parametre-code-barres-form.service';

describe('ParametreCodeBarres Form Service', () => {
  let service: ParametreCodeBarresFormService;

  beforeEach(() => {
    service = TestBed.inject(ParametreCodeBarresFormService);
  });

  describe('Service methods', () => {
    describe('createParametreCodeBarresFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createParametreCodeBarresFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            formatParDefaut: expect.any(Object),
            prefixe: expect.any(Object),
            longueur: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });

      it('passing IParametreCodeBarres should create a new form with FormGroup', () => {
        const formGroup = service.createParametreCodeBarresFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            formatParDefaut: expect.any(Object),
            prefixe: expect.any(Object),
            longueur: expect.any(Object),
            actif: expect.any(Object),
          }),
        );
      });
    });

    describe('getParametreCodeBarres', () => {
      it('should return NewParametreCodeBarres for default ParametreCodeBarres initial value', () => {
        const formGroup = service.createParametreCodeBarresFormGroup(sampleWithNewData);

        const parametreCodeBarres = service.getParametreCodeBarres(formGroup);

        expect(parametreCodeBarres).toMatchObject(sampleWithNewData);
      });

      it('should return NewParametreCodeBarres for empty ParametreCodeBarres initial value', () => {
        const formGroup = service.createParametreCodeBarresFormGroup();

        const parametreCodeBarres = service.getParametreCodeBarres(formGroup);

        expect(parametreCodeBarres).toMatchObject({});
      });

      it('should return IParametreCodeBarres', () => {
        const formGroup = service.createParametreCodeBarresFormGroup(sampleWithRequiredData);

        const parametreCodeBarres = service.getParametreCodeBarres(formGroup);

        expect(parametreCodeBarres).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IParametreCodeBarres should not enable id FormControl', () => {
        const formGroup = service.createParametreCodeBarresFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewParametreCodeBarres should disable id FormControl', () => {
        const formGroup = service.createParametreCodeBarresFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
