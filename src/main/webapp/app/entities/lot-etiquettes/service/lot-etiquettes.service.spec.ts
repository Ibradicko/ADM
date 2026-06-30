import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILotEtiquettes } from '../lot-etiquettes.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../lot-etiquettes.test-samples';

import { LotEtiquettesService, RestLotEtiquettes } from './lot-etiquettes.service';

const requireRestSample: RestLotEtiquettes = {
  ...sampleWithRequiredData,
  dateGeneration: sampleWithRequiredData.dateGeneration?.toJSON(),
};

describe('LotEtiquettes Service', () => {
  let service: LotEtiquettesService;
  let httpMock: HttpTestingController;
  let expectedResult: ILotEtiquettes | ILotEtiquettes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LotEtiquettesService);
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

    it('should create a LotEtiquettes', () => {
      const lotEtiquettes = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(lotEtiquettes).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LotEtiquettes', () => {
      const lotEtiquettes = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(lotEtiquettes).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LotEtiquettes', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LotEtiquettes', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LotEtiquettes', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLotEtiquettesToCollectionIfMissing', () => {
      it('should add a LotEtiquettes to an empty array', () => {
        const lotEtiquettes: ILotEtiquettes = sampleWithRequiredData;
        expectedResult = service.addLotEtiquettesToCollectionIfMissing([], lotEtiquettes);
        expect(expectedResult).toEqual([lotEtiquettes]);
      });

      it('should not add a LotEtiquettes to an array that contains it', () => {
        const lotEtiquettes: ILotEtiquettes = sampleWithRequiredData;
        const lotEtiquettesCollection: ILotEtiquettes[] = [
          {
            ...lotEtiquettes,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLotEtiquettesToCollectionIfMissing(lotEtiquettesCollection, lotEtiquettes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LotEtiquettes to an array that doesn't contain it", () => {
        const lotEtiquettes: ILotEtiquettes = sampleWithRequiredData;
        const lotEtiquettesCollection: ILotEtiquettes[] = [sampleWithPartialData];
        expectedResult = service.addLotEtiquettesToCollectionIfMissing(lotEtiquettesCollection, lotEtiquettes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lotEtiquettes);
      });

      it('should add only unique LotEtiquettes to an array', () => {
        const lotEtiquettesArray: ILotEtiquettes[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const lotEtiquettesCollection: ILotEtiquettes[] = [sampleWithRequiredData];
        expectedResult = service.addLotEtiquettesToCollectionIfMissing(lotEtiquettesCollection, ...lotEtiquettesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const lotEtiquettes: ILotEtiquettes = sampleWithRequiredData;
        const lotEtiquettes2: ILotEtiquettes = sampleWithPartialData;
        expectedResult = service.addLotEtiquettesToCollectionIfMissing([], lotEtiquettes, lotEtiquettes2);
        expect(expectedResult).toEqual([lotEtiquettes, lotEtiquettes2]);
      });

      it('should accept null and undefined values', () => {
        const lotEtiquettes: ILotEtiquettes = sampleWithRequiredData;
        expectedResult = service.addLotEtiquettesToCollectionIfMissing([], null, lotEtiquettes, undefined);
        expect(expectedResult).toEqual([lotEtiquettes]);
      });

      it('should return initial array if no LotEtiquettes is added', () => {
        const lotEtiquettesCollection: ILotEtiquettes[] = [sampleWithRequiredData];
        expectedResult = service.addLotEtiquettesToCollectionIfMissing(lotEtiquettesCollection, undefined, null);
        expect(expectedResult).toEqual(lotEtiquettesCollection);
      });
    });

    describe('compareLotEtiquettes', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLotEtiquettes(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 1087 };
        const entity2 = null;

        const compareResult1 = service.compareLotEtiquettes(entity1, entity2);
        const compareResult2 = service.compareLotEtiquettes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 1087 };
        const entity2 = { id: 17694 };

        const compareResult1 = service.compareLotEtiquettes(entity1, entity2);
        const compareResult2 = service.compareLotEtiquettes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 1087 };
        const entity2 = { id: 1087 };

        const compareResult1 = service.compareLotEtiquettes(entity1, entity2);
        const compareResult2 = service.compareLotEtiquettes(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
