import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneVente } from '../ligne-vente.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ligne-vente.test-samples';

import { LigneVenteService } from './ligne-vente.service';

const requireRestSample: ILigneVente = {
  ...sampleWithRequiredData,
};

describe('LigneVente Service', () => {
  let service: LigneVenteService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneVente | ILigneVente[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneVenteService);
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

    it('should create a LigneVente', () => {
      const ligneVente = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneVente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneVente', () => {
      const ligneVente = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneVente).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneVente', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneVente', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneVente', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneVenteToCollectionIfMissing', () => {
      it('should add a LigneVente to an empty array', () => {
        const ligneVente: ILigneVente = sampleWithRequiredData;
        expectedResult = service.addLigneVenteToCollectionIfMissing([], ligneVente);
        expect(expectedResult).toEqual([ligneVente]);
      });

      it('should not add a LigneVente to an array that contains it', () => {
        const ligneVente: ILigneVente = sampleWithRequiredData;
        const ligneVenteCollection: ILigneVente[] = [
          {
            ...ligneVente,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneVenteToCollectionIfMissing(ligneVenteCollection, ligneVente);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneVente to an array that doesn't contain it", () => {
        const ligneVente: ILigneVente = sampleWithRequiredData;
        const ligneVenteCollection: ILigneVente[] = [sampleWithPartialData];
        expectedResult = service.addLigneVenteToCollectionIfMissing(ligneVenteCollection, ligneVente);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneVente);
      });

      it('should add only unique LigneVente to an array', () => {
        const ligneVenteArray: ILigneVente[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneVenteCollection: ILigneVente[] = [sampleWithRequiredData];
        expectedResult = service.addLigneVenteToCollectionIfMissing(ligneVenteCollection, ...ligneVenteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneVente: ILigneVente = sampleWithRequiredData;
        const ligneVente2: ILigneVente = sampleWithPartialData;
        expectedResult = service.addLigneVenteToCollectionIfMissing([], ligneVente, ligneVente2);
        expect(expectedResult).toEqual([ligneVente, ligneVente2]);
      });

      it('should accept null and undefined values', () => {
        const ligneVente: ILigneVente = sampleWithRequiredData;
        expectedResult = service.addLigneVenteToCollectionIfMissing([], null, ligneVente, undefined);
        expect(expectedResult).toEqual([ligneVente]);
      });

      it('should return initial array if no LigneVente is added', () => {
        const ligneVenteCollection: ILigneVente[] = [sampleWithRequiredData];
        expectedResult = service.addLigneVenteToCollectionIfMissing(ligneVenteCollection, undefined, null);
        expect(expectedResult).toEqual(ligneVenteCollection);
      });
    });

    describe('compareLigneVente', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneVente(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 103 };
        const entity2 = null;

        const compareResult1 = service.compareLigneVente(entity1, entity2);
        const compareResult2 = service.compareLigneVente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 103 };
        const entity2 = { id: 17072 };

        const compareResult1 = service.compareLigneVente(entity1, entity2);
        const compareResult2 = service.compareLigneVente(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 103 };
        const entity2 = { id: 103 };

        const compareResult1 = service.compareLigneVente(entity1, entity2);
        const compareResult2 = service.compareLigneVente(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
