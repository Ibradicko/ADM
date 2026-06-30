import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../profil-metier.test-samples';

import { ProfilMetierFormService } from './profil-metier-form.service';

describe('ProfilMetier Form Service', () => {
  let service: ProfilMetierFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfilMetierFormService);
  });

  describe('Service methods', () => {
    describe('createProfilMetierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfilMetierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            description: expect.any(Object),
            statut: expect.any(Object),
            permissionses: expect.any(Object),
          }),
        );
      });

      it('passing IProfilMetier should create a new form with FormGroup', () => {
        const formGroup = service.createProfilMetierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            description: expect.any(Object),
            statut: expect.any(Object),
            permissionses: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfilMetier', () => {
      it('should return NewProfilMetier for default ProfilMetier initial value', () => {
        const formGroup = service.createProfilMetierFormGroup(sampleWithNewData);

        const profilMetier = service.getProfilMetier(formGroup);

        expect(profilMetier).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfilMetier for empty ProfilMetier initial value', () => {
        const formGroup = service.createProfilMetierFormGroup();

        const profilMetier = service.getProfilMetier(formGroup);

        expect(profilMetier).toMatchObject({});
      });

      it('should return IProfilMetier', () => {
        const formGroup = service.createProfilMetierFormGroup(sampleWithRequiredData);

        const profilMetier = service.getProfilMetier(formGroup);

        expect(profilMetier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfilMetier should not enable id FormControl', () => {
        const formGroup = service.createProfilMetierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfilMetier should disable id FormControl', () => {
        const formGroup = service.createProfilMetierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
