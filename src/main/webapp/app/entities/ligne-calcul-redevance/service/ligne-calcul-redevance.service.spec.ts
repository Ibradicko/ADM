import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneCalculRedevance } from '../ligne-calcul-redevance.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../ligne-calcul-redevance.test-samples';

import { LigneCalculRedevanceService } from './ligne-calcul-redevance.service';

const requireRestSample: ILigneCalculRedevance = {
  ...sampleWithRequiredData,
};

describe('LigneCalculRedevance Service', () => {
  let service: LigneCalculRedevanceService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneCalculRedevance | ILigneCalculRedevance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneCalculRedevanceService);
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

    it('should create a LigneCalculRedevance', () => {
      const ligneCalculRedevance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneCalculRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneCalculRedevance', () => {
      const ligneCalculRedevance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneCalculRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneCalculRedevance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneCalculRedevance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneCalculRedevance', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneCalculRedevanceToCollectionIfMissing', () => {
      it('should add a LigneCalculRedevance to an empty array', () => {
        const ligneCalculRedevance: ILigneCalculRedevance = sampleWithRequiredData;
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing([], ligneCalculRedevance);
        expect(expectedResult).toEqual([ligneCalculRedevance]);
      });

      it('should not add a LigneCalculRedevance to an array that contains it', () => {
        const ligneCalculRedevance: ILigneCalculRedevance = sampleWithRequiredData;
        const ligneCalculRedevanceCollection: ILigneCalculRedevance[] = [
          {
            ...ligneCalculRedevance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing(ligneCalculRedevanceCollection, ligneCalculRedevance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneCalculRedevance to an array that doesn't contain it", () => {
        const ligneCalculRedevance: ILigneCalculRedevance = sampleWithRequiredData;
        const ligneCalculRedevanceCollection: ILigneCalculRedevance[] = [sampleWithPartialData];
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing(ligneCalculRedevanceCollection, ligneCalculRedevance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneCalculRedevance);
      });

      it('should add only unique LigneCalculRedevance to an array', () => {
        const ligneCalculRedevanceArray: ILigneCalculRedevance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneCalculRedevanceCollection: ILigneCalculRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing(ligneCalculRedevanceCollection, ...ligneCalculRedevanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneCalculRedevance: ILigneCalculRedevance = sampleWithRequiredData;
        const ligneCalculRedevance2: ILigneCalculRedevance = sampleWithPartialData;
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing([], ligneCalculRedevance, ligneCalculRedevance2);
        expect(expectedResult).toEqual([ligneCalculRedevance, ligneCalculRedevance2]);
      });

      it('should accept null and undefined values', () => {
        const ligneCalculRedevance: ILigneCalculRedevance = sampleWithRequiredData;
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing([], null, ligneCalculRedevance, undefined);
        expect(expectedResult).toEqual([ligneCalculRedevance]);
      });

      it('should return initial array if no LigneCalculRedevance is added', () => {
        const ligneCalculRedevanceCollection: ILigneCalculRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addLigneCalculRedevanceToCollectionIfMissing(ligneCalculRedevanceCollection, undefined, null);
        expect(expectedResult).toEqual(ligneCalculRedevanceCollection);
      });
    });

    describe('compareLigneCalculRedevance', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneCalculRedevance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14249 };
        const entity2 = null;

        const compareResult1 = service.compareLigneCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareLigneCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14249 };
        const entity2 = { id: 29287 };

        const compareResult1 = service.compareLigneCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareLigneCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14249 };
        const entity2 = { id: 14249 };

        const compareResult1 = service.compareLigneCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareLigneCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
