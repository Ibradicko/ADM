import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfilMetier } from '../profil-metier.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../profil-metier.test-samples';

import { ProfilMetierService } from './profil-metier.service';

const requireRestSample: IProfilMetier = {
  ...sampleWithRequiredData,
};

describe('ProfilMetier Service', () => {
  let service: ProfilMetierService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfilMetier | IProfilMetier[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfilMetierService);
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

    it('should create a ProfilMetier', () => {
      const profilMetier = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profilMetier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfilMetier', () => {
      const profilMetier = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profilMetier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfilMetier', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfilMetier', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfilMetier', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfilMetierToCollectionIfMissing', () => {
      it('should add a ProfilMetier to an empty array', () => {
        const profilMetier: IProfilMetier = sampleWithRequiredData;
        expectedResult = service.addProfilMetierToCollectionIfMissing([], profilMetier);
        expect(expectedResult).toEqual([profilMetier]);
      });

      it('should not add a ProfilMetier to an array that contains it', () => {
        const profilMetier: IProfilMetier = sampleWithRequiredData;
        const profilMetierCollection: IProfilMetier[] = [
          {
            ...profilMetier,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfilMetierToCollectionIfMissing(profilMetierCollection, profilMetier);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfilMetier to an array that doesn't contain it", () => {
        const profilMetier: IProfilMetier = sampleWithRequiredData;
        const profilMetierCollection: IProfilMetier[] = [sampleWithPartialData];
        expectedResult = service.addProfilMetierToCollectionIfMissing(profilMetierCollection, profilMetier);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profilMetier);
      });

      it('should add only unique ProfilMetier to an array', () => {
        const profilMetierArray: IProfilMetier[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profilMetierCollection: IProfilMetier[] = [sampleWithRequiredData];
        expectedResult = service.addProfilMetierToCollectionIfMissing(profilMetierCollection, ...profilMetierArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profilMetier: IProfilMetier = sampleWithRequiredData;
        const profilMetier2: IProfilMetier = sampleWithPartialData;
        expectedResult = service.addProfilMetierToCollectionIfMissing([], profilMetier, profilMetier2);
        expect(expectedResult).toEqual([profilMetier, profilMetier2]);
      });

      it('should accept null and undefined values', () => {
        const profilMetier: IProfilMetier = sampleWithRequiredData;
        expectedResult = service.addProfilMetierToCollectionIfMissing([], null, profilMetier, undefined);
        expect(expectedResult).toEqual([profilMetier]);
      });

      it('should return initial array if no ProfilMetier is added', () => {
        const profilMetierCollection: IProfilMetier[] = [sampleWithRequiredData];
        expectedResult = service.addProfilMetierToCollectionIfMissing(profilMetierCollection, undefined, null);
        expect(expectedResult).toEqual(profilMetierCollection);
      });
    });

    describe('compareProfilMetier', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfilMetier(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12096 };
        const entity2 = null;

        const compareResult1 = service.compareProfilMetier(entity1, entity2);
        const compareResult2 = service.compareProfilMetier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12096 };
        const entity2 = { id: 25052 };

        const compareResult1 = service.compareProfilMetier(entity1, entity2);
        const compareResult2 = service.compareProfilMetier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12096 };
        const entity2 = { id: 12096 };

        const compareResult1 = service.compareProfilMetier(entity1, entity2);
        const compareResult2 = service.compareProfilMetier(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
