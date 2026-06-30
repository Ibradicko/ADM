import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IOperationCorrectiveVente } from '../operation-corrective-vente.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../operation-corrective-vente.test-samples';

import { OperationCorrectiveVenteService, RestOperationCorrectiveVente } from './operation-corrective-vente.service';

const requireRestSample: RestOperationCorrectiveVente = {
  ...sampleWithRequiredData,
  dateOperation: sampleWithRequiredData.dateOperation?.toJSON(),
};

describe('OperationCorrectiveVente Service', () => {
  let service: OperationCorrectiveVenteService;
  let httpMock: HttpTestingController;
  let expectedResult: IOperationCorrectiveVente | IOperationCorrectiveVente[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OperationCorrectiveVenteService);
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

    it('should create a OperationCorrectiveVente', () => {
      const operationCorrectiveVente = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(operationCorrectiveVente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OperationCorrectiveVente', () => {
      const operationCorrectiveVente = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(operationCorrectiveVente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OperationCorrectiveVente', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OperationCorrectiveVente', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OperationCorrectiveVente', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addOperationCorrectiveVenteToCollectionIfMissing', () => {
      it('should add a OperationCorrectiveVente to an empty array', () => {
        const operationCorrectiveVente: IOperationCorrectiveVente = sampleWithRequiredData;
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing([], operationCorrectiveVente);
        expect(expectedResult).toEqual([operationCorrectiveVente]);
      });

      it('should not add a OperationCorrectiveVente to an array that contains it', () => {
        const operationCorrectiveVente: IOperationCorrectiveVente = sampleWithRequiredData;
        const operationCorrectiveVenteCollection: IOperationCorrectiveVente[] = [
          {
            ...operationCorrectiveVente,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing(
          operationCorrectiveVenteCollection,
          operationCorrectiveVente,
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OperationCorrectiveVente to an array that doesn't contain it", () => {
        const operationCorrectiveVente: IOperationCorrectiveVente = sampleWithRequiredData;
        const operationCorrectiveVenteCollection: IOperationCorrectiveVente[] = [sampleWithPartialData];
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing(
          operationCorrectiveVenteCollection,
          operationCorrectiveVente,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(operationCorrectiveVente);
      });

      it('should add only unique OperationCorrectiveVente to an array', () => {
        const operationCorrectiveVenteArray: IOperationCorrectiveVente[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const operationCorrectiveVenteCollection: IOperationCorrectiveVente[] = [sampleWithRequiredData];
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing(
          operationCorrectiveVenteCollection,
          ...operationCorrectiveVenteArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const operationCorrectiveVente: IOperationCorrectiveVente = sampleWithRequiredData;
        const operationCorrectiveVente2: IOperationCorrectiveVente = sampleWithPartialData;
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing([], operationCorrectiveVente, operationCorrectiveVente2);
        expect(expectedResult).toEqual([operationCorrectiveVente, operationCorrectiveVente2]);
      });

      it('should accept null and undefined values', () => {
        const operationCorrectiveVente: IOperationCorrectiveVente = sampleWithRequiredData;
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing([], null, operationCorrectiveVente, undefined);
        expect(expectedResult).toEqual([operationCorrectiveVente]);
      });

      it('should return initial array if no OperationCorrectiveVente is added', () => {
        const operationCorrectiveVenteCollection: IOperationCorrectiveVente[] = [sampleWithRequiredData];
        expectedResult = service.addOperationCorrectiveVenteToCollectionIfMissing(operationCorrectiveVenteCollection, undefined, null);
        expect(expectedResult).toEqual(operationCorrectiveVenteCollection);
      });
    });

    describe('compareOperationCorrectiveVente', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOperationCorrectiveVente(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8986 };
        const entity2 = null;

        const compareResult1 = service.compareOperationCorrectiveVente(entity1, entity2);
        const compareResult2 = service.compareOperationCorrectiveVente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8986 };
        const entity2 = { id: 24724 };

        const compareResult1 = service.compareOperationCorrectiveVente(entity1, entity2);
        const compareResult2 = service.compareOperationCorrectiveVente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8986 };
        const entity2 = { id: 8986 };

        const compareResult1 = service.compareOperationCorrectiveVente(entity1, entity2);
        const compareResult2 = service.compareOperationCorrectiveVente(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
