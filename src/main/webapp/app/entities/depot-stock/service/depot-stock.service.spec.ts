import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDepotStock } from '../depot-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../depot-stock.test-samples';

import { DepotStockService } from './depot-stock.service';

const requireRestSample: IDepotStock = {
  ...sampleWithRequiredData,
};

describe('DepotStock Service', () => {
  let service: DepotStockService;
  let httpMock: HttpTestingController;
  let expectedResult: IDepotStock | IDepotStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DepotStockService);
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

    it('should create a DepotStock', () => {
      const depotStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(depotStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DepotStock', () => {
      const depotStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(depotStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DepotStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DepotStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DepotStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addDepotStockToCollectionIfMissing', () => {
      it('should add a DepotStock to an empty array', () => {
        const depotStock: IDepotStock = sampleWithRequiredData;
        expectedResult = service.addDepotStockToCollectionIfMissing([], depotStock);
        expect(expectedResult).toEqual([depotStock]);
      });

      it('should not add a DepotStock to an array that contains it', () => {
        const depotStock: IDepotStock = sampleWithRequiredData;
        const depotStockCollection: IDepotStock[] = [
          {
            ...depotStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDepotStockToCollectionIfMissing(depotStockCollection, depotStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DepotStock to an array that doesn't contain it", () => {
        const depotStock: IDepotStock = sampleWithRequiredData;
        const depotStockCollection: IDepotStock[] = [sampleWithPartialData];
        expectedResult = service.addDepotStockToCollectionIfMissing(depotStockCollection, depotStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(depotStock);
      });

      it('should add only unique DepotStock to an array', () => {
        const depotStockArray: IDepotStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const depotStockCollection: IDepotStock[] = [sampleWithRequiredData];
        expectedResult = service.addDepotStockToCollectionIfMissing(depotStockCollection, ...depotStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const depotStock: IDepotStock = sampleWithRequiredData;
        const depotStock2: IDepotStock = sampleWithPartialData;
        expectedResult = service.addDepotStockToCollectionIfMissing([], depotStock, depotStock2);
        expect(expectedResult).toEqual([depotStock, depotStock2]);
      });

      it('should accept null and undefined values', () => {
        const depotStock: IDepotStock = sampleWithRequiredData;
        expectedResult = service.addDepotStockToCollectionIfMissing([], null, depotStock, undefined);
        expect(expectedResult).toEqual([depotStock]);
      });

      it('should return initial array if no DepotStock is added', () => {
        const depotStockCollection: IDepotStock[] = [sampleWithRequiredData];
        expectedResult = service.addDepotStockToCollectionIfMissing(depotStockCollection, undefined, null);
        expect(expectedResult).toEqual(depotStockCollection);
      });
    });

    describe('compareDepotStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDepotStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 26721 };
        const entity2 = null;

        const compareResult1 = service.compareDepotStock(entity1, entity2);
        const compareResult2 = service.compareDepotStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 26721 };
        const entity2 = { id: 25113 };

        const compareResult1 = service.compareDepotStock(entity1, entity2);
        const compareResult2 = service.compareDepotStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 26721 };
        const entity2 = { id: 26721 };

        const compareResult1 = service.compareDepotStock(entity1, entity2);
        const compareResult2 = service.compareDepotStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
