import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISousFamilleArticle } from '../sous-famille-article.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sous-famille-article.test-samples';

import { SousFamilleArticleService } from './sous-famille-article.service';

const requireRestSample: ISousFamilleArticle = {
  ...sampleWithRequiredData,
};

describe('SousFamilleArticle Service', () => {
  let service: SousFamilleArticleService;
  let httpMock: HttpTestingController;
  let expectedResult: ISousFamilleArticle | ISousFamilleArticle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SousFamilleArticleService);
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

    it('should create a SousFamilleArticle', () => {
      const sousFamilleArticle = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sousFamilleArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SousFamilleArticle', () => {
      const sousFamilleArticle = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sousFamilleArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SousFamilleArticle', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SousFamilleArticle', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SousFamilleArticle', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addSousFamilleArticleToCollectionIfMissing', () => {
      it('should add a SousFamilleArticle to an empty array', () => {
        const sousFamilleArticle: ISousFamilleArticle = sampleWithRequiredData;
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing([], sousFamilleArticle);
        expect(expectedResult).toEqual([sousFamilleArticle]);
      });

      it('should not add a SousFamilleArticle to an array that contains it', () => {
        const sousFamilleArticle: ISousFamilleArticle = sampleWithRequiredData;
        const sousFamilleArticleCollection: ISousFamilleArticle[] = [
          {
            ...sousFamilleArticle,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing(sousFamilleArticleCollection, sousFamilleArticle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SousFamilleArticle to an array that doesn't contain it", () => {
        const sousFamilleArticle: ISousFamilleArticle = sampleWithRequiredData;
        const sousFamilleArticleCollection: ISousFamilleArticle[] = [sampleWithPartialData];
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing(sousFamilleArticleCollection, sousFamilleArticle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sousFamilleArticle);
      });

      it('should add only unique SousFamilleArticle to an array', () => {
        const sousFamilleArticleArray: ISousFamilleArticle[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sousFamilleArticleCollection: ISousFamilleArticle[] = [sampleWithRequiredData];
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing(sousFamilleArticleCollection, ...sousFamilleArticleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sousFamilleArticle: ISousFamilleArticle = sampleWithRequiredData;
        const sousFamilleArticle2: ISousFamilleArticle = sampleWithPartialData;
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing([], sousFamilleArticle, sousFamilleArticle2);
        expect(expectedResult).toEqual([sousFamilleArticle, sousFamilleArticle2]);
      });

      it('should accept null and undefined values', () => {
        const sousFamilleArticle: ISousFamilleArticle = sampleWithRequiredData;
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing([], null, sousFamilleArticle, undefined);
        expect(expectedResult).toEqual([sousFamilleArticle]);
      });

      it('should return initial array if no SousFamilleArticle is added', () => {
        const sousFamilleArticleCollection: ISousFamilleArticle[] = [sampleWithRequiredData];
        expectedResult = service.addSousFamilleArticleToCollectionIfMissing(sousFamilleArticleCollection, undefined, null);
        expect(expectedResult).toEqual(sousFamilleArticleCollection);
      });
    });

    describe('compareSousFamilleArticle', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSousFamilleArticle(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 30207 };
        const entity2 = null;

        const compareResult1 = service.compareSousFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareSousFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 30207 };
        const entity2 = { id: 5132 };

        const compareResult1 = service.compareSousFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareSousFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 30207 };
        const entity2 = { id: 30207 };

        const compareResult1 = service.compareSousFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareSousFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
