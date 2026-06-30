import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-calcul-redevance.test-samples';

import { LigneCalculRedevanceFormService } from './ligne-calcul-redevance-form.service';

describe('LigneCalculRedevance Form Service', () => {
  let service: LigneCalculRedevanceFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneCalculRedevanceFormService);
  });

  describe('Service methods', () => {
    describe('createLigneCalculRedevanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            baseCalcul: expect.any(Object),
            tauxApplique: expect.any(Object),
            montantRedevance: expect.any(Object),
            calcul: expect.any(Object),
            vente: expect.any(Object),
          }),
        );
      });

      it('passing ILigneCalculRedevance should create a new form with FormGroup', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            baseCalcul: expect.any(Object),
            tauxApplique: expect.any(Object),
            montantRedevance: expect.any(Object),
            calcul: expect.any(Object),
            vente: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneCalculRedevance', () => {
      it('should return NewLigneCalculRedevance for default LigneCalculRedevance initial value', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup(sampleWithNewData);

        const ligneCalculRedevance = service.getLigneCalculRedevance(formGroup);

        expect(ligneCalculRedevance).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneCalculRedevance for empty LigneCalculRedevance initial value', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup();

        const ligneCalculRedevance = service.getLigneCalculRedevance(formGroup);

        expect(ligneCalculRedevance).toMatchObject({});
      });

      it('should return ILigneCalculRedevance', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup(sampleWithRequiredData);

        const ligneCalculRedevance = service.getLigneCalculRedevance(formGroup);

        expect(ligneCalculRedevance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneCalculRedevance should not enable id FormControl', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneCalculRedevance should disable id FormControl', () => {
        const formGroup = service.createLigneCalculRedevanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
