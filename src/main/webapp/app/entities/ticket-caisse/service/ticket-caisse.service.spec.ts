import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITicketCaisse } from '../ticket-caisse.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ticket-caisse.test-samples';

import { RestTicketCaisse, TicketCaisseService } from './ticket-caisse.service';

const requireRestSample: RestTicketCaisse = {
  ...sampleWithRequiredData,
  dateEmission: sampleWithRequiredData.dateEmission?.toJSON(),
};

describe('TicketCaisse Service', () => {
  let service: TicketCaisseService;
  let httpMock: HttpTestingController;
  let expectedResult: ITicketCaisse | ITicketCaisse[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TicketCaisseService);
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

    it('should create a TicketCaisse', () => {
      const ticketCaisse = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ticketCaisse).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TicketCaisse', () => {
      const ticketCaisse = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ticketCaisse).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TicketCaisse', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TicketCaisse', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TicketCaisse', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTicketCaisseToCollectionIfMissing', () => {
      it('should add a TicketCaisse to an empty array', () => {
        const ticketCaisse: ITicketCaisse = sampleWithRequiredData;
        expectedResult = service.addTicketCaisseToCollectionIfMissing([], ticketCaisse);
        expect(expectedResult).toEqual([ticketCaisse]);
      });

      it('should not add a TicketCaisse to an array that contains it', () => {
        const ticketCaisse: ITicketCaisse = sampleWithRequiredData;
        const ticketCaisseCollection: ITicketCaisse[] = [
          {
            ...ticketCaisse,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTicketCaisseToCollectionIfMissing(ticketCaisseCollection, ticketCaisse);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TicketCaisse to an array that doesn't contain it", () => {
        const ticketCaisse: ITicketCaisse = sampleWithRequiredData;
        const ticketCaisseCollection: ITicketCaisse[] = [sampleWithPartialData];
        expectedResult = service.addTicketCaisseToCollectionIfMissing(ticketCaisseCollection, ticketCaisse);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketCaisse);
      });

      it('should add only unique TicketCaisse to an array', () => {
        const ticketCaisseArray: ITicketCaisse[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ticketCaisseCollection: ITicketCaisse[] = [sampleWithRequiredData];
        expectedResult = service.addTicketCaisseToCollectionIfMissing(ticketCaisseCollection, ...ticketCaisseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ticketCaisse: ITicketCaisse = sampleWithRequiredData;
        const ticketCaisse2: ITicketCaisse = sampleWithPartialData;
        expectedResult = service.addTicketCaisseToCollectionIfMissing([], ticketCaisse, ticketCaisse2);
        expect(expectedResult).toEqual([ticketCaisse, ticketCaisse2]);
      });

      it('should accept null and undefined values', () => {
        const ticketCaisse: ITicketCaisse = sampleWithRequiredData;
        expectedResult = service.addTicketCaisseToCollectionIfMissing([], null, ticketCaisse, undefined);
        expect(expectedResult).toEqual([ticketCaisse]);
      });

      it('should return initial array if no TicketCaisse is added', () => {
        const ticketCaisseCollection: ITicketCaisse[] = [sampleWithRequiredData];
        expectedResult = service.addTicketCaisseToCollectionIfMissing(ticketCaisseCollection, undefined, null);
        expect(expectedResult).toEqual(ticketCaisseCollection);
      });
    });

    describe('compareTicketCaisse', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTicketCaisse(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27212 };
        const entity2 = null;

        const compareResult1 = service.compareTicketCaisse(entity1, entity2);
        const compareResult2 = service.compareTicketCaisse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27212 };
        const entity2 = { id: 29133 };

        const compareResult1 = service.compareTicketCaisse(entity1, entity2);
        const compareResult2 = service.compareTicketCaisse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27212 };
        const entity2 = { id: 27212 };

        const compareResult1 = service.compareTicketCaisse(entity1, entity2);
        const compareResult2 = service.compareTicketCaisse(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
