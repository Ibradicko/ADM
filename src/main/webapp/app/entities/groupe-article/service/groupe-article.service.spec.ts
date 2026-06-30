import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IGroupeArticle } from '../groupe-article.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../groupe-article.test-samples';

import { GroupeArticleService } from './groupe-article.service';

const requireRestSample: IGroupeArticle = {
  ...sampleWithRequiredData,
};

describe('GroupeArticle Service', () => {
  let service: GroupeArticleService;
  let httpMock: HttpTestingController;
  let expectedResult: IGroupeArticle | IGroupeArticle[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(GroupeArticleService);
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

    it('should create a GroupeArticle', () => {
      const groupeArticle = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(groupeArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GroupeArticle', () => {
      const groupeArticle = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(groupeArticle).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GroupeArticle', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GroupeArticle', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GroupeArticle', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addGroupeArticleToCollectionIfMissing', () => {
      it('should add a GroupeArticle to an empty array', () => {
        const groupeArticle: IGroupeArticle = sampleWithRequiredData;
        expectedResult = service.addGroupeArticleToCollectionIfMissing([], groupeArticle);
        expect(expectedResult).toEqual([groupeArticle]);
      });

      it('should not add a GroupeArticle to an array that contains it', () => {
        const groupeArticle: IGroupeArticle = sampleWithRequiredData;
        const groupeArticleCollection: IGroupeArticle[] = [
          {
            ...groupeArticle,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGroupeArticleToCollectionIfMissing(groupeArticleCollection, groupeArticle);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GroupeArticle to an array that doesn't contain it", () => {
        const groupeArticle: IGroupeArticle = sampleWithRequiredData;
        const groupeArticleCollection: IGroupeArticle[] = [sampleWithPartialData];
        expectedResult = service.addGroupeArticleToCollectionIfMissing(groupeArticleCollection, groupeArticle);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(groupeArticle);
      });

      it('should add only unique GroupeArticle to an array', () => {
        const groupeArticleArray: IGroupeArticle[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const groupeArticleCollection: IGroupeArticle[] = [sampleWithRequiredData];
        expectedResult = service.addGroupeArticleToCollectionIfMissing(groupeArticleCollection, ...groupeArticleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const groupeArticle: IGroupeArticle = sampleWithRequiredData;
        const groupeArticle2: IGroupeArticle = sampleWithPartialData;
        expectedResult = service.addGroupeArticleToCollectionIfMissing([], groupeArticle, groupeArticle2);
        expect(expectedResult).toEqual([groupeArticle, groupeArticle2]);
      });

      it('should accept null and undefined values', () => {
        const groupeArticle: IGroupeArticle = sampleWithRequiredData;
        expectedResult = service.addGroupeArticleToCollectionIfMissing([], null, groupeArticle, undefined);
        expect(expectedResult).toEqual([groupeArticle]);
      });

      it('should return initial array if no GroupeArticle is added', () => {
        const groupeArticleCollection: IGroupeArticle[] = [sampleWithRequiredData];
        expectedResult = service.addGroupeArticleToCollectionIfMissing(groupeArticleCollection, undefined, null);
        expect(expectedResult).toEqual(groupeArticleCollection);
      });
    });

    describe('compareGroupeArticle', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGroupeArticle(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2930 };
        const entity2 = null;

        const compareResult1 = service.compareGroupeArticle(entity1, entity2);
        const compareResult2 = service.compareGroupeArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2930 };
        const entity2 = { id: 28799 };

        const compareResult1 = service.compareGroupeArticle(entity1, entity2);
        const compareResult2 = service.compareGroupeArticle(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2930 };
        const entity2 = { id: 2930 };

        const compareResult1 = service.compareGroupeArticle(entity1, entity2);
        const compareResult2 = service.compareGroupeArticle(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
