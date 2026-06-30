import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../vente.test-samples';

import { VenteFormService } from './vente-form.service';

describe('Vente Form Service', () => {
  let service: VenteFormService;

  beforeEach(() => {
    service = TestBed.inject(VenteFormService);
  });

  describe('Service methods', () => {
    describe('createVenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroTicket: expect.any(Object),
            dateHeure: expect.any(Object),
            statut: expect.any(Object),
            referencePassager: expect.any(Object),
            referenceCarteEmbarquement: expect.any(Object),
            montantBrut: expect.any(Object),
            montantRemise: expect.any(Object),
            montantNet: expect.any(Object),
            commentaire: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            vendeur: expect.any(Object),
          }),
        );
      });

      it('passing IVente should create a new form with FormGroup', () => {
        const formGroup = service.createVenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroTicket: expect.any(Object),
            dateHeure: expect.any(Object),
            statut: expect.any(Object),
            referencePassager: expect.any(Object),
            referenceCarteEmbarquement: expect.any(Object),
            montantBrut: expect.any(Object),
            montantRemise: expect.any(Object),
            montantNet: expect.any(Object),
            commentaire: expect.any(Object),
            boutique: expect.any(Object),
            locataire: expect.any(Object),
            vendeur: expect.any(Object),
          }),
        );
      });
    });

    describe('getVente', () => {
      it('should return NewVente for default Vente initial value', () => {
        const formGroup = service.createVenteFormGroup(sampleWithNewData);

        const vente = service.getVente(formGroup);

        expect(vente).toMatchObject(sampleWithNewData);
      });

      it('should return NewVente for empty Vente initial value', () => {
        const formGroup = service.createVenteFormGroup();

        const vente = service.getVente(formGroup);

        expect(vente).toMatchObject({});
      });

      it('should return IVente', () => {
        const formGroup = service.createVenteFormGroup(sampleWithRequiredData);

        const vente = service.getVente(formGroup);

        expect(vente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVente should not enable id FormControl', () => {
        const formGroup = service.createVenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVente should disable id FormControl', () => {
        const formGroup = service.createVenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
