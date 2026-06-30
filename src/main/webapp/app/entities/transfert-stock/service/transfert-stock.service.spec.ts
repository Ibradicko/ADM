import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITransfertStock } from '../transfert-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../transfert-stock.test-samples';

import { RestTransfertStock, TransfertStockService } from './transfert-stock.service';

const requireRestSample: RestTransfertStock = {
  ...sampleWithRequiredData,
  dateTransfert: sampleWithRequiredData.dateTransfert?.toJSON(),
};

describe('TransfertStock Service', () => {
  let service: TransfertStockService;
  let httpMock: HttpTestingController;
  let expectedResult: ITransfertStock | ITransfertStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TransfertStockService);
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

    it('should create a TransfertStock', () => {
      const transfertStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(transfertStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TransfertStock', () => {
      const transfertStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(transfertStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TransfertStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TransfertStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TransfertStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTransfertStockToCollectionIfMissing', () => {
      it('should add a TransfertStock to an empty array', () => {
        const transfertStock: ITransfertStock = sampleWithRequiredData;
        expectedResult = service.addTransfertStockToCollectionIfMissing([], transfertStock);
        expect(expectedResult).toEqual([transfertStock]);
      });

      it('should not add a TransfertStock to an array that contains it', () => {
        const transfertStock: ITransfertStock = sampleWithRequiredData;
        const transfertStockCollection: ITransfertStock[] = [
          {
            ...transfertStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTransfertStockToCollectionIfMissing(transfertStockCollection, transfertStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TransfertStock to an array that doesn't contain it", () => {
        const transfertStock: ITransfertStock = sampleWithRequiredData;
        const transfertStockCollection: ITransfertStock[] = [sampleWithPartialData];
        expectedResult = service.addTransfertStockToCollectionIfMissing(transfertStockCollection, transfertStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transfertStock);
      });

      it('should add only unique TransfertStock to an array', () => {
        const transfertStockArray: ITransfertStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const transfertStockCollection: ITransfertStock[] = [sampleWithRequiredData];
        expectedResult = service.addTransfertStockToCollectionIfMissing(transfertStockCollection, ...transfertStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transfertStock: ITransfertStock = sampleWithRequiredData;
        const transfertStock2: ITransfertStock = sampleWithPartialData;
        expectedResult = service.addTransfertStockToCollectionIfMissing([], transfertStock, transfertStock2);
        expect(expectedResult).toEqual([transfertStock, transfertStock2]);
      });

      it('should accept null and undefined values', () => {
        const transfertStock: ITransfertStock = sampleWithRequiredData;
        expectedResult = service.addTransfertStockToCollectionIfMissing([], null, transfertStock, undefined);
        expect(expectedResult).toEqual([transfertStock]);
      });

      it('should return initial array if no TransfertStock is added', () => {
        const transfertStockCollection: ITransfertStock[] = [sampleWithRequiredData];
        expectedResult = service.addTransfertStockToCollectionIfMissing(transfertStockCollection, undefined, null);
        expect(expectedResult).toEqual(transfertStockCollection);
      });
    });

    describe('compareTransfertStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTransfertStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31780 };
        const entity2 = null;

        const compareResult1 = service.compareTransfertStock(entity1, entity2);
        const compareResult2 = service.compareTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31780 };
        const entity2 = { id: 6468 };

        const compareResult1 = service.compareTransfertStock(entity1, entity2);
        const compareResult2 = service.compareTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31780 };
        const entity2 = { id: 31780 };

        const compareResult1 = service.compareTransfertStock(entity1, entity2);
        const compareResult2 = service.compareTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
