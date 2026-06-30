import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../scan-inconnu.test-samples';

import { ScanInconnuFormService } from './scan-inconnu-form.service';

describe('ScanInconnu Form Service', () => {
  let service: ScanInconnuFormService;

  beforeEach(() => {
    service = TestBed.inject(ScanInconnuFormService);
  });

  describe('Service methods', () => {
    describe('createScanInconnuFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createScanInconnuFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            codeScanne: expect.any(Object),
            ecranOrigine: expect.any(Object),
            dateScan: expect.any(Object),
            commentaire: expect.any(Object),
            resolu: expect.any(Object),
            boutique: expect.any(Object),
            produitAffecte: expect.any(Object),
          }),
        );
      });

      it('passing IScanInconnu should create a new form with FormGroup', () => {
        const formGroup = service.createScanInconnuFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            codeScanne: expect.any(Object),
            ecranOrigine: expect.any(Object),
            dateScan: expect.any(Object),
            commentaire: expect.any(Object),
            resolu: expect.any(Object),
            boutique: expect.any(Object),
            produitAffecte: expect.any(Object),
          }),
        );
      });
    });

    describe('getScanInconnu', () => {
      it('should return NewScanInconnu for default ScanInconnu initial value', () => {
        const formGroup = service.createScanInconnuFormGroup(sampleWithNewData);

        const scanInconnu = service.getScanInconnu(formGroup);

        expect(scanInconnu).toMatchObject(sampleWithNewData);
      });

      it('should return NewScanInconnu for empty ScanInconnu initial value', () => {
        const formGroup = service.createScanInconnuFormGroup();

        const scanInconnu = service.getScanInconnu(formGroup);

        expect(scanInconnu).toMatchObject({});
      });

      it('should return IScanInconnu', () => {
        const formGroup = service.createScanInconnuFormGroup(sampleWithRequiredData);

        const scanInconnu = service.getScanInconnu(formGroup);

        expect(scanInconnu).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IScanInconnu should not enable id FormControl', () => {
        const formGroup = service.createScanInconnuFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewScanInconnu should disable id FormControl', () => {
        const formGroup = service.createScanInconnuFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
