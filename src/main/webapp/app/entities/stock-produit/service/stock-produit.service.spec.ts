import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IStockProduit } from '../stock-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../stock-produit.test-samples';

import { RestStockProduit, StockProduitService } from './stock-produit.service';

const requireRestSample: RestStockProduit = {
  ...sampleWithRequiredData,
  dateDernierMouvement: sampleWithRequiredData.dateDernierMouvement?.toJSON(),
};

describe('StockProduit Service', () => {
  let service: StockProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: IStockProduit | IStockProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StockProduitService);
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

    it('should create a StockProduit', () => {
      const stockProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stockProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StockProduit', () => {
      const stockProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stockProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StockProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StockProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StockProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addStockProduitToCollectionIfMissing', () => {
      it('should add a StockProduit to an empty array', () => {
        const stockProduit: IStockProduit = sampleWithRequiredData;
        expectedResult = service.addStockProduitToCollectionIfMissing([], stockProduit);
        expect(expectedResult).toEqual([stockProduit]);
      });

      it('should not add a StockProduit to an array that contains it', () => {
        const stockProduit: IStockProduit = sampleWithRequiredData;
        const stockProduitCollection: IStockProduit[] = [
          {
            ...stockProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStockProduitToCollectionIfMissing(stockProduitCollection, stockProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StockProduit to an array that doesn't contain it", () => {
        const stockProduit: IStockProduit = sampleWithRequiredData;
        const stockProduitCollection: IStockProduit[] = [sampleWithPartialData];
        expectedResult = service.addStockProduitToCollectionIfMissing(stockProduitCollection, stockProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockProduit);
      });

      it('should add only unique StockProduit to an array', () => {
        const stockProduitArray: IStockProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stockProduitCollection: IStockProduit[] = [sampleWithRequiredData];
        expectedResult = service.addStockProduitToCollectionIfMissing(stockProduitCollection, ...stockProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stockProduit: IStockProduit = sampleWithRequiredData;
        const stockProduit2: IStockProduit = sampleWithPartialData;
        expectedResult = service.addStockProduitToCollectionIfMissing([], stockProduit, stockProduit2);
        expect(expectedResult).toEqual([stockProduit, stockProduit2]);
      });

      it('should accept null and undefined values', () => {
        const stockProduit: IStockProduit = sampleWithRequiredData;
        expectedResult = service.addStockProduitToCollectionIfMissing([], null, stockProduit, undefined);
        expect(expectedResult).toEqual([stockProduit]);
      });

      it('should return initial array if no StockProduit is added', () => {
        const stockProduitCollection: IStockProduit[] = [sampleWithRequiredData];
        expectedResult = service.addStockProduitToCollectionIfMissing(stockProduitCollection, undefined, null);
        expect(expectedResult).toEqual(stockProduitCollection);
      });
    });

    describe('compareStockProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStockProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21740 };
        const entity2 = null;

        const compareResult1 = service.compareStockProduit(entity1, entity2);
        const compareResult2 = service.compareStockProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21740 };
        const entity2 = { id: 29002 };

        const compareResult1 = service.compareStockProduit(entity1, entity2);
        const compareResult2 = service.compareStockProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21740 };
        const entity2 = { id: 21740 };

        const compareResult1 = service.compareStockProduit(entity1, entity2);
        const compareResult2 = service.compareStockProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
