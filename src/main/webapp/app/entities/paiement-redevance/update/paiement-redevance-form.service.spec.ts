import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../paiement-redevance.test-samples';

import { PaiementRedevanceFormService } from './paiement-redevance-form.service';

describe('PaiementRedevance Form Service', () => {
  let service: PaiementRedevanceFormService;

  beforeEach(() => {
    service = TestBed.inject(PaiementRedevanceFormService);
  });

  describe('Service methods', () => {
    describe('createPaiementRedevanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaiementRedevanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            montant: expect.any(Object),
            datePaiement: expect.any(Object),
            modePaiement: expect.any(Object),
            commentaire: expect.any(Object),
            calcul: expect.any(Object),
          }),
        );
      });

      it('passing IPaiementRedevance should create a new form with FormGroup', () => {
        const formGroup = service.createPaiementRedevanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            montant: expect.any(Object),
            datePaiement: expect.any(Object),
            modePaiement: expect.any(Object),
            commentaire: expect.any(Object),
            calcul: expect.any(Object),
          }),
        );
      });
    });

    describe('getPaiementRedevance', () => {
      it('should return NewPaiementRedevance for default PaiementRedevance initial value', () => {
        const formGroup = service.createPaiementRedevanceFormGroup(sampleWithNewData);

        const paiementRedevance = service.getPaiementRedevance(formGroup);

        expect(paiementRedevance).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaiementRedevance for empty PaiementRedevance initial value', () => {
        const formGroup = service.createPaiementRedevanceFormGroup();

        const paiementRedevance = service.getPaiementRedevance(formGroup);

        expect(paiementRedevance).toMatchObject({});
      });

      it('should return IPaiementRedevance', () => {
        const formGroup = service.createPaiementRedevanceFormGroup(sampleWithRequiredData);

        const paiementRedevance = service.getPaiementRedevance(formGroup);

        expect(paiementRedevance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaiementRedevance should not enable id FormControl', () => {
        const formGroup = service.createPaiementRedevanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaiementRedevance should disable id FormControl', () => {
        const formGroup = service.createPaiementRedevanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
