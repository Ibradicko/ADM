import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILocataire } from '../locataire.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../locataire.test-samples';

import { LocataireService, RestLocataire } from './locataire.service';

const requireRestSample: RestLocataire = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
};

describe('Locataire Service', () => {
  let service: LocataireService;
  let httpMock: HttpTestingController;
  let expectedResult: ILocataire | ILocataire[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LocataireService);
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

    it('should create a Locataire', () => {
      const locataire = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(locataire).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Locataire', () => {
      const locataire = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(locataire).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Locataire', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Locataire', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Locataire', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLocataireToCollectionIfMissing', () => {
      it('should add a Locataire to an empty array', () => {
        const locataire: ILocataire = sampleWithRequiredData;
        expectedResult = service.addLocataireToCollectionIfMissing([], locataire);
        expect(expectedResult).toEqual([locataire]);
      });

      it('should not add a Locataire to an array that contains it', () => {
        const locataire: ILocataire = sampleWithRequiredData;
        const locataireCollection: ILocataire[] = [
          {
            ...locataire,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLocataireToCollectionIfMissing(locataireCollection, locataire);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Locataire to an array that doesn't contain it", () => {
        const locataire: ILocataire = sampleWithRequiredData;
        const locataireCollection: ILocataire[] = [sampleWithPartialData];
        expectedResult = service.addLocataireToCollectionIfMissing(locataireCollection, locataire);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locataire);
      });

      it('should add only unique Locataire to an array', () => {
        const locataireArray: ILocataire[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const locataireCollection: ILocataire[] = [sampleWithRequiredData];
        expectedResult = service.addLocataireToCollectionIfMissing(locataireCollection, ...locataireArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const locataire: ILocataire = sampleWithRequiredData;
        const locataire2: ILocataire = sampleWithPartialData;
        expectedResult = service.addLocataireToCollectionIfMissing([], locataire, locataire2);
        expect(expectedResult).toEqual([locataire, locataire2]);
      });

      it('should accept null and undefined values', () => {
        const locataire: ILocataire = sampleWithRequiredData;
        expectedResult = service.addLocataireToCollectionIfMissing([], null, locataire, undefined);
        expect(expectedResult).toEqual([locataire]);
      });

      it('should return initial array if no Locataire is added', () => {
        const locataireCollection: ILocataire[] = [sampleWithRequiredData];
        expectedResult = service.addLocataireToCollectionIfMissing(locataireCollection, undefined, null);
        expect(expectedResult).toEqual(locataireCollection);
      });
    });

    describe('compareLocataire', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLocataire(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3768 };
        const entity2 = null;

        const compareResult1 = service.compareLocataire(entity1, entity2);
        const compareResult2 = service.compareLocataire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3768 };
        const entity2 = { id: 24112 };

        const compareResult1 = service.compareLocataire(entity1, entity2);
        const compareResult2 = service.compareLocataire(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 3768 };
        const entity2 = { id: 3768 };

        const compareResult1 = service.compareLocataire(entity1, entity2);
        const compareResult2 = service.compareLocataire(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
