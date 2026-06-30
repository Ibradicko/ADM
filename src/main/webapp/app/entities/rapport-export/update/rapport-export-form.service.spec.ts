import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../rapport-export.test-samples';

import { RapportExportFormService } from './rapport-export-form.service';

describe('RapportExport Form Service', () => {
  let service: RapportExportFormService;

  beforeEach(() => {
    service = TestBed.inject(RapportExportFormService);
  });

  describe('Service methods', () => {
    describe('createRapportExportFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRapportExportFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeRapport: expect.any(Object),
            format: expect.any(Object),
            periodeDebut: expect.any(Object),
            periodeFin: expect.any(Object),
            cheminFichier: expect.any(Object),
            dateGeneration: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IRapportExport should create a new form with FormGroup', () => {
        const formGroup = service.createRapportExportFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            typeRapport: expect.any(Object),
            format: expect.any(Object),
            periodeDebut: expect.any(Object),
            periodeFin: expect.any(Object),
            cheminFichier: expect.any(Object),
            dateGeneration: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getRapportExport', () => {
      it('should return NewRapportExport for default RapportExport initial value', () => {
        const formGroup = service.createRapportExportFormGroup(sampleWithNewData);

        const rapportExport = service.getRapportExport(formGroup);

        expect(rapportExport).toMatchObject(sampleWithNewData);
      });

      it('should return NewRapportExport for empty RapportExport initial value', () => {
        const formGroup = service.createRapportExportFormGroup();

        const rapportExport = service.getRapportExport(formGroup);

        expect(rapportExport).toMatchObject({});
      });

      it('should return IRapportExport', () => {
        const formGroup = service.createRapportExportFormGroup(sampleWithRequiredData);

        const rapportExport = service.getRapportExport(formGroup);

        expect(rapportExport).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRapportExport should not enable id FormControl', () => {
        const formGroup = service.createRapportExportFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRapportExport should disable id FormControl', () => {
        const formGroup = service.createRapportExportFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
