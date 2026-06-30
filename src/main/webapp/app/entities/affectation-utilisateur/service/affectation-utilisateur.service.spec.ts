import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IAffectationUtilisateur } from '../affectation-utilisateur.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../affectation-utilisateur.test-samples';

import { AffectationUtilisateurService, RestAffectationUtilisateur } from './affectation-utilisateur.service';

const requireRestSample: RestAffectationUtilisateur = {
  ...sampleWithRequiredData,
  dateDebut: sampleWithRequiredData.dateDebut?.format(DATE_FORMAT),
  dateFin: sampleWithRequiredData.dateFin?.format(DATE_FORMAT),
};

describe('AffectationUtilisateur Service', () => {
  let service: AffectationUtilisateurService;
  let httpMock: HttpTestingController;
  let expectedResult: IAffectationUtilisateur | IAffectationUtilisateur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AffectationUtilisateurService);
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

    it('should create a AffectationUtilisateur', () => {
      const affectationUtilisateur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(affectationUtilisateur).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AffectationUtilisateur', () => {
      const affectationUtilisateur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(affectationUtilisateur).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AffectationUtilisateur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AffectationUtilisateur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AffectationUtilisateur', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addAffectationUtilisateurToCollectionIfMissing', () => {
      it('should add a AffectationUtilisateur to an empty array', () => {
        const affectationUtilisateur: IAffectationUtilisateur = sampleWithRequiredData;
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing([], affectationUtilisateur);
        expect(expectedResult).toEqual([affectationUtilisateur]);
      });

      it('should not add a AffectationUtilisateur to an array that contains it', () => {
        const affectationUtilisateur: IAffectationUtilisateur = sampleWithRequiredData;
        const affectationUtilisateurCollection: IAffectationUtilisateur[] = [
          {
            ...affectationUtilisateur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing(affectationUtilisateurCollection, affectationUtilisateur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AffectationUtilisateur to an array that doesn't contain it", () => {
        const affectationUtilisateur: IAffectationUtilisateur = sampleWithRequiredData;
        const affectationUtilisateurCollection: IAffectationUtilisateur[] = [sampleWithPartialData];
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing(affectationUtilisateurCollection, affectationUtilisateur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(affectationUtilisateur);
      });

      it('should add only unique AffectationUtilisateur to an array', () => {
        const affectationUtilisateurArray: IAffectationUtilisateur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const affectationUtilisateurCollection: IAffectationUtilisateur[] = [sampleWithRequiredData];
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing(
          affectationUtilisateurCollection,
          ...affectationUtilisateurArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const affectationUtilisateur: IAffectationUtilisateur = sampleWithRequiredData;
        const affectationUtilisateur2: IAffectationUtilisateur = sampleWithPartialData;
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing([], affectationUtilisateur, affectationUtilisateur2);
        expect(expectedResult).toEqual([affectationUtilisateur, affectationUtilisateur2]);
      });

      it('should accept null and undefined values', () => {
        const affectationUtilisateur: IAffectationUtilisateur = sampleWithRequiredData;
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing([], null, affectationUtilisateur, undefined);
        expect(expectedResult).toEqual([affectationUtilisateur]);
      });

      it('should return initial array if no AffectationUtilisateur is added', () => {
        const affectationUtilisateurCollection: IAffectationUtilisateur[] = [sampleWithRequiredData];
        expectedResult = service.addAffectationUtilisateurToCollectionIfMissing(affectationUtilisateurCollection, undefined, null);
        expect(expectedResult).toEqual(affectationUtilisateurCollection);
      });
    });

    describe('compareAffectationUtilisateur', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAffectationUtilisateur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4001 };
        const entity2 = null;

        const compareResult1 = service.compareAffectationUtilisateur(entity1, entity2);
        const compareResult2 = service.compareAffectationUtilisateur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4001 };
        const entity2 = { id: 21243 };

        const compareResult1 = service.compareAffectationUtilisateur(entity1, entity2);
        const compareResult2 = service.compareAffectationUtilisateur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4001 };
        const entity2 = { id: 4001 };

        const compareResult1 = service.compareAffectationUtilisateur(entity1, entity2);
        const compareResult2 = service.compareAffectationUtilisateur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
