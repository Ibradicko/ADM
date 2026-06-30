import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPermissionMetier } from '../permission-metier.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../permission-metier.test-samples';

import { PermissionMetierService } from './permission-metier.service';

const requireRestSample: IPermissionMetier = {
  ...sampleWithRequiredData,
};

describe('PermissionMetier Service', () => {
  let service: PermissionMetierService;
  let httpMock: HttpTestingController;
  let expectedResult: IPermissionMetier | IPermissionMetier[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PermissionMetierService);
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

    it('should create a PermissionMetier', () => {
      const permissionMetier = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(permissionMetier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PermissionMetier', () => {
      const permissionMetier = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(permissionMetier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PermissionMetier', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PermissionMetier', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PermissionMetier', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPermissionMetierToCollectionIfMissing', () => {
      it('should add a PermissionMetier to an empty array', () => {
        const permissionMetier: IPermissionMetier = sampleWithRequiredData;
        expectedResult = service.addPermissionMetierToCollectionIfMissing([], permissionMetier);
        expect(expectedResult).toEqual([permissionMetier]);
      });

      it('should not add a PermissionMetier to an array that contains it', () => {
        const permissionMetier: IPermissionMetier = sampleWithRequiredData;
        const permissionMetierCollection: IPermissionMetier[] = [
          {
            ...permissionMetier,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPermissionMetierToCollectionIfMissing(permissionMetierCollection, permissionMetier);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PermissionMetier to an array that doesn't contain it", () => {
        const permissionMetier: IPermissionMetier = sampleWithRequiredData;
        const permissionMetierCollection: IPermissionMetier[] = [sampleWithPartialData];
        expectedResult = service.addPermissionMetierToCollectionIfMissing(permissionMetierCollection, permissionMetier);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permissionMetier);
      });

      it('should add only unique PermissionMetier to an array', () => {
        const permissionMetierArray: IPermissionMetier[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const permissionMetierCollection: IPermissionMetier[] = [sampleWithRequiredData];
        expectedResult = service.addPermissionMetierToCollectionIfMissing(permissionMetierCollection, ...permissionMetierArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const permissionMetier: IPermissionMetier = sampleWithRequiredData;
        const permissionMetier2: IPermissionMetier = sampleWithPartialData;
        expectedResult = service.addPermissionMetierToCollectionIfMissing([], permissionMetier, permissionMetier2);
        expect(expectedResult).toEqual([permissionMetier, permissionMetier2]);
      });

      it('should accept null and undefined values', () => {
        const permissionMetier: IPermissionMetier = sampleWithRequiredData;
        expectedResult = service.addPermissionMetierToCollectionIfMissing([], null, permissionMetier, undefined);
        expect(expectedResult).toEqual([permissionMetier]);
      });

      it('should return initial array if no PermissionMetier is added', () => {
        const permissionMetierCollection: IPermissionMetier[] = [sampleWithRequiredData];
        expectedResult = service.addPermissionMetierToCollectionIfMissing(permissionMetierCollection, undefined, null);
        expect(expectedResult).toEqual(permissionMetierCollection);
      });
    });

    describe('comparePermissionMetier', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePermissionMetier(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28695 };
        const entity2 = null;

        const compareResult1 = service.comparePermissionMetier(entity1, entity2);
        const compareResult2 = service.comparePermissionMetier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28695 };
        const entity2 = { id: 30575 };

        const compareResult1 = service.comparePermissionMetier(entity1, entity2);
        const compareResult2 = service.comparePermissionMetier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28695 };
        const entity2 = { id: 28695 };

        const compareResult1 = service.comparePermissionMetier(entity1, entity2);
        const compareResult2 = service.comparePermissionMetier(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
