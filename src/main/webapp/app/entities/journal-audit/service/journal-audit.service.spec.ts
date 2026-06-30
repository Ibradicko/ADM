import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IJournalAudit } from '../journal-audit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../journal-audit.test-samples';

import { JournalAuditService, RestJournalAudit } from './journal-audit.service';

const requireRestSample: RestJournalAudit = {
  ...sampleWithRequiredData,
  dateAction: sampleWithRequiredData.dateAction?.toJSON(),
};

describe('JournalAudit Service', () => {
  let service: JournalAuditService;
  let httpMock: HttpTestingController;
  let expectedResult: IJournalAudit | IJournalAudit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(JournalAuditService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a JournalAudit', () => {
      const journalAudit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(journalAudit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a JournalAudit', () => {
      const journalAudit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(journalAudit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a JournalAudit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of JournalAudit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a JournalAudit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addJournalAuditToCollectionIfMissing', () => {
      it('should add a JournalAudit to an empty array', () => {
        const journalAudit: IJournalAudit = sampleWithRequiredData;
        expectedResult = service.addJournalAuditToCollectionIfMissing([], journalAudit);
        expect(expectedResult).toEqual([journalAudit]);
      });

      it('should not add a JournalAudit to an array that contains it', () => {
        const journalAudit: IJournalAudit = sampleWithRequiredData;
        const journalAuditCollection: IJournalAudit[] = [
          {
            ...journalAudit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJournalAuditToCollectionIfMissing(journalAuditCollection, journalAudit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a JournalAudit to an array that doesn't contain it", () => {
        const journalAudit: IJournalAudit = sampleWithRequiredData;
        const journalAuditCollection: IJournalAudit[] = [sampleWithPartialData];
        expectedResult = service.addJournalAuditToCollectionIfMissing(journalAuditCollection, journalAudit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(journalAudit);
      });

      it('should add only unique JournalAudit to an array', () => {
        const journalAuditArray: IJournalAudit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const journalAuditCollection: IJournalAudit[] = [sampleWithRequiredData];
        expectedResult = service.addJournalAuditToCollectionIfMissing(journalAuditCollection, ...journalAuditArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const journalAudit: IJournalAudit = sampleWithRequiredData;
        const journalAudit2: IJournalAudit = sampleWithPartialData;
        expectedResult = service.addJournalAuditToCollectionIfMissing([], journalAudit, journalAudit2);
        expect(expectedResult).toEqual([journalAudit, journalAudit2]);
      });

      it('should accept null and undefined values', () => {
        const journalAudit: IJournalAudit = sampleWithRequiredData;
        expectedResult = service.addJournalAuditToCollectionIfMissing([], null, journalAudit, undefined);
        expect(expectedResult).toEqual([journalAudit]);
      });

      it('should return initial array if no JournalAudit is added', () => {
        const journalAuditCollection: IJournalAudit[] = [sampleWithRequiredData];
        expectedResult = service.addJournalAuditToCollectionIfMissing(journalAuditCollection, undefined, null);
        expect(expectedResult).toEqual(journalAuditCollection);
      });
    });

    describe('compareJournalAudit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJournalAudit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25137 };
        const entity2 = null;

        const compareResult1 = service.compareJournalAudit(entity1, entity2);
        const compareResult2 = service.compareJournalAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25137 };
        const entity2 = { id: 13768 };

        const compareResult1 = service.compareJournalAudit(entity1, entity2);
        const compareResult2 = service.compareJournalAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25137 };
        const entity2 = { id: 25137 };

        const compareResult1 = service.compareJournalAudit(entity1, entity2);
        const compareResult2 = service.compareJournalAudit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
