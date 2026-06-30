import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IFamilleArticle } from '../famille-article.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../famille-article.test-samples';

import { FamilleArticleService } from './famille-article.service';

const requireRestSample: IFamilleArticle = {
  ...sampleWithRequiredData,
};

describe('FamilleArticle Service', () => {
  let service: FamilleArticleService;
  let httpMock: HttpTestingController;
  let expectedResult: IFamilleArticle | IFamilleArticle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FamilleArticleService);
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

    it('should create a FamilleArticle', () => {
      const familleArticle = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(familleArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FamilleArticle', () => {
      const familleArticle = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(familleArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FamilleArticle', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FamilleArticle', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FamilleArticle', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addFamilleArticleToCollectionIfMissing', () => {
      it('should add a FamilleArticle to an empty array', () => {
        const familleArticle: IFamilleArticle = sampleWithRequiredData;
        expectedResult = service.addFamilleArticleToCollectionIfMissing([], familleArticle);
        expect(expectedResult).toEqual([familleArticle]);
      });

      it('should not add a FamilleArticle to an array that contains it', () => {
        const familleArticle: IFamilleArticle = sampleWithRequiredData;
        const familleArticleCollection: IFamilleArticle[] = [
          {
            ...familleArticle,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFamilleArticleToCollectionIfMissing(familleArticleCollection, familleArticle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FamilleArticle to an array that doesn't contain it", () => {
        const familleArticle: IFamilleArticle = sampleWithRequiredData;
        const familleArticleCollection: IFamilleArticle[] = [sampleWithPartialData];
        expectedResult = service.addFamilleArticleToCollectionIfMissing(familleArticleCollection, familleArticle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(familleArticle);
      });

      it('should add only unique FamilleArticle to an array', () => {
        const familleArticleArray: IFamilleArticle[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const familleArticleCollection: IFamilleArticle[] = [sampleWithRequiredData];
        expectedResult = service.addFamilleArticleToCollectionIfMissing(familleArticleCollection, ...familleArticleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const familleArticle: IFamilleArticle = sampleWithRequiredData;
        const familleArticle2: IFamilleArticle = sampleWithPartialData;
        expectedResult = service.addFamilleArticleToCollectionIfMissing([], familleArticle, familleArticle2);
        expect(expectedResult).toEqual([familleArticle, familleArticle2]);
      });

      it('should accept null and undefined values', () => {
        const familleArticle: IFamilleArticle = sampleWithRequiredData;
        expectedResult = service.addFamilleArticleToCollectionIfMissing([], null, familleArticle, undefined);
        expect(expectedResult).toEqual([familleArticle]);
      });

      it('should return initial array if no FamilleArticle is added', () => {
        const familleArticleCollection: IFamilleArticle[] = [sampleWithRequiredData];
        expectedResult = service.addFamilleArticleToCollectionIfMissing(familleArticleCollection, undefined, null);
        expect(expectedResult).toEqual(familleArticleCollection);
      });
    });

    describe('compareFamilleArticle', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFamilleArticle(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29368 };
        const entity2 = null;

        const compareResult1 = service.compareFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29368 };
        const entity2 = { id: 14702 };

        const compareResult1 = service.compareFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29368 };
        const entity2 = { id: 29368 };

        const compareResult1 = service.compareFamilleArticle(entity1, entity2);
        const compareResult2 = service.compareFamilleArticle(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
