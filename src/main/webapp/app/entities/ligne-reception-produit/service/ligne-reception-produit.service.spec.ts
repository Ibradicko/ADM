import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneReceptionProduit } from '../ligne-reception-produit.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../ligne-reception-produit.test-samples';

import { LigneReceptionProduitService } from './ligne-reception-produit.service';

const requireRestSample: ILigneReceptionProduit = {
  ...sampleWithRequiredData,
};

describe('LigneReceptionProduit Service', () => {
  let service: LigneReceptionProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneReceptionProduit | ILigneReceptionProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneReceptionProduitService);
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

    it('should create a LigneReceptionProduit', () => {
      const ligneReceptionProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneReceptionProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneReceptionProduit', () => {
      const ligneReceptionProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneReceptionProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneReceptionProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneReceptionProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneReceptionProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneReceptionProduitToCollectionIfMissing', () => {
      it('should add a LigneReceptionProduit to an empty array', () => {
        const ligneReceptionProduit: ILigneReceptionProduit = sampleWithRequiredData;
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing([], ligneReceptionProduit);
        expect(expectedResult).toEqual([ligneReceptionProduit]);
      });

      it('should not add a LigneReceptionProduit to an array that contains it', () => {
        const ligneReceptionProduit: ILigneReceptionProduit = sampleWithRequiredData;
        const ligneReceptionProduitCollection: ILigneReceptionProduit[] = [
          {
            ...ligneReceptionProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing(ligneReceptionProduitCollection, ligneReceptionProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneReceptionProduit to an array that doesn't contain it", () => {
        const ligneReceptionProduit: ILigneReceptionProduit = sampleWithRequiredData;
        const ligneReceptionProduitCollection: ILigneReceptionProduit[] = [sampleWithPartialData];
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing(ligneReceptionProduitCollection, ligneReceptionProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneReceptionProduit);
      });

      it('should add only unique LigneReceptionProduit to an array', () => {
        const ligneReceptionProduitArray: ILigneReceptionProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneReceptionProduitCollection: ILigneReceptionProduit[] = [sampleWithRequiredData];
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing(
          ligneReceptionProduitCollection,
          ...ligneReceptionProduitArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneReceptionProduit: ILigneReceptionProduit = sampleWithRequiredData;
        const ligneReceptionProduit2: ILigneReceptionProduit = sampleWithPartialData;
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing([], ligneReceptionProduit, ligneReceptionProduit2);
        expect(expectedResult).toEqual([ligneReceptionProduit, ligneReceptionProduit2]);
      });

      it('should accept null and undefined values', () => {
        const ligneReceptionProduit: ILigneReceptionProduit = sampleWithRequiredData;
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing([], null, ligneReceptionProduit, undefined);
        expect(expectedResult).toEqual([ligneReceptionProduit]);
      });

      it('should return initial array if no LigneReceptionProduit is added', () => {
        const ligneReceptionProduitCollection: ILigneReceptionProduit[] = [sampleWithRequiredData];
        expectedResult = service.addLigneReceptionProduitToCollectionIfMissing(ligneReceptionProduitCollection, undefined, null);
        expect(expectedResult).toEqual(ligneReceptionProduitCollection);
      });
    });

    describe('compareLigneReceptionProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneReceptionProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9739 };
        const entity2 = null;

        const compareResult1 = service.compareLigneReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareLigneReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9739 };
        const entity2 = { id: 14106 };

        const compareResult1 = service.compareLigneReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareLigneReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9739 };
        const entity2 = { id: 9739 };

        const compareResult1 = service.compareLigneReceptionProduit(entity1, entity2);
        const compareResult2 = service.compareLigneReceptionProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
