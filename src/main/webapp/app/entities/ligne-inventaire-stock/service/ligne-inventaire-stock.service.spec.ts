import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneInventaireStock } from '../ligne-inventaire-stock.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../ligne-inventaire-stock.test-samples';

import { LigneInventaireStockService } from './ligne-inventaire-stock.service';

const requireRestSample: ILigneInventaireStock = {
  ...sampleWithRequiredData,
};

describe('LigneInventaireStock Service', () => {
  let service: LigneInventaireStockService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneInventaireStock | ILigneInventaireStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneInventaireStockService);
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

    it('should create a LigneInventaireStock', () => {
      const ligneInventaireStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneInventaireStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneInventaireStock', () => {
      const ligneInventaireStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneInventaireStock).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneInventaireStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneInventaireStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneInventaireStock', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneInventaireStockToCollectionIfMissing', () => {
      it('should add a LigneInventaireStock to an empty array', () => {
        const ligneInventaireStock: ILigneInventaireStock = sampleWithRequiredData;
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing([], ligneInventaireStock);
        expect(expectedResult).toEqual([ligneInventaireStock]);
      });

      it('should not add a LigneInventaireStock to an array that contains it', () => {
        const ligneInventaireStock: ILigneInventaireStock = sampleWithRequiredData;
        const ligneInventaireStockCollection: ILigneInventaireStock[] = [
          {
            ...ligneInventaireStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing(ligneInventaireStockCollection, ligneInventaireStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneInventaireStock to an array that doesn't contain it", () => {
        const ligneInventaireStock: ILigneInventaireStock = sampleWithRequiredData;
        const ligneInventaireStockCollection: ILigneInventaireStock[] = [sampleWithPartialData];
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing(ligneInventaireStockCollection, ligneInventaireStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneInventaireStock);
      });

      it('should add only unique LigneInventaireStock to an array', () => {
        const ligneInventaireStockArray: ILigneInventaireStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneInventaireStockCollection: ILigneInventaireStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing(ligneInventaireStockCollection, ...ligneInventaireStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneInventaireStock: ILigneInventaireStock = sampleWithRequiredData;
        const ligneInventaireStock2: ILigneInventaireStock = sampleWithPartialData;
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing([], ligneInventaireStock, ligneInventaireStock2);
        expect(expectedResult).toEqual([ligneInventaireStock, ligneInventaireStock2]);
      });

      it('should accept null and undefined values', () => {
        const ligneInventaireStock: ILigneInventaireStock = sampleWithRequiredData;
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing([], null, ligneInventaireStock, undefined);
        expect(expectedResult).toEqual([ligneInventaireStock]);
      });

      it('should return initial array if no LigneInventaireStock is added', () => {
        const ligneInventaireStockCollection: ILigneInventaireStock[] = [sampleWithRequiredData];
        expectedResult = service.addLigneInventaireStockToCollectionIfMissing(ligneInventaireStockCollection, undefined, null);
        expect(expectedResult).toEqual(ligneInventaireStockCollection);
      });
    });

    describe('compareLigneInventaireStock', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneInventaireStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20417 };
        const entity2 = null;

        const compareResult1 = service.compareLigneInventaireStock(entity1, entity2);
        const compareResult2 = service.compareLigneInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20417 };
        const entity2 = { id: 1239 };

        const compareResult1 = service.compareLigneInventaireStock(entity1, entity2);
        const compareResult2 = service.compareLigneInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20417 };
        const entity2 = { id: 20417 };

        const compareResult1 = service.compareLigneInventaireStock(entity1, entity2);
        const compareResult2 = service.compareLigneInventaireStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
