import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../journal-audit.test-samples';

import { JournalAuditFormService } from './journal-audit-form.service';

describe('JournalAudit Form Service', () => {
  let service: JournalAuditFormService;

  beforeEach(() => {
    service = TestBed.inject(JournalAuditFormService);
  });

  describe('Service methods', () => {
    describe('createJournalAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJournalAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            typeAction: expect.any(Object),
            entiteConcernee: expect.any(Object),
            identifiantEntite: expect.any(Object),
            description: expect.any(Object),
            adresseIp: expect.any(Object),
            dateAction: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });

      it('passing IJournalAudit should create a new form with FormGroup', () => {
        const formGroup = service.createJournalAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            typeAction: expect.any(Object),
            entiteConcernee: expect.any(Object),
            identifiantEntite: expect.any(Object),
            description: expect.any(Object),
            adresseIp: expect.any(Object),
            dateAction: expect.any(Object),
            boutique: expect.any(Object),
            utilisateur: expect.any(Object),
          }),
        );
      });
    });

    describe('getJournalAudit', () => {
      it('should return NewJournalAudit for default JournalAudit initial value', () => {
        const formGroup = service.createJournalAuditFormGroup(sampleWithNewData);

        const journalAudit = service.getJournalAudit(formGroup);

        expect(journalAudit).toMatchObject(sampleWithNewData);
      });

      it('should return NewJournalAudit for empty JournalAudit initial value', () => {
        const formGroup = service.createJournalAuditFormGroup();

        const journalAudit = service.getJournalAudit(formGroup);

        expect(journalAudit).toMatchObject({});
      });

      it('should return IJournalAudit', () => {
        const formGroup = service.createJournalAuditFormGroup(sampleWithRequiredData);

        const journalAudit = service.getJournalAudit(formGroup);

        expect(journalAudit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJournalAudit should not enable id FormControl', () => {
        const formGroup = service.createJournalAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJournalAudit should disable id FormControl', () => {
        const formGroup = service.createJournalAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
