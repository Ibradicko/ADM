import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPaiementRedevance } from '../paiement-redevance.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../paiement-redevance.test-samples';

import { PaiementRedevanceService, RestPaiementRedevance } from './paiement-redevance.service';

const requireRestSample: RestPaiementRedevance = {
  ...sampleWithRequiredData,
  datePaiement: sampleWithRequiredData.datePaiement?.format(DATE_FORMAT),
};

describe('PaiementRedevance Service', () => {
  let service: PaiementRedevanceService;
  let httpMock: HttpTestingController;
  let expectedResult: IPaiementRedevance | IPaiementRedevance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PaiementRedevanceService);
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

    it('should create a PaiementRedevance', () => {
      const paiementRedevance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(paiementRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PaiementRedevance', () => {
      const paiementRedevance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(paiementRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PaiementRedevance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PaiementRedevance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PaiementRedevance', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPaiementRedevanceToCollectionIfMissing', () => {
      it('should add a PaiementRedevance to an empty array', () => {
        const paiementRedevance: IPaiementRedevance = sampleWithRequiredData;
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing([], paiementRedevance);
        expect(expectedResult).toEqual([paiementRedevance]);
      });

      it('should not add a PaiementRedevance to an array that contains it', () => {
        const paiementRedevance: IPaiementRedevance = sampleWithRequiredData;
        const paiementRedevanceCollection: IPaiementRedevance[] = [
          {
            ...paiementRedevance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing(paiementRedevanceCollection, paiementRedevance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PaiementRedevance to an array that doesn't contain it", () => {
        const paiementRedevance: IPaiementRedevance = sampleWithRequiredData;
        const paiementRedevanceCollection: IPaiementRedevance[] = [sampleWithPartialData];
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing(paiementRedevanceCollection, paiementRedevance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paiementRedevance);
      });

      it('should add only unique PaiementRedevance to an array', () => {
        const paiementRedevanceArray: IPaiementRedevance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const paiementRedevanceCollection: IPaiementRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing(paiementRedevanceCollection, ...paiementRedevanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const paiementRedevance: IPaiementRedevance = sampleWithRequiredData;
        const paiementRedevance2: IPaiementRedevance = sampleWithPartialData;
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing([], paiementRedevance, paiementRedevance2);
        expect(expectedResult).toEqual([paiementRedevance, paiementRedevance2]);
      });

      it('should accept null and undefined values', () => {
        const paiementRedevance: IPaiementRedevance = sampleWithRequiredData;
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing([], null, paiementRedevance, undefined);
        expect(expectedResult).toEqual([paiementRedevance]);
      });

      it('should return initial array if no PaiementRedevance is added', () => {
        const paiementRedevanceCollection: IPaiementRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addPaiementRedevanceToCollectionIfMissing(paiementRedevanceCollection, undefined, null);
        expect(expectedResult).toEqual(paiementRedevanceCollection);
      });
    });

    describe('comparePaiementRedevance', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePaiementRedevance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 32698 };
        const entity2 = null;

        const compareResult1 = service.comparePaiementRedevance(entity1, entity2);
        const compareResult2 = service.comparePaiementRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 32698 };
        const entity2 = { id: 27581 };

        const compareResult1 = service.comparePaiementRedevance(entity1, entity2);
        const compareResult2 = service.comparePaiementRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 32698 };
        const entity2 = { id: 32698 };

        const compareResult1 = service.comparePaiementRedevance(entity1, entity2);
        const compareResult2 = service.comparePaiementRedevance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
