import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IScanInconnu } from '../scan-inconnu.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../scan-inconnu.test-samples';

import { RestScanInconnu, ScanInconnuService } from './scan-inconnu.service';

const requireRestSample: RestScanInconnu = {
  ...sampleWithRequiredData,
  dateScan: sampleWithRequiredData.dateScan?.toJSON(),
};

describe('ScanInconnu Service', () => {
  let service: ScanInconnuService;
  let httpMock: HttpTestingController;
  let expectedResult: IScanInconnu | IScanInconnu[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ScanInconnuService);
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

    it('should create a ScanInconnu', () => {
      const scanInconnu = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(scanInconnu).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ScanInconnu', () => {
      const scanInconnu = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(scanInconnu).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ScanInconnu', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ScanInconnu', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ScanInconnu', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addScanInconnuToCollectionIfMissing', () => {
      it('should add a ScanInconnu to an empty array', () => {
        const scanInconnu: IScanInconnu = sampleWithRequiredData;
        expectedResult = service.addScanInconnuToCollectionIfMissing([], scanInconnu);
        expect(expectedResult).toEqual([scanInconnu]);
      });

      it('should not add a ScanInconnu to an array that contains it', () => {
        const scanInconnu: IScanInconnu = sampleWithRequiredData;
        const scanInconnuCollection: IScanInconnu[] = [
          {
            ...scanInconnu,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addScanInconnuToCollectionIfMissing(scanInconnuCollection, scanInconnu);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ScanInconnu to an array that doesn't contain it", () => {
        const scanInconnu: IScanInconnu = sampleWithRequiredData;
        const scanInconnuCollection: IScanInconnu[] = [sampleWithPartialData];
        expectedResult = service.addScanInconnuToCollectionIfMissing(scanInconnuCollection, scanInconnu);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scanInconnu);
      });

      it('should add only unique ScanInconnu to an array', () => {
        const scanInconnuArray: IScanInconnu[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const scanInconnuCollection: IScanInconnu[] = [sampleWithRequiredData];
        expectedResult = service.addScanInconnuToCollectionIfMissing(scanInconnuCollection, ...scanInconnuArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scanInconnu: IScanInconnu = sampleWithRequiredData;
        const scanInconnu2: IScanInconnu = sampleWithPartialData;
        expectedResult = service.addScanInconnuToCollectionIfMissing([], scanInconnu, scanInconnu2);
        expect(expectedResult).toEqual([scanInconnu, scanInconnu2]);
      });

      it('should accept null and undefined values', () => {
        const scanInconnu: IScanInconnu = sampleWithRequiredData;
        expectedResult = service.addScanInconnuToCollectionIfMissing([], null, scanInconnu, undefined);
        expect(expectedResult).toEqual([scanInconnu]);
      });

      it('should return initial array if no ScanInconnu is added', () => {
        const scanInconnuCollection: IScanInconnu[] = [sampleWithRequiredData];
        expectedResult = service.addScanInconnuToCollectionIfMissing(scanInconnuCollection, undefined, null);
        expect(expectedResult).toEqual(scanInconnuCollection);
      });
    });

    describe('compareScanInconnu', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareScanInconnu(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4379 };
        const entity2 = null;

        const compareResult1 = service.compareScanInconnu(entity1, entity2);
        const compareResult2 = service.compareScanInconnu(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4379 };
        const entity2 = { id: 18351 };

        const compareResult1 = service.compareScanInconnu(entity1, entity2);
        const compareResult2 = service.compareScanInconnu(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4379 };
        const entity2 = { id: 4379 };

        const compareResult1 = service.compareScanInconnu(entity1, entity2);
        const compareResult2 = service.compareScanInconnu(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
