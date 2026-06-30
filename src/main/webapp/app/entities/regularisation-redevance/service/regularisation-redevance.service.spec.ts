import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRegularisationRedevance } from '../regularisation-redevance.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../regularisation-redevance.test-samples';

import { RegularisationRedevanceService, RestRegularisationRedevance } from './regularisation-redevance.service';

const requireRestSample: RestRegularisationRedevance = {
  ...sampleWithRequiredData,
  dateRegularisation: sampleWithRequiredData.dateRegularisation?.toJSON(),
};

describe('RegularisationRedevance Service', () => {
  let service: RegularisationRedevanceService;
  let httpMock: HttpTestingController;
  let expectedResult: IRegularisationRedevance | IRegularisationRedevance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RegularisationRedevanceService);
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

    it('should create a RegularisationRedevance', () => {
      const regularisationRedevance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(regularisationRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RegularisationRedevance', () => {
      const regularisationRedevance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(regularisationRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RegularisationRedevance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RegularisationRedevance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RegularisationRedevance', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addRegularisationRedevanceToCollectionIfMissing', () => {
      it('should add a RegularisationRedevance to an empty array', () => {
        const regularisationRedevance: IRegularisationRedevance = sampleWithRequiredData;
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing([], regularisationRedevance);
        expect(expectedResult).toEqual([regularisationRedevance]);
      });

      it('should not add a RegularisationRedevance to an array that contains it', () => {
        const regularisationRedevance: IRegularisationRedevance = sampleWithRequiredData;
        const regularisationRedevanceCollection: IRegularisationRedevance[] = [
          {
            ...regularisationRedevance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing(
          regularisationRedevanceCollection,
          regularisationRedevance,
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RegularisationRedevance to an array that doesn't contain it", () => {
        const regularisationRedevance: IRegularisationRedevance = sampleWithRequiredData;
        const regularisationRedevanceCollection: IRegularisationRedevance[] = [sampleWithPartialData];
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing(
          regularisationRedevanceCollection,
          regularisationRedevance,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(regularisationRedevance);
      });

      it('should add only unique RegularisationRedevance to an array', () => {
        const regularisationRedevanceArray: IRegularisationRedevance[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const regularisationRedevanceCollection: IRegularisationRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing(
          regularisationRedevanceCollection,
          ...regularisationRedevanceArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const regularisationRedevance: IRegularisationRedevance = sampleWithRequiredData;
        const regularisationRedevance2: IRegularisationRedevance = sampleWithPartialData;
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing([], regularisationRedevance, regularisationRedevance2);
        expect(expectedResult).toEqual([regularisationRedevance, regularisationRedevance2]);
      });

      it('should accept null and undefined values', () => {
        const regularisationRedevance: IRegularisationRedevance = sampleWithRequiredData;
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing([], null, regularisationRedevance, undefined);
        expect(expectedResult).toEqual([regularisationRedevance]);
      });

      it('should return initial array if no RegularisationRedevance is added', () => {
        const regularisationRedevanceCollection: IRegularisationRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addRegularisationRedevanceToCollectionIfMissing(regularisationRedevanceCollection, undefined, null);
        expect(expectedResult).toEqual(regularisationRedevanceCollection);
      });
    });

    describe('compareRegularisationRedevance', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRegularisationRedevance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29254 };
        const entity2 = null;

        const compareResult1 = service.compareRegularisationRedevance(entity1, entity2);
        const compareResult2 = service.compareRegularisationRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29254 };
        const entity2 = { id: 26564 };

        const compareResult1 = service.compareRegularisationRedevance(entity1, entity2);
        const compareResult2 = service.compareRegularisationRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29254 };
        const entity2 = { id: 29254 };

        const compareResult1 = service.compareRegularisationRedevance(entity1, entity2);
        const compareResult2 = service.compareRegularisationRedevance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
