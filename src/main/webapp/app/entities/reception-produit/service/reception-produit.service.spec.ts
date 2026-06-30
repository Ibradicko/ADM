import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IReceptionProduit } from '../reception-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../reception-produit.test-samples';

import { ReceptionProduitService, RestReceptionProduit } from './reception-produit.service';

const requireRestSample: RestReceptionProduit = {
  ...sampleWithRequiredData,
  dateReception: sampleWithRequiredData.dateReception?.toJSON(),
};

describe('ReceptionProduit Service', () => {
  let service: ReceptionProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: IReceptionProduit | IReceptionProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ReceptionProduitService);
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

    it('should create a ReceptionProduit', () => {
      const receptionProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(receptionProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReceptionProduit', () => {
      const receptionProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(receptionProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReceptionProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReceptionProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReceptionProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addReceptionProduitToCollectionIfMissing', () => {
      it('should add a ReceptionProduit to an empty array', () => {
        const receptionProduit: IReceptionProduit = sampleWithRequiredData;
        expectedResult = service.addReceptionProduitToCollectionIfMissing([], receptionProduit);
        expect(expectedResult).toEqual([receptionProduit]);
      });

      it('should not add a ReceptionProduit to an array that contains it', () => {
        const receptionProduit: IReceptionProduit = sampleWithRequiredData;
        const receptionProduitCollection: IReceptionProduit[] = [
          {
            ...receptionProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReceptionProduitToCollectionIfMissing(receptionProduitCollection, receptionProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReceptionProduit to an array that doesn't contain it", () => {
        const receptionProduit: IReceptionProduit = sampleWithRequiredData;
        const receptionProduitCollection: IReceptionProduit[] = [sampleWithPartialData];
        expectedResult = service.addReceptionProduitToCollectionIfMissing(receptionProduitCollection, receptionProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(receptionProduit);
      });

      it('should add only unique ReceptionProduit to an array', () => {
        const receptionProduitArray: IReceptionProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const receptionProduitCollection: IReceptionProduit[] = [sampleWithRequiredData];
        expectedResult = service.addReceptionProduitToCollectionIfMissing(receptionProduitCollection, ...receptionProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const receptionProduit: IReceptionProduit = sampleWithRequiredData;
        const receptionProduit2: IReceptionProduit = sampleWithPartialData;
        expectedResult = service.addReceptionProduitToCollectionIfMissing([], receptionProduit, receptionProduit2);
        expect(expectedResult).toEqual([receptionProduit, receptionProduit2]);
      });

      it('should accept null and undefined values', () => {
        const receptionProduit: IReceptionProduit = sampleWithRequiredData;
        expectedResult = service.addReceptionProduitToCollectionIfMissing([], null, receptionProduit, undefined);
        expect(expectedResult).toEqual([receptionProduit]);
      });

      it('should return initial array if no ReceptionProduit is added', () => {
        const receptionProduitCollection: IReceptionProduit[] = [sampleWithRequiredData];
        expectedResult = service.addReceptionProduitToCollectionIfMissing(receptionProduitCollection, undefined, null);
        expect(expectedResult).toEqual(receptionProduitCollection);
      });
    });

    describe('compareReceptionProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReceptionProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19661 };
        const entity2 = null;

        const compareResult1 = service.compareReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19661 };
        const entity2 = { id: 1742 };

        const compareResult1 = service.compareReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19661 };
        const entity2 = { id: 19661 };

        const compareResult1 = service.compareReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
