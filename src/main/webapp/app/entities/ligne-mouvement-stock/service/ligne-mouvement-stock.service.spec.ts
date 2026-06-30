import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneMouvementStock } from '../ligne-mouvement-stock.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../ligne-mouvement-stock.test-samples';

import { LigneMouvementStockService } from './ligne-mouvement-stock.service';

const requireRestSample: ILigneMouvementStock = {
  ...sampleWithRequiredData,
};

describe('LigneMouvementStock Service', () => {
  let service: LigneMouvementStockService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneMouvementStock | ILigneMouvementStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneMouvementStockService);
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

    it('should create a LigneMouvementStock', () => {
      const ligneMouvementStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneMouvementStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneMouvementStock', () => {
      const ligneMouvementStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneMouvementStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneMouvementStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneMouvementStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneMouvementStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneMouvementStockToCollectionIfMissing', () => {
      it('should add a LigneMouvementStock to an empty array', () => {
        const ligneMouvementStock: ILigneMouvementStock = sampleWithRequiredData;
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing([], ligneMouvementStock);
        expect(expectedResult).toEqual([ligneMouvementStock]);
      });

      it('should not add a LigneMouvementStock to an array that contains it', () => {
        const ligneMouvementStock: ILigneMouvementStock = sampleWithRequiredData;
        const ligneMouvementStockCollection: ILigneMouvementStock[] = [
          {
            ...ligneMouvementStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing(ligneMouvementStockCollection, ligneMouvementStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneMouvementStock to an array that doesn't contain it", () => {
        const ligneMouvementStock: ILigneMouvementStock = sampleWithRequiredData;
        const ligneMouvementStockCollection: ILigneMouvementStock[] = [sampleWithPartialData];
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing(ligneMouvementStockCollection, ligneMouvementStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneMouvementStock);
      });

      it('should add only unique LigneMouvementStock to an array', () => {
        const ligneMouvementStockArray: ILigneMouvementStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneMouvementStockCollection: ILigneMouvementStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing(ligneMouvementStockCollection, ...ligneMouvementStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneMouvementStock: ILigneMouvementStock = sampleWithRequiredData;
        const ligneMouvementStock2: ILigneMouvementStock = sampleWithPartialData;
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing([], ligneMouvementStock, ligneMouvementStock2);
        expect(expectedResult).toEqual([ligneMouvementStock, ligneMouvementStock2]);
      });

      it('should accept null and undefined values', () => {
        const ligneMouvementStock: ILigneMouvementStock = sampleWithRequiredData;
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing([], null, ligneMouvementStock, undefined);
        expect(expectedResult).toEqual([ligneMouvementStock]);
      });

      it('should return initial array if no LigneMouvementStock is added', () => {
        const ligneMouvementStockCollection: ILigneMouvementStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneMouvementStockToCollectionIfMissing(ligneMouvementStockCollection, undefined, null);
        expect(expectedResult).toEqual(ligneMouvementStockCollection);
      });
    });

    describe('compareLigneMouvementStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneMouvementStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15898 };
        const entity2 = null;

        const compareResult1 = service.compareLigneMouvementStock(entity1, entity2);
        const compareResult2 = service.compareLigneMouvementStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15898 };
        const entity2 = { id: 7678 };

        const compareResult1 = service.compareLigneMouvementStock(entity1, entity2);
        const compareResult2 = service.compareLigneMouvementStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15898 };
        const entity2 = { id: 15898 };

        const compareResult1 = service.compareLigneMouvementStock(entity1, entity2);
        const compareResult2 = service.compareLigneMouvementStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
