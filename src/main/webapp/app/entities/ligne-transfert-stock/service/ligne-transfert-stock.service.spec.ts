import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneTransfertStock } from '../ligne-transfert-stock.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../ligne-transfert-stock.test-samples';

import { LigneTransfertStockService } from './ligne-transfert-stock.service';

const requireRestSample: ILigneTransfertStock = {
  ...sampleWithRequiredData,
};

describe('LigneTransfertStock Service', () => {
  let service: LigneTransfertStockService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneTransfertStock | ILigneTransfertStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneTransfertStockService);
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

    it('should create a LigneTransfertStock', () => {
      const ligneTransfertStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneTransfertStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneTransfertStock', () => {
      const ligneTransfertStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneTransfertStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneTransfertStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneTransfertStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneTransfertStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneTransfertStockToCollectionIfMissing', () => {
      it('should add a LigneTransfertStock to an empty array', () => {
        const ligneTransfertStock: ILigneTransfertStock = sampleWithRequiredData;
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing([], ligneTransfertStock);
        expect(expectedResult).toEqual([ligneTransfertStock]);
      });

      it('should not add a LigneTransfertStock to an array that contains it', () => {
        const ligneTransfertStock: ILigneTransfertStock = sampleWithRequiredData;
        const ligneTransfertStockCollection: ILigneTransfertStock[] = [
          {
            ...ligneTransfertStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing(ligneTransfertStockCollection, ligneTransfertStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneTransfertStock to an array that doesn't contain it", () => {
        const ligneTransfertStock: ILigneTransfertStock = sampleWithRequiredData;
        const ligneTransfertStockCollection: ILigneTransfertStock[] = [sampleWithPartialData];
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing(ligneTransfertStockCollection, ligneTransfertStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneTransfertStock);
      });

      it('should add only unique LigneTransfertStock to an array', () => {
        const ligneTransfertStockArray: ILigneTransfertStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneTransfertStockCollection: ILigneTransfertStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing(ligneTransfertStockCollection, ...ligneTransfertStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneTransfertStock: ILigneTransfertStock = sampleWithRequiredData;
        const ligneTransfertStock2: ILigneTransfertStock = sampleWithPartialData;
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing([], ligneTransfertStock, ligneTransfertStock2);
        expect(expectedResult).toEqual([ligneTransfertStock, ligneTransfertStock2]);
      });

      it('should accept null and undefined values', () => {
        const ligneTransfertStock: ILigneTransfertStock = sampleWithRequiredData;
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing([], null, ligneTransfertStock, undefined);
        expect(expectedResult).toEqual([ligneTransfertStock]);
      });

      it('should return initial array if no LigneTransfertStock is added', () => {
        const ligneTransfertStockCollection: ILigneTransfertStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneTransfertStockToCollectionIfMissing(ligneTransfertStockCollection, undefined, null);
        expect(expectedResult).toEqual(ligneTransfertStockCollection);
      });
    });

    describe('compareLigneTransfertStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneTransfertStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19112 };
        const entity2 = null;

        const compareResult1 = service.compareLigneTransfertStock(entity1, entity2);
        const compareResult2 = service.compareLigneTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19112 };
        const entity2 = { id: 1772 };

        const compareResult1 = service.compareLigneTransfertStock(entity1, entity2);
        const compareResult2 = service.compareLigneTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19112 };
        const entity2 = { id: 19112 };

        const compareResult1 = service.compareLigneTransfertStock(entity1, entity2);
        const compareResult2 = service.compareLigneTransfertStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
