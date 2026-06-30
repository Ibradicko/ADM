import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ICalculRedevance } from '../calcul-redevance.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../calcul-redevance.test-samples';

import { CalculRedevanceService, RestCalculRedevance } from './calcul-redevance.service';

const requireRestSample: RestCalculRedevance = {
  ...sampleWithRequiredData,
  periodeDebut: sampleWithRequiredData.periodeDebut?.format(DATE_FORMAT),
  periodeFin: sampleWithRequiredData.periodeFin?.format(DATE_FORMAT),
  dateCalcul: sampleWithRequiredData.dateCalcul?.toJSON(),
};

describe('CalculRedevance Service', () => {
  let service: CalculRedevanceService;
  let httpMock: HttpTestingController;
  let expectedResult: ICalculRedevance | ICalculRedevance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CalculRedevanceService);
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

    it('should create a CalculRedevance', () => {
      const calculRedevance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(calculRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CalculRedevance', () => {
      const calculRedevance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(calculRedevance).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CalculRedevance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CalculRedevance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CalculRedevance', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCalculRedevanceToCollectionIfMissing', () => {
      it('should add a CalculRedevance to an empty array', () => {
        const calculRedevance: ICalculRedevance = sampleWithRequiredData;
        expectedResult = service.addCalculRedevanceToCollectionIfMissing([], calculRedevance);
        expect(expectedResult).toEqual([calculRedevance]);
      });

      it('should not add a CalculRedevance to an array that contains it', () => {
        const calculRedevance: ICalculRedevance = sampleWithRequiredData;
        const calculRedevanceCollection: ICalculRedevance[] = [
          {
            ...calculRedevance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCalculRedevanceToCollectionIfMissing(calculRedevanceCollection, calculRedevance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CalculRedevance to an array that doesn't contain it", () => {
        const calculRedevance: ICalculRedevance = sampleWithRequiredData;
        const calculRedevanceCollection: ICalculRedevance[] = [sampleWithPartialData];
        expectedResult = service.addCalculRedevanceToCollectionIfMissing(calculRedevanceCollection, calculRedevance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(calculRedevance);
      });

      it('should add only unique CalculRedevance to an array', () => {
        const calculRedevanceArray: ICalculRedevance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const calculRedevanceCollection: ICalculRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addCalculRedevanceToCollectionIfMissing(calculRedevanceCollection, ...calculRedevanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const calculRedevance: ICalculRedevance = sampleWithRequiredData;
        const calculRedevance2: ICalculRedevance = sampleWithPartialData;
        expectedResult = service.addCalculRedevanceToCollectionIfMissing([], calculRedevance, calculRedevance2);
        expect(expectedResult).toEqual([calculRedevance, calculRedevance2]);
      });

      it('should accept null and undefined values', () => {
        const calculRedevance: ICalculRedevance = sampleWithRequiredData;
        expectedResult = service.addCalculRedevanceToCollectionIfMissing([], null, calculRedevance, undefined);
        expect(expectedResult).toEqual([calculRedevance]);
      });

      it('should return initial array if no CalculRedevance is added', () => {
        const calculRedevanceCollection: ICalculRedevance[] = [sampleWithRequiredData];
        expectedResult = service.addCalculRedevanceToCollectionIfMissing(calculRedevanceCollection, undefined, null);
        expect(expectedResult).toEqual(calculRedevanceCollection);
      });
    });

    describe('compareCalculRedevance', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCalculRedevance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28461 };
        const entity2 = null;

        const compareResult1 = service.compareCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28461 };
        const entity2 = { id: 4867 };

        const compareResult1 = service.compareCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28461 };
        const entity2 = { id: 28461 };

        const compareResult1 = service.compareCalculRedevance(entity1, entity2);
        const compareResult2 = service.compareCalculRedevance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
