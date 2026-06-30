import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IBoutique } from '../boutique.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../boutique.test-samples';

import { BoutiqueService, RestBoutique } from './boutique.service';

const requireRestSample: RestBoutique = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
};

describe('Boutique Service', () => {
  let service: BoutiqueService;
  let httpMock: HttpTestingController;
  let expectedResult: IBoutique | IBoutique[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BoutiqueService);
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

    it('should create a Boutique', () => {
      const boutique = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(boutique).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Boutique', () => {
      const boutique = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(boutique).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Boutique', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Boutique', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Boutique', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addBoutiqueToCollectionIfMissing', () => {
      it('should add a Boutique to an empty array', () => {
        const boutique: IBoutique = sampleWithRequiredData;
        expectedResult = service.addBoutiqueToCollectionIfMissing([], boutique);
        expect(expectedResult).toEqual([boutique]);
      });

      it('should not add a Boutique to an array that contains it', () => {
        const boutique: IBoutique = sampleWithRequiredData;
        const boutiqueCollection: IBoutique[] = [
          {
            ...boutique,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBoutiqueToCollectionIfMissing(boutiqueCollection, boutique);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Boutique to an array that doesn't contain it", () => {
        const boutique: IBoutique = sampleWithRequiredData;
        const boutiqueCollection: IBoutique[] = [sampleWithPartialData];
        expectedResult = service.addBoutiqueToCollectionIfMissing(boutiqueCollection, boutique);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(boutique);
      });

      it('should add only unique Boutique to an array', () => {
        const boutiqueArray: IBoutique[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const boutiqueCollection: IBoutique[] = [sampleWithRequiredData];
        expectedResult = service.addBoutiqueToCollectionIfMissing(boutiqueCollection, ...boutiqueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const boutique: IBoutique = sampleWithRequiredData;
        const boutique2: IBoutique = sampleWithPartialData;
        expectedResult = service.addBoutiqueToCollectionIfMissing([], boutique, boutique2);
        expect(expectedResult).toEqual([boutique, boutique2]);
      });

      it('should accept null and undefined values', () => {
        const boutique: IBoutique = sampleWithRequiredData;
        expectedResult = service.addBoutiqueToCollectionIfMissing([], null, boutique, undefined);
        expect(expectedResult).toEqual([boutique]);
      });

      it('should return initial array if no Boutique is added', () => {
        const boutiqueCollection: IBoutique[] = [sampleWithRequiredData];
        expectedResult = service.addBoutiqueToCollectionIfMissing(boutiqueCollection, undefined, null);
        expect(expectedResult).toEqual(boutiqueCollection);
      });
    });

    describe('compareBoutique', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBoutique(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 5005 };
        const entity2 = null;

        const compareResult1 = service.compareBoutique(entity1, entity2);
        const compareResult2 = service.compareBoutique(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 5005 };
        const entity2 = { id: 26278 };

        const compareResult1 = service.compareBoutique(entity1, entity2);
        const compareResult2 = service.compareBoutique(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 5005 };
        const entity2 = { id: 5005 };

        const compareResult1 = service.compareBoutique(entity1, entity2);
        const compareResult2 = service.compareBoutique(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
