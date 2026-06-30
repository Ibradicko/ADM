import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../permission-metier.test-samples';

import { PermissionMetierFormService } from './permission-metier-form.service';

describe('PermissionMetier Form Service', () => {
  let service: PermissionMetierFormService;

  beforeEach(() => {
    service = TestBed.inject(PermissionMetierFormService);
  });

  describe('Service methods', () => {
    describe('createPermissionMetierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPermissionMetierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            module: expect.any(Object),
            description: expect.any(Object),
            profilses: expect.any(Object),
          }),
        );
      });

      it('passing IPermissionMetier should create a new form with FormGroup', () => {
        const formGroup = service.createPermissionMetierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            libelle: expect.any(Object),
            module: expect.any(Object),
            description: expect.any(Object),
            profilses: expect.any(Object),
          }),
        );
      });
    });

    describe('getPermissionMetier', () => {
      it('should return NewPermissionMetier for default PermissionMetier initial value', () => {
        const formGroup = service.createPermissionMetierFormGroup(sampleWithNewData);

        const permissionMetier = service.getPermissionMetier(formGroup);

        expect(permissionMetier).toMatchObject(sampleWithNewData);
      });

      it('should return NewPermissionMetier for empty PermissionMetier initial value', () => {
        const formGroup = service.createPermissionMetierFormGroup();

        const permissionMetier = service.getPermissionMetier(formGroup);

        expect(permissionMetier).toMatchObject({});
      });

      it('should return IPermissionMetier', () => {
        const formGroup = service.createPermissionMetierFormGroup(sampleWithRequiredData);

        const permissionMetier = service.getPermissionMetier(formGroup);

        expect(permissionMetier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPermissionMetier should not enable id FormControl', () => {
        const formGroup = service.createPermissionMetierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPermissionMetier should disable id FormControl', () => {
        const formGroup = service.createPermissionMetierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
