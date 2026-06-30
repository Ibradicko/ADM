import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IParametreGlobal } from '../parametre-global.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../parametre-global.test-samples';

import { ParametreGlobalService } from './parametre-global.service';

const requireRestSample: IParametreGlobal = {
  ...sampleWithRequiredData,
};

describe('ParametreGlobal Service', () => {
  let service: ParametreGlobalService;
  let httpMock: HttpTestingController;
  let expectedResult: IParametreGlobal | IParametreGlobal[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ParametreGlobalService);
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

    it('should create a ParametreGlobal', () => {
      const parametreGlobal = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(parametreGlobal).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ParametreGlobal', () => {
      const parametreGlobal = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(parametreGlobal).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ParametreGlobal', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ParametreGlobal', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ParametreGlobal', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addParametreGlobalToCollectionIfMissing', () => {
      it('should add a ParametreGlobal to an empty array', () => {
        const parametreGlobal: IParametreGlobal = sampleWithRequiredData;
        expectedResult = service.addParametreGlobalToCollectionIfMissing([], parametreGlobal);
        expect(expectedResult).toEqual([parametreGlobal]);
      });

      it('should not add a ParametreGlobal to an array that contains it', () => {
        const parametreGlobal: IParametreGlobal = sampleWithRequiredData;
        const parametreGlobalCollection: IParametreGlobal[] = [
          {
            ...parametreGlobal,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addParametreGlobalToCollectionIfMissing(parametreGlobalCollection, parametreGlobal);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ParametreGlobal to an array that doesn't contain it", () => {
        const parametreGlobal: IParametreGlobal = sampleWithRequiredData;
        const parametreGlobalCollection: IParametreGlobal[] = [sampleWithPartialData];
        expectedResult = service.addParametreGlobalToCollectionIfMissing(parametreGlobalCollection, parametreGlobal);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parametreGlobal);
      });

      it('should add only unique ParametreGlobal to an array', () => {
        const parametreGlobalArray: IParametreGlobal[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const parametreGlobalCollection: IParametreGlobal[] = [sampleWithRequiredData];
        expectedResult = service.addParametreGlobalToCollectionIfMissing(parametreGlobalCollection, ...parametreGlobalArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const parametreGlobal: IParametreGlobal = sampleWithRequiredData;
        const parametreGlobal2: IParametreGlobal = sampleWithPartialData;
        expectedResult = service.addParametreGlobalToCollectionIfMissing([], parametreGlobal, parametreGlobal2);
        expect(expectedResult).toEqual([parametreGlobal, parametreGlobal2]);
      });

      it('should accept null and undefined values', () => {
        const parametreGlobal: IParametreGlobal = sampleWithRequiredData;
        expectedResult = service.addParametreGlobalToCollectionIfMissing([], null, parametreGlobal, undefined);
        expect(expectedResult).toEqual([parametreGlobal]);
      });

      it('should return initial array if no ParametreGlobal is added', () => {
        const parametreGlobalCollection: IParametreGlobal[] = [sampleWithRequiredData];
        expectedResult = service.addParametreGlobalToCollectionIfMissing(parametreGlobalCollection, undefined, null);
        expect(expectedResult).toEqual(parametreGlobalCollection);
      });
    });

    describe('compareParametreGlobal', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareParametreGlobal(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 778 };
        const entity2 = null;

        const compareResult1 = service.compareParametreGlobal(entity1, entity2);
        const compareResult2 = service.compareParametreGlobal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 778 };
        const entity2 = { id: 11141 };

        const compareResult1 = service.compareParametreGlobal(entity1, entity2);
        const compareResult2 = service.compareParametreGlobal(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 778 };
        const entity2 = { id: 778 };

        const compareResult1 = service.compareParametreGlobal(entity1, entity2);
        const compareResult2 = service.compareParametreGlobal(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
