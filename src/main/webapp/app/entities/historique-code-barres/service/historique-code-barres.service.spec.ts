import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IHistoriqueCodeBarres } from '../historique-code-barres.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../historique-code-barres.test-samples';

import { HistoriqueCodeBarresService, RestHistoriqueCodeBarres } from './historique-code-barres.service';

const requireRestSample: RestHistoriqueCodeBarres = {
  ...sampleWithRequiredData,
  dateChangement: sampleWithRequiredData.dateChangement?.toJSON(),
};

describe('HistoriqueCodeBarres Service', () => {
  let service: HistoriqueCodeBarresService;
  let httpMock: HttpTestingController;
  let expectedResult: IHistoriqueCodeBarres | IHistoriqueCodeBarres[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HistoriqueCodeBarresService);
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

    it('should create a HistoriqueCodeBarres', () => {
      const historiqueCodeBarres = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(historiqueCodeBarres).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a HistoriqueCodeBarres', () => {
      const historiqueCodeBarres = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(historiqueCodeBarres).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a HistoriqueCodeBarres', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of HistoriqueCodeBarres', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a HistoriqueCodeBarres', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addHistoriqueCodeBarresToCollectionIfMissing', () => {
      it('should add a HistoriqueCodeBarres to an empty array', () => {
        const historiqueCodeBarres: IHistoriqueCodeBarres = sampleWithRequiredData;
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing([], historiqueCodeBarres);
        expect(expectedResult).toEqual([historiqueCodeBarres]);
      });

      it('should not add a HistoriqueCodeBarres to an array that contains it', () => {
        const historiqueCodeBarres: IHistoriqueCodeBarres = sampleWithRequiredData;
        const historiqueCodeBarresCollection: IHistoriqueCodeBarres[] = [
          {
            ...historiqueCodeBarres,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing(historiqueCodeBarresCollection, historiqueCodeBarres);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a HistoriqueCodeBarres to an array that doesn't contain it", () => {
        const historiqueCodeBarres: IHistoriqueCodeBarres = sampleWithRequiredData;
        const historiqueCodeBarresCollection: IHistoriqueCodeBarres[] = [sampleWithPartialData];
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing(historiqueCodeBarresCollection, historiqueCodeBarres);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(historiqueCodeBarres);
      });

      it('should add only unique HistoriqueCodeBarres to an array', () => {
        const historiqueCodeBarresArray: IHistoriqueCodeBarres[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const historiqueCodeBarresCollection: IHistoriqueCodeBarres[] = [sampleWithRequiredData];
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing(historiqueCodeBarresCollection, ...historiqueCodeBarresArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const historiqueCodeBarres: IHistoriqueCodeBarres = sampleWithRequiredData;
        const historiqueCodeBarres2: IHistoriqueCodeBarres = sampleWithPartialData;
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing([], historiqueCodeBarres, historiqueCodeBarres2);
        expect(expectedResult).toEqual([historiqueCodeBarres, historiqueCodeBarres2]);
      });

      it('should accept null and undefined values', () => {
        const historiqueCodeBarres: IHistoriqueCodeBarres = sampleWithRequiredData;
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing([], null, historiqueCodeBarres, undefined);
        expect(expectedResult).toEqual([historiqueCodeBarres]);
      });

      it('should return initial array if no HistoriqueCodeBarres is added', () => {
        const historiqueCodeBarresCollection: IHistoriqueCodeBarres[] = [sampleWithRequiredData];
        expectedResult = service.addHistoriqueCodeBarresToCollectionIfMissing(historiqueCodeBarresCollection, undefined, null);
        expect(expectedResult).toEqual(historiqueCodeBarresCollection);
      });
    });

    describe('compareHistoriqueCodeBarres', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHistoriqueCodeBarres(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29449 };
        const entity2 = null;

        const compareResult1 = service.compareHistoriqueCodeBarres(entity1, entity2);
        const compareResult2 = service.compareHistoriqueCodeBarres(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29449 };
        const entity2 = { id: 26931 };

        const compareResult1 = service.compareHistoriqueCodeBarres(entity1, entity2);
        const compareResult2 = service.compareHistoriqueCodeBarres(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29449 };
        const entity2 = { id: 29449 };

        const compareResult1 = service.compareHistoriqueCodeBarres(entity1, entity2);
        const compareResult2 = service.compareHistoriqueCodeBarres(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
