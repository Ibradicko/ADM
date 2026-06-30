import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IFamilleArticle } from 'app/entities/famille-article/famille-article.model';
import { FamilleArticleService } from 'app/entities/famille-article/service/famille-article.service';
import { SousFamilleArticleService } from '../service/sous-famille-article.service';
import { ISousFamilleArticle } from '../sous-famille-article.model';

import { SousFamilleArticleFormService } from './sous-famille-article-form.service';
import { SousFamilleArticleUpdate } from './sous-famille-article-update';

describe('SousFamilleArticle Management Update Component', () => {
  let comp: SousFamilleArticleUpdate;
  let fixture: ComponentFixture<SousFamilleArticleUpdate>;
  let activatedRoute: ActivatedRoute;
  let sousFamilleArticleFormService: SousFamilleArticleFormService;
  let sousFamilleArticleService: SousFamilleArticleService;
  let familleArticleService: FamilleArticleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(SousFamilleArticleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sousFamilleArticleFormService = TestBed.inject(SousFamilleArticleFormService);
    sousFamilleArticleService = TestBed.inject(SousFamilleArticleService);
    familleArticleService = TestBed.inject(FamilleArticleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call FamilleArticle query and add missing value', () => {
      const sousFamilleArticle: ISousFamilleArticle = { id: 5132 };
      const familleArticle: IFamilleArticle = { id: 29368 };
      sousFamilleArticle.familleArticle = familleArticle;

      const familleArticleCollection: IFamilleArticle[] = [{ id: 29368 }];
      vitest.spyOn(familleArticleService, 'query').mockReturnValue(of(new HttpResponse({ body: familleArticleCollection })));
      const additionalFamilleArticles = [familleArticle];
      const expectedCollection: IFamilleArticle[] = [...additionalFamilleArticles, ...familleArticleCollection];
      vitest.spyOn(familleArticleService, 'addFamilleArticleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sousFamilleArticle });
      comp.ngOnInit();

      expect(familleArticleService.query).toHaveBeenCalled();
      expect(familleArticleService.addFamilleArticleToCollectionIfMissing).toHaveBeenCalledWith(
        familleArticleCollection,
        ...additionalFamilleArticles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.familleArticlesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const sousFamilleArticle: ISousFamilleArticle = { id: 5132 };
      const familleArticle: IFamilleArticle = { id: 29368 };
      sousFamilleArticle.familleArticle = familleArticle;

      activatedRoute.data = of({ sousFamilleArticle });
      comp.ngOnInit();

      expect(comp.familleArticlesSharedCollection()).toContainEqual(familleArticle);
      expect(comp.sousFamilleArticle).toEqual(sousFamilleArticle);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISousFamilleArticle>();
      const sousFamilleArticle = { id: 30207 };
      vitest.spyOn(sousFamilleArticleFormService, 'getSousFamilleArticle').mockReturnValue(sousFamilleArticle);
      vitest.spyOn(sousFamilleArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sousFamilleArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sousFamilleArticle);
      saveSubject.complete();

      // THEN
      expect(sousFamilleArticleFormService.getSousFamilleArticle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sousFamilleArticleService.update).toHaveBeenCalledWith(expect.objectContaining(sousFamilleArticle));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISousFamilleArticle>();
      const sousFamilleArticle = { id: 30207 };
      vitest.spyOn(sousFamilleArticleFormService, 'getSousFamilleArticle').mockReturnValue({ id: null });
      vitest.spyOn(sousFamilleArticleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sousFamilleArticle: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(sousFamilleArticle);
      saveSubject.complete();

      // THEN
      expect(sousFamilleArticleFormService.getSousFamilleArticle).toHaveBeenCalled();
      expect(sousFamilleArticleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISousFamilleArticle>();
      const sousFamilleArticle = { id: 30207 };
      vitest.spyOn(sousFamilleArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sousFamilleArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sousFamilleArticleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFamilleArticle', () => {
      it('should forward to familleArticleService', () => {
        const entity = { id: 29368 };
        const entity2 = { id: 14702 };
        vitest.spyOn(familleArticleService, 'compareFamilleArticle');
        comp.compareFamilleArticle(entity, entity2);
        expect(familleArticleService.compareFamilleArticle).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
