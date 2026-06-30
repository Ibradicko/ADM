import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../affectation-utilisateur.test-samples';

import { AffectationUtilisateurFormService } from './affectation-utilisateur-form.service';

describe('AffectationUtilisateur Form Service', () => {
  let service: AffectationUtilisateurFormService;

  beforeEach(() => {
    service = TestBed.inject(AffectationUtilisateurFormService);
  });

  describe('Service methods', () => {
    describe('createAffectationUtilisateurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            actif: expect.any(Object),
            user: expect.any(Object),
            boutique: expect.any(Object),
            profil: expect.any(Object),
          }),
        );
      });

      it('passing IAffectationUtilisateur should create a new form with FormGroup', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            actif: expect.any(Object),
            user: expect.any(Object),
            boutique: expect.any(Object),
            profil: expect.any(Object),
          }),
        );
      });
    });

    describe('getAffectationUtilisateur', () => {
      it('should return NewAffectationUtilisateur for default AffectationUtilisateur initial value', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup(sampleWithNewData);

        const affectationUtilisateur = service.getAffectationUtilisateur(formGroup);

        expect(affectationUtilisateur).toMatchObject(sampleWithNewData);
      });

      it('should return NewAffectationUtilisateur for empty AffectationUtilisateur initial value', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup();

        const affectationUtilisateur = service.getAffectationUtilisateur(formGroup);

        expect(affectationUtilisateur).toMatchObject({});
      });

      it('should return IAffectationUtilisateur', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup(sampleWithRequiredData);

        const affectationUtilisateur = service.getAffectationUtilisateur(formGroup);

        expect(affectationUtilisateur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAffectationUtilisateur should not enable id FormControl', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAffectationUtilisateur should disable id FormControl', () => {
        const formGroup = service.createAffectationUtilisateurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
