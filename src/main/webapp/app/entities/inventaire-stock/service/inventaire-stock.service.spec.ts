import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IInventaireStock } from '../inventaire-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../inventaire-stock.test-samples';

import { InventaireStockService, RestInventaireStock } from './inventaire-stock.service';

const requireRestSample: RestInventaireStock = {
  ...sampleWithRequiredData,
  dateDebut: sampleWithRequiredData.dateDebut?.toJSON(),
  dateFin: sampleWithRequiredData.dateFin?.toJSON(),
};

describe('InventaireStock Service', () => {
  let service: InventaireStockService;
  let httpMock: HttpTestingController;
  let expectedResult: IInventaireStock | IInventaireStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InventaireStockService);
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

    it('should create a InventaireStock', () => {
      const inventaireStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(inventaireStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a InventaireStock', () => {
      const inventaireStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(inventaireStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a InventaireStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of InventaireStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a InventaireStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addInventaireStockToCollectionIfMissing', () => {
      it('should add a InventaireStock to an empty array', () => {
        const inventaireStock: IInventaireStock = sampleWithRequiredData;
        expectedResult = service.addInventaireStockToCollectionIfMissing([], inventaireStock);
        expect(expectedResult).toEqual([inventaireStock]);
      });

      it('should not add a InventaireStock to an array that contains it', () => {
        const inventaireStock: IInventaireStock = sampleWithRequiredData;
        const inventaireStockCollection: IInventaireStock[] = [
          {
            ...inventaireStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInventaireStockToCollectionIfMissing(inventaireStockCollection, inventaireStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a InventaireStock to an array that doesn't contain it", () => {
        const inventaireStock: IInventaireStock = sampleWithRequiredData;
        const inventaireStockCollection: IInventaireStock[] = [sampleWithPartialData];
        expectedResult = service.addInventaireStockToCollectionIfMissing(inventaireStockCollection, inventaireStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inventaireStock);
      });

      it('should add only unique InventaireStock to an array', () => {
        const inventaireStockArray: IInventaireStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const inventaireStockCollection: IInventaireStock[] = [sampleWithRequiredData];
        expectedResult = service.addInventaireStockToCollectionIfMissing(inventaireStockCollection, ...inventaireStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const inventaireStock: IInventaireStock = sampleWithRequiredData;
        const inventaireStock2: IInventaireStock = sampleWithPartialData;
        expectedResult = service.addInventaireStockToCollectionIfMissing([], inventaireStock, inventaireStock2);
        expect(expectedResult).toEqual([inventaireStock, inventaireStock2]);
      });

      it('should accept null and undefined values', () => {
        const inventaireStock: IInventaireStock = sampleWithRequiredData;
        expectedResult = service.addInventaireStockToCollectionIfMissing([], null, inventaireStock, undefined);
        expect(expectedResult).toEqual([inventaireStock]);
      });

      it('should return initial array if no InventaireStock is added', () => {
        const inventaireStockCollection: IInventaireStock[] = [sampleWithRequiredData];
        expectedResult = service.addInventaireStockToCollectionIfMissing(inventaireStockCollection, undefined, null);
        expect(expectedResult).toEqual(inventaireStockCollection);
      });
    });

    describe('compareInventaireStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInventaireStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31192 };
        const entity2 = null;

        const compareResult1 = service.compareInventaireStock(entity1, entity2);
        const compareResult2 = service.compareInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31192 };
        const entity2 = { id: 105 };

        const compareResult1 = service.compareInventaireStock(entity1, entity2);
        const compareResult2 = service.compareInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31192 };
        const entity2 = { id: 31192 };

        const compareResult1 = service.compareInventaireStock(entity1, entity2);
        const compareResult2 = service.compareInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
