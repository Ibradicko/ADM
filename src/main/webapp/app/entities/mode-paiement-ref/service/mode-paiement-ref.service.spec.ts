import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IModePaiementRef } from '../mode-paiement-ref.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mode-paiement-ref.test-samples';

import { ModePaiementRefService } from './mode-paiement-ref.service';

const requireRestSample: IModePaiementRef = {
  ...sampleWithRequiredData,
};

describe('ModePaiementRef Service', () => {
  let service: ModePaiementRefService;
  let httpMock: HttpTestingController;
  let expectedResult: IModePaiementRef | IModePaiementRef[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ModePaiementRefService);
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

    it('should create a ModePaiementRef', () => {
      const modePaiementRef = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(modePaiementRef).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ModePaiementRef', () => {
      const modePaiementRef = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(modePaiementRef).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ModePaiementRef', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ModePaiementRef', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ModePaiementRef', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addModePaiementRefToCollectionIfMissing', () => {
      it('should add a ModePaiementRef to an empty array', () => {
        const modePaiementRef: IModePaiementRef = sampleWithRequiredData;
        expectedResult = service.addModePaiementRefToCollectionIfMissing([], modePaiementRef);
        expect(expectedResult).toEqual([modePaiementRef]);
      });

      it('should not add a ModePaiementRef to an array that contains it', () => {
        const modePaiementRef: IModePaiementRef = sampleWithRequiredData;
        const modePaiementRefCollection: IModePaiementRef[] = [
          {
            ...modePaiementRef,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addModePaiementRefToCollectionIfMissing(modePaiementRefCollection, modePaiementRef);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ModePaiementRef to an array that doesn't contain it", () => {
        const modePaiementRef: IModePaiementRef = sampleWithRequiredData;
        const modePaiementRefCollection: IModePaiementRef[] = [sampleWithPartialData];
        expectedResult = service.addModePaiementRefToCollectionIfMissing(modePaiementRefCollection, modePaiementRef);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(modePaiementRef);
      });

      it('should add only unique ModePaiementRef to an array', () => {
        const modePaiementRefArray: IModePaiementRef[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const modePaiementRefCollection: IModePaiementRef[] = [sampleWithRequiredData];
        expectedResult = service.addModePaiementRefToCollectionIfMissing(modePaiementRefCollection, ...modePaiementRefArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const modePaiementRef: IModePaiementRef = sampleWithRequiredData;
        const modePaiementRef2: IModePaiementRef = sampleWithPartialData;
        expectedResult = service.addModePaiementRefToCollectionIfMissing([], modePaiementRef, modePaiementRef2);
        expect(expectedResult).toEqual([modePaiementRef, modePaiementRef2]);
      });

      it('should accept null and undefined values', () => {
        const modePaiementRef: IModePaiementRef = sampleWithRequiredData;
        expectedResult = service.addModePaiementRefToCollectionIfMissing([], null, modePaiementRef, undefined);
        expect(expectedResult).toEqual([modePaiementRef]);
      });

      it('should return initial array if no ModePaiementRef is added', () => {
        const modePaiementRefCollection: IModePaiementRef[] = [sampleWithRequiredData];
        expectedResult = service.addModePaiementRefToCollectionIfMissing(modePaiementRefCollection, undefined, null);
        expect(expectedResult).toEqual(modePaiementRefCollection);
      });
    });

    describe('compareModePaiementRef', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareModePaiementRef(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14388 };
        const entity2 = null;

        const compareResult1 = service.compareModePaiementRef(entity1, entity2);
        const compareResult2 = service.compareModePaiementRef(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14388 };
        const entity2 = { id: 1636 };

        const compareResult1 = service.compareModePaiementRef(entity1, entity2);
        const compareResult2 = service.compareModePaiementRef(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14388 };
        const entity2 = { id: 14388 };

        const compareResult1 = service.compareModePaiementRef(entity1, entity2);
        const compareResult2 = service.compareModePaiementRef(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
