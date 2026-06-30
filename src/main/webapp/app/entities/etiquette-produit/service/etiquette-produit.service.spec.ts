import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEtiquetteProduit } from '../etiquette-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../etiquette-produit.test-samples';

import { EtiquetteProduitService, RestEtiquetteProduit } from './etiquette-produit.service';

const requireRestSample: RestEtiquetteProduit = {
  ...sampleWithRequiredData,
  dateImpression: sampleWithRequiredData.dateImpression?.toJSON(),
};

describe('EtiquetteProduit Service', () => {
  let service: EtiquetteProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: IEtiquetteProduit | IEtiquetteProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EtiquetteProduitService);
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

    it('should create a EtiquetteProduit', () => {
      const etiquetteProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(etiquetteProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EtiquetteProduit', () => {
      const etiquetteProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(etiquetteProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EtiquetteProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EtiquetteProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EtiquetteProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addEtiquetteProduitToCollectionIfMissing', () => {
      it('should add a EtiquetteProduit to an empty array', () => {
        const etiquetteProduit: IEtiquetteProduit = sampleWithRequiredData;
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing([], etiquetteProduit);
        expect(expectedResult).toEqual([etiquetteProduit]);
      });

      it('should not add a EtiquetteProduit to an array that contains it', () => {
        const etiquetteProduit: IEtiquetteProduit = sampleWithRequiredData;
        const etiquetteProduitCollection: IEtiquetteProduit[] = [
          {
            ...etiquetteProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing(etiquetteProduitCollection, etiquetteProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EtiquetteProduit to an array that doesn't contain it", () => {
        const etiquetteProduit: IEtiquetteProduit = sampleWithRequiredData;
        const etiquetteProduitCollection: IEtiquetteProduit[] = [sampleWithPartialData];
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing(etiquetteProduitCollection, etiquetteProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(etiquetteProduit);
      });

      it('should add only unique EtiquetteProduit to an array', () => {
        const etiquetteProduitArray: IEtiquetteProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const etiquetteProduitCollection: IEtiquetteProduit[] = [sampleWithRequiredData];
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing(etiquetteProduitCollection, ...etiquetteProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const etiquetteProduit: IEtiquetteProduit = sampleWithRequiredData;
        const etiquetteProduit2: IEtiquetteProduit = sampleWithPartialData;
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing([], etiquetteProduit, etiquetteProduit2);
        expect(expectedResult).toEqual([etiquetteProduit, etiquetteProduit2]);
      });

      it('should accept null and undefined values', () => {
        const etiquetteProduit: IEtiquetteProduit = sampleWithRequiredData;
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing([], null, etiquetteProduit, undefined);
        expect(expectedResult).toEqual([etiquetteProduit]);
      });

      it('should return initial array if no EtiquetteProduit is added', () => {
        const etiquetteProduitCollection: IEtiquetteProduit[] = [sampleWithRequiredData];
        expectedResult = service.addEtiquetteProduitToCollectionIfMissing(etiquetteProduitCollection, undefined, null);
        expect(expectedResult).toEqual(etiquetteProduitCollection);
      });
    });

    describe('compareEtiquetteProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEtiquetteProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25712 };
        const entity2 = null;

        const compareResult1 = service.compareEtiquetteProduit(entity1, entity2);
        const compareResult2 = service.compareEtiquetteProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25712 };
        const entity2 = { id: 9016 };

        const compareResult1 = service.compareEtiquetteProduit(entity1, entity2);
        const compareResult2 = service.compareEtiquetteProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25712 };
        const entity2 = { id: 25712 };

        const compareResult1 = service.compareEtiquetteProduit(entity1, entity2);
        const compareResult2 = service.compareEtiquetteProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
