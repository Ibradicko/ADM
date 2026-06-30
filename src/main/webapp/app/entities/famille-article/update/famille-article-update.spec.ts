import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { IFamilleArticle } from '../famille-article.model';
import { FamilleArticleService } from '../service/famille-article.service';

import { FamilleArticleFormService } from './famille-article-form.service';
import { FamilleArticleUpdate } from './famille-article-update';

describe('FamilleArticle Management Update Component', () => {
  let comp: FamilleArticleUpdate;
  let fixture: ComponentFixture<FamilleArticleUpdate>;
  let activatedRoute: ActivatedRoute;
  let familleArticleFormService: FamilleArticleFormService;
  let familleArticleService: FamilleArticleService;
  let groupeArticleService: GroupeArticleService;

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

    fixture = TestBed.createComponent(FamilleArticleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    familleArticleFormService = TestBed.inject(FamilleArticleFormService);
    familleArticleService = TestBed.inject(FamilleArticleService);
    groupeArticleService = TestBed.inject(GroupeArticleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call GroupeArticle query and add missing value', () => {
      const familleArticle: IFamilleArticle = { id: 14702 };
      const groupeArticle: IGroupeArticle = { id: 2930 };
      familleArticle.groupeArticle = groupeArticle;

      const groupeArticleCollection: IGroupeArticle[] = [{ id: 2930 }];
      vitest.spyOn(groupeArticleService, 'query').mockReturnValue(of(new HttpResponse({ body: groupeArticleCollection })));
      const additionalGroupeArticles = [groupeArticle];
      const expectedCollection: IGroupeArticle[] = [...additionalGroupeArticles, ...groupeArticleCollection];
      vitest.spyOn(groupeArticleService, 'addGroupeArticleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ familleArticle });
      comp.ngOnInit();

      expect(groupeArticleService.query).toHaveBeenCalled();
      expect(groupeArticleService.addGroupeArticleToCollectionIfMissing).toHaveBeenCalledWith(
        groupeArticleCollection,
        ...additionalGroupeArticles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.groupeArticlesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const familleArticle: IFamilleArticle = { id: 14702 };
      const groupeArticle: IGroupeArticle = { id: 2930 };
      familleArticle.groupeArticle = groupeArticle;

      activatedRoute.data = of({ familleArticle });
      comp.ngOnInit();

      expect(comp.groupeArticlesSharedCollection()).toContainEqual(groupeArticle);
      expect(comp.familleArticle).toEqual(familleArticle);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFamilleArticle>();
      const familleArticle = { id: 29368 };
      vitest.spyOn(familleArticleFormService, 'getFamilleArticle').mockReturnValue(familleArticle);
      vitest.spyOn(familleArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ familleArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(familleArticle);
      saveSubject.complete();

      // THEN
      expect(familleArticleFormService.getFamilleArticle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(familleArticleService.update).toHaveBeenCalledWith(expect.objectContaining(familleArticle));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFamilleArticle>();
      const familleArticle = { id: 29368 };
      vitest.spyOn(familleArticleFormService, 'getFamilleArticle').mockReturnValue({ id: null });
      vitest.spyOn(familleArticleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ familleArticle: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(familleArticle);
      saveSubject.complete();

      // THEN
      expect(familleArticleFormService.getFamilleArticle).toHaveBeenCalled();
      expect(familleArticleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFamilleArticle>();
      const familleArticle = { id: 29368 };
      vitest.spyOn(familleArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ familleArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(familleArticleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGroupeArticle', () => {
      it('should forward to groupeArticleService', () => {
        const entity = { id: 2930 };
        const entity2 = { id: 28799 };
        vitest.spyOn(groupeArticleService, 'compareGroupeArticle');
        comp.compareGroupeArticle(entity, entity2);
        expect(groupeArticleService.compareGroupeArticle).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
