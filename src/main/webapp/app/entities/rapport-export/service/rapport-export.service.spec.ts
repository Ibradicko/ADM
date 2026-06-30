import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IRapportExport } from '../rapport-export.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../rapport-export.test-samples';

import { RapportExportService, RestRapportExport } from './rapport-export.service';

const requireRestSample: RestRapportExport = {
  ...sampleWithRequiredData,
  periodeDebut: sampleWithRequiredData.periodeDebut?.format(DATE_FORMAT),
  periodeFin: sampleWithRequiredData.periodeFin?.format(DATE_FORMAT),
  dateGeneration: sampleWithRequiredData.dateGeneration?.toJSON(),
};

describe('RapportExport Service', () => {
  let service: RapportExportService;
  let httpMock: HttpTestingController;
  let expectedResult: IRapportExport | IRapportExport[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RapportExportService);
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

    it('should create a RapportExport', () => {
      const rapportExport = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rapportExport).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RapportExport', () => {
      const rapportExport = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rapportExport).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RapportExport', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RapportExport', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RapportExport', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addRapportExportToCollectionIfMissing', () => {
      it('should add a RapportExport to an empty array', () => {
        const rapportExport: IRapportExport = sampleWithRequiredData;
        expectedResult = service.addRapportExportToCollectionIfMissing([], rapportExport);
        expect(expectedResult).toEqual([rapportExport]);
      });

      it('should not add a RapportExport to an array that contains it', () => {
        const rapportExport: IRapportExport = sampleWithRequiredData;
        const rapportExportCollection: IRapportExport[] = [
          {
            ...rapportExport,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRapportExportToCollectionIfMissing(rapportExportCollection, rapportExport);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RapportExport to an array that doesn't contain it", () => {
        const rapportExport: IRapportExport = sampleWithRequiredData;
        const rapportExportCollection: IRapportExport[] = [sampleWithPartialData];
        expectedResult = service.addRapportExportToCollectionIfMissing(rapportExportCollection, rapportExport);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rapportExport);
      });

      it('should add only unique RapportExport to an array', () => {
        const rapportExportArray: IRapportExport[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rapportExportCollection: IRapportExport[] = [sampleWithRequiredData];
        expectedResult = service.addRapportExportToCollectionIfMissing(rapportExportCollection, ...rapportExportArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rapportExport: IRapportExport = sampleWithRequiredData;
        const rapportExport2: IRapportExport = sampleWithPartialData;
        expectedResult = service.addRapportExportToCollectionIfMissing([], rapportExport, rapportExport2);
        expect(expectedResult).toEqual([rapportExport, rapportExport2]);
      });

      it('should accept null and undefined values', () => {
        const rapportExport: IRapportExport = sampleWithRequiredData;
        expectedResult = service.addRapportExportToCollectionIfMissing([], null, rapportExport, undefined);
        expect(expectedResult).toEqual([rapportExport]);
      });

      it('should return initial array if no RapportExport is added', () => {
        const rapportExportCollection: IRapportExport[] = [sampleWithRequiredData];
        expectedResult = service.addRapportExportToCollectionIfMissing(rapportExportCollection, undefined, null);
        expect(expectedResult).toEqual(rapportExportCollection);
      });
    });

    describe('compareRapportExport', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRapportExport(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 650 };
        const entity2 = null;

        const compareResult1 = service.compareRapportExport(entity1, entity2);
        const compareResult2 = service.compareRapportExport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 650 };
        const entity2 = { id: 15905 };

        const compareResult1 = service.compareRapportExport(entity1, entity2);
        const compareResult2 = service.compareRapportExport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 650 };
        const entity2 = { id: 650 };

        const compareResult1 = service.compareRapportExport(entity1, entity2);
        const compareResult2 = service.compareRapportExport(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
