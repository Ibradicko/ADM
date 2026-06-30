import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ITarifProduit } from '../tarif-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tarif-produit.test-samples';

import { RestTarifProduit, TarifProduitService } from './tarif-produit.service';

const requireRestSample: RestTarifProduit = {
  ...sampleWithRequiredData,
  dateDebut: sampleWithRequiredData.dateDebut?.format(DATE_FORMAT),
  dateFin: sampleWithRequiredData.dateFin?.format(DATE_FORMAT),
};

describe('TarifProduit Service', () => {
  let service: TarifProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: ITarifProduit | ITarifProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TarifProduitService);
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

    it('should create a TarifProduit', () => {
      const tarifProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tarifProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TarifProduit', () => {
      const tarifProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tarifProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TarifProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TarifProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TarifProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTarifProduitToCollectionIfMissing', () => {
      it('should add a TarifProduit to an empty array', () => {
        const tarifProduit: ITarifProduit = sampleWithRequiredData;
        expectedResult = service.addTarifProduitToCollectionIfMissing([], tarifProduit);
        expect(expectedResult).toEqual([tarifProduit]);
      });

      it('should not add a TarifProduit to an array that contains it', () => {
        const tarifProduit: ITarifProduit = sampleWithRequiredData;
        const tarifProduitCollection: ITarifProduit[] = [
          {
            ...tarifProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTarifProduitToCollectionIfMissing(tarifProduitCollection, tarifProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TarifProduit to an array that doesn't contain it", () => {
        const tarifProduit: ITarifProduit = sampleWithRequiredData;
        const tarifProduitCollection: ITarifProduit[] = [sampleWithPartialData];
        expectedResult = service.addTarifProduitToCollectionIfMissing(tarifProduitCollection, tarifProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tarifProduit);
      });

      it('should add only unique TarifProduit to an array', () => {
        const tarifProduitArray: ITarifProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tarifProduitCollection: ITarifProduit[] = [sampleWithRequiredData];
        expectedResult = service.addTarifProduitToCollectionIfMissing(tarifProduitCollection, ...tarifProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tarifProduit: ITarifProduit = sampleWithRequiredData;
        const tarifProduit2: ITarifProduit = sampleWithPartialData;
        expectedResult = service.addTarifProduitToCollectionIfMissing([], tarifProduit, tarifProduit2);
        expect(expectedResult).toEqual([tarifProduit, tarifProduit2]);
      });

      it('should accept null and undefined values', () => {
        const tarifProduit: ITarifProduit = sampleWithRequiredData;
        expectedResult = service.addTarifProduitToCollectionIfMissing([], null, tarifProduit, undefined);
        expect(expectedResult).toEqual([tarifProduit]);
      });

      it('should return initial array if no TarifProduit is added', () => {
        const tarifProduitCollection: ITarifProduit[] = [sampleWithRequiredData];
        expectedResult = service.addTarifProduitToCollectionIfMissing(tarifProduitCollection, undefined, null);
        expect(expectedResult).toEqual(tarifProduitCollection);
      });
    });

    describe('compareTarifProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTarifProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16387 };
        const entity2 = null;

        const compareResult1 = service.compareTarifProduit(entity1, entity2);
        const compareResult2 = service.compareTarifProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16387 };
        const entity2 = { id: 9822 };

        const compareResult1 = service.compareTarifProduit(entity1, entity2);
        const compareResult2 = service.compareTarifProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16387 };
        const entity2 = { id: 16387 };

        const compareResult1 = service.compareTarifProduit(entity1, entity2);
        const compareResult2 = service.compareTarifProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
