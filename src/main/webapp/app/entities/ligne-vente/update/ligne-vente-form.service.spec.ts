import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-vente.test-samples';

import { LigneVenteFormService } from './ligne-vente-form.service';

describe('LigneVente Form Service', () => {
  let service: LigneVenteFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneVenteFormService);
  });

  describe('Service methods', () => {
    describe('createLigneVenteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneVenteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            prixUnitaire: expect.any(Object),
            remise: expect.any(Object),
            montantLigne: expect.any(Object),
            codeBarresScanne: expect.any(Object),
            vente: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing ILigneVente should create a new form with FormGroup', () => {
        const formGroup = service.createLigneVenteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            prixUnitaire: expect.any(Object),
            remise: expect.any(Object),
            montantLigne: expect.any(Object),
            codeBarresScanne: expect.any(Object),
            vente: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneVente', () => {
      it('should return NewLigneVente for default LigneVente initial value', () => {
        const formGroup = service.createLigneVenteFormGroup(sampleWithNewData);

        const ligneVente = service.getLigneVente(formGroup);

        expect(ligneVente).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneVente for empty LigneVente initial value', () => {
        const formGroup = service.createLigneVenteFormGroup();

        const ligneVente = service.getLigneVente(formGroup);

        expect(ligneVente).toMatchObject({});
      });

      it('should return ILigneVente', () => {
        const formGroup = service.createLigneVenteFormGroup(sampleWithRequiredData);

        const ligneVente = service.getLigneVente(formGroup);

        expect(ligneVente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneVente should not enable id FormControl', () => {
        const formGroup = service.createLigneVenteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneVente should disable id FormControl', () => {
        const formGroup = service.createLigneVenteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
