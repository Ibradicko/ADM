import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../paiement-vente.test-samples';

import { PaiementVenteFormService } from './paiement-vente-form.service';

describe('PaiementVente Form Service', () => {
  let service: PaiementVenteFormService;

  beforeEach(() => {
    service = TestBed.inject(PaiementVenteFormService);
  });

  describe('Service methods', () => {
    describe('createPaiementVenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaiementVenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            statut: expect.any(Object),
            referencePaiement: expect.any(Object),
            datePaiement: expect.any(Object),
            vente: expect.any(Object),
            modePaiement: expect.any(Object),
          }),
        );
      });

      it('passing IPaiementVente should create a new form with FormGroup', () => {
        const formGroup = service.createPaiementVenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            statut: expect.any(Object),
            referencePaiement: expect.any(Object),
            datePaiement: expect.any(Object),
            vente: expect.any(Object),
            modePaiement: expect.any(Object),
          }),
        );
      });
    });

    describe('getPaiementVente', () => {
      it('should return NewPaiementVente for default PaiementVente initial value', () => {
        const formGroup = service.createPaiementVenteFormGroup(sampleWithNewData);

        const paiementVente = service.getPaiementVente(formGroup);

        expect(paiementVente).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaiementVente for empty PaiementVente initial value', () => {
        const formGroup = service.createPaiementVenteFormGroup();

        const paiementVente = service.getPaiementVente(formGroup);

        expect(paiementVente).toMatchObject({});
      });

      it('should return IPaiementVente', () => {
        const formGroup = service.createPaiementVenteFormGroup(sampleWithRequiredData);

        const paiementVente = service.getPaiementVente(formGroup);

        expect(paiementVente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaiementVente should not enable id FormControl', () => {
        const formGroup = service.createPaiementVenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaiementVente should disable id FormControl', () => {
        const formGroup = service.createPaiementVenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
