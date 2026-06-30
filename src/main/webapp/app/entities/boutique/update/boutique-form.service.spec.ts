import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../boutique.test-samples';

import { BoutiqueFormService } from './boutique-form.service';

describe('Boutique Form Service', () => {
  let service: BoutiqueFormService;

  beforeEach(() => {
    service = TestBed.inject(BoutiqueFormService);
  });

  describe('Service methods', () => {
    describe('createBoutiqueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBoutiqueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            type: expect.any(Object),
            emplacement: expect.any(Object),
            telephone: expect.any(Object),
            email: expect.any(Object),
            statut: expect.any(Object),
            dateCreation: expect.any(Object),
          }),
        );
      });

      it('passing IBoutique should create a new form with FormGroup', () => {
        const formGroup = service.createBoutiqueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            nom: expect.any(Object),
            type: expect.any(Object),
            emplacement: expect.any(Object),
            telephone: expect.any(Object),
            email: expect.any(Object),
            statut: expect.any(Object),
            dateCreation: expect.any(Object),
          }),
        );
      });
    });

    describe('getBoutique', () => {
      it('should return NewBoutique for default Boutique initial value', () => {
        const formGroup = service.createBoutiqueFormGroup(sampleWithNewData);

        const boutique = service.getBoutique(formGroup);

        expect(boutique).toMatchObject(sampleWithNewData);
      });

      it('should return NewBoutique for empty Boutique initial value', () => {
        const formGroup = service.createBoutiqueFormGroup();

        const boutique = service.getBoutique(formGroup);

        expect(boutique).toMatchObject({});
      });

      it('should return IBoutique', () => {
        const formGroup = service.createBoutiqueFormGroup(sampleWithRequiredData);

        const boutique = service.getBoutique(formGroup);

        expect(boutique).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBoutique should not enable id FormControl', () => {
        const formGroup = service.createBoutiqueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBoutique should disable id FormControl', () => {
        const formGroup = service.createBoutiqueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
