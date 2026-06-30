import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../regle-redevance.test-samples';

import { RegleRedevanceFormService } from './regle-redevance-form.service';

describe('RegleRedevance Form Service', () => {
  let service: RegleRedevanceFormService;

  beforeEach(() => {
    service = TestBed.inject(RegleRedevanceFormService);
  });

  describe('Service methods', () => {
    describe('createRegleRedevanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRegleRedevanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            typeRegle: expect.any(Object),
            taux: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            priorite: expect.any(Object),
            actif: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            groupeArticle: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing IRegleRedevance should create a new form with FormGroup', () => {
        const formGroup = service.createRegleRedevanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            typeRegle: expect.any(Object),
            taux: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            priorite: expect.any(Object),
            actif: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            groupeArticle: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getRegleRedevance', () => {
      it('should return NewRegleRedevance for default RegleRedevance initial value', () => {
        const formGroup = service.createRegleRedevanceFormGroup(sampleWithNewData);

        const regleRedevance = service.getRegleRedevance(formGroup);

        expect(regleRedevance).toMatchObject(sampleWithNewData);
      });

      it('should return NewRegleRedevance for empty RegleRedevance initial value', () => {
        const formGroup = service.createRegleRedevanceFormGroup();

        const regleRedevance = service.getRegleRedevance(formGroup);

        expect(regleRedevance).toMatchObject({});
      });

      it('should return IRegleRedevance', () => {
        const formGroup = service.createRegleRedevanceFormGroup(sampleWithRequiredData);

        const regleRedevance = service.getRegleRedevance(formGroup);

        expect(regleRedevance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRegleRedevance should not enable id FormControl', () => {
        const formGroup = service.createRegleRedevanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRegleRedevance should disable id FormControl', () => {
        const formGroup = service.createRegleRedevanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
