import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../historique-code-barres.test-samples';

import { HistoriqueCodeBarresFormService } from './historique-code-barres-form.service';

describe('HistoriqueCodeBarres Form Service', () => {
  let service: HistoriqueCodeBarresFormService;

  beforeEach(() => {
    service = TestBed.inject(HistoriqueCodeBarresFormService);
  });

  describe('Service methods', () => {
    describe('createHistoriqueCodeBarresFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            ancienCode: expect.any(Object),
            nouveauCode: expect.any(Object),
            motif: expect.any(Object),
            dateChangement: expect.any(Object),
            produit: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IHistoriqueCodeBarres should create a new form with FormGroup', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            ancienCode: expect.any(Object),
            nouveauCode: expect.any(Object),
            motif: expect.any(Object),
            dateChangement: expect.any(Object),
            produit: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getHistoriqueCodeBarres', () => {
      it('should return NewHistoriqueCodeBarres for default HistoriqueCodeBarres initial value', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup(sampleWithNewData);

        const historiqueCodeBarres = service.getHistoriqueCodeBarres(formGroup);

        expect(historiqueCodeBarres).toMatchObject(sampleWithNewData);
      });

      it('should return NewHistoriqueCodeBarres for empty HistoriqueCodeBarres initial value', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup();

        const historiqueCodeBarres = service.getHistoriqueCodeBarres(formGroup);

        expect(historiqueCodeBarres).toMatchObject({});
      });

      it('should return IHistoriqueCodeBarres', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup(sampleWithRequiredData);

        const historiqueCodeBarres = service.getHistoriqueCodeBarres(formGroup);

        expect(historiqueCodeBarres).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHistoriqueCodeBarres should not enable id FormControl', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHistoriqueCodeBarres should disable id FormControl', () => {
        const formGroup = service.createHistoriqueCodeBarresFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
