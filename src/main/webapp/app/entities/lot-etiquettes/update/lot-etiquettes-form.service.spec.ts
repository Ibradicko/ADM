import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../lot-etiquettes.test-samples';

import { LotEtiquettesFormService } from './lot-etiquettes-form.service';

describe('LotEtiquettes Form Service', () => {
  let service: LotEtiquettesFormService;

  beforeEach(() => {
    service = TestBed.inject(LotEtiquettesFormService);
  });

  describe('Service methods', () => {
    describe('createLotEtiquettesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLotEtiquettesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateGeneration: expect.any(Object),
            formatImpression: expect.any(Object),
            nombreEtiquettes: expect.any(Object),
          }),
        );
      });

      it('passing ILotEtiquettes should create a new form with FormGroup', () => {
        const formGroup = service.createLotEtiquettesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            dateGeneration: expect.any(Object),
            formatImpression: expect.any(Object),
            nombreEtiquettes: expect.any(Object),
          }),
        );
      });
    });

    describe('getLotEtiquettes', () => {
      it('should return NewLotEtiquettes for default LotEtiquettes initial value', () => {
        const formGroup = service.createLotEtiquettesFormGroup(sampleWithNewData);

        const lotEtiquettes = service.getLotEtiquettes(formGroup);

        expect(lotEtiquettes).toMatchObject(sampleWithNewData);
      });

      it('should return NewLotEtiquettes for empty LotEtiquettes initial value', () => {
        const formGroup = service.createLotEtiquettesFormGroup();

        const lotEtiquettes = service.getLotEtiquettes(formGroup);

        expect(lotEtiquettes).toMatchObject({});
      });

      it('should return ILotEtiquettes', () => {
        const formGroup = service.createLotEtiquettesFormGroup(sampleWithRequiredData);

        const lotEtiquettes = service.getLotEtiquettes(formGroup);

        expect(lotEtiquettes).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILotEtiquettes should not enable id FormControl', () => {
        const formGroup = service.createLotEtiquettesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLotEtiquettes should disable id FormControl', () => {
        const formGroup = service.createLotEtiquettesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
