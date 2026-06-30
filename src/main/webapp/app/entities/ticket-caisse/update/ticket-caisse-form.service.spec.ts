import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ticket-caisse.test-samples';

import { TicketCaisseFormService } from './ticket-caisse-form.service';

describe('TicketCaisse Form Service', () => {
  let service: TicketCaisseFormService;

  beforeEach(() => {
    service = TestBed.inject(TicketCaisseFormService);
  });

  describe('Service methods', () => {
    describe('createTicketCaisseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketCaisseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numero: expect.any(Object),
            dateEmission: expect.any(Object),
            nombreImpressions: expect.any(Object),
            contenu: expect.any(Object),
            vente: expect.any(Object),
          }),
        );
      });

      it('passing ITicketCaisse should create a new form with FormGroup', () => {
        const formGroup = service.createTicketCaisseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numero: expect.any(Object),
            dateEmission: expect.any(Object),
            nombreImpressions: expect.any(Object),
            contenu: expect.any(Object),
            vente: expect.any(Object),
          }),
        );
      });
    });

    describe('getTicketCaisse', () => {
      it('should return NewTicketCaisse for default TicketCaisse initial value', () => {
        const formGroup = service.createTicketCaisseFormGroup(sampleWithNewData);

        const ticketCaisse = service.getTicketCaisse(formGroup);

        expect(ticketCaisse).toMatchObject(sampleWithNewData);
      });

      it('should return NewTicketCaisse for empty TicketCaisse initial value', () => {
        const formGroup = service.createTicketCaisseFormGroup();

        const ticketCaisse = service.getTicketCaisse(formGroup);

        expect(ticketCaisse).toMatchObject({});
      });

      it('should return ITicketCaisse', () => {
        const formGroup = service.createTicketCaisseFormGroup(sampleWithRequiredData);

        const ticketCaisse = service.getTicketCaisse(formGroup);

        expect(ticketCaisse).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITicketCaisse should not enable id FormControl', () => {
        const formGroup = service.createTicketCaisseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTicketCaisse should disable id FormControl', () => {
        const formGroup = service.createTicketCaisseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
