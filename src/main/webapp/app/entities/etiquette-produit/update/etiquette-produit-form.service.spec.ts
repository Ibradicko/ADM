import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../etiquette-produit.test-samples';

import { EtiquetteProduitFormService } from './etiquette-produit-form.service';

describe('EtiquetteProduit Form Service', () => {
  let service: EtiquetteProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(EtiquetteProduitFormService);
  });

  describe('Service methods', () => {
    describe('createEtiquetteProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEtiquetteProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            imprimee: expect.any(Object),
            dateImpression: expect.any(Object),
            produit: expect.any(Object),
            lot: expect.any(Object),
          }),
        );
      });

      it('passing IEtiquetteProduit should create a new form with FormGroup', () => {
        const formGroup = service.createEtiquetteProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            imprimee: expect.any(Object),
            dateImpression: expect.any(Object),
            produit: expect.any(Object),
            lot: expect.any(Object),
          }),
        );
      });
    });

    describe('getEtiquetteProduit', () => {
      it('should return NewEtiquetteProduit for default EtiquetteProduit initial value', () => {
        const formGroup = service.createEtiquetteProduitFormGroup(sampleWithNewData);

        const etiquetteProduit = service.getEtiquetteProduit(formGroup);

        expect(etiquetteProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewEtiquetteProduit for empty EtiquetteProduit initial value', () => {
        const formGroup = service.createEtiquetteProduitFormGroup();

        const etiquetteProduit = service.getEtiquetteProduit(formGroup);

        expect(etiquetteProduit).toMatchObject({});
      });

      it('should return IEtiquetteProduit', () => {
        const formGroup = service.createEtiquetteProduitFormGroup(sampleWithRequiredData);

        const etiquetteProduit = service.getEtiquetteProduit(formGroup);

        expect(etiquetteProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEtiquetteProduit should not enable id FormControl', () => {
        const formGroup = service.createEtiquetteProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEtiquetteProduit should disable id FormControl', () => {
        const formGroup = service.createEtiquetteProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
