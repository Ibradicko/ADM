import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IGroupeArticle } from '../groupe-article.model';
import { GroupeArticleService } from '../service/groupe-article.service';

import { GroupeArticleFormService } from './groupe-article-form.service';
import { GroupeArticleUpdate } from './groupe-article-update';

describe('GroupeArticle Management Update Component', () => {
  let comp: GroupeArticleUpdate;
  let fixture: ComponentFixture<GroupeArticleUpdate>;
  let activatedRoute: ActivatedRoute;
  let groupeArticleFormService: GroupeArticleFormService;
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

    fixture = TestBed.createComponent(GroupeArticleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    groupeArticleFormService = TestBed.inject(GroupeArticleFormService);
    groupeArticleService = TestBed.inject(GroupeArticleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const groupeArticle: IGroupeArticle = { id: 28799, tauxRedevance: 7.5 };

      activatedRoute.data = of({ groupeArticle });
      comp.ngOnInit();

      expect(comp.groupeArticle).toEqual(groupeArticle);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGroupeArticle>();
      const groupeArticle = { id: 2930 };
      vitest.spyOn(groupeArticleFormService, 'getGroupeArticle').mockReturnValue(groupeArticle);
      vitest.spyOn(groupeArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groupeArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(groupeArticle);
      saveSubject.complete();

      // THEN
      expect(groupeArticleFormService.getGroupeArticle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(groupeArticleService.update).toHaveBeenCalledWith(expect.objectContaining(groupeArticle));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IGroupeArticle>();
      const groupeArticle = { id: 2930 };
      vitest.spyOn(groupeArticleFormService, 'getGroupeArticle').mockReturnValue({ id: null });
      vitest.spyOn(groupeArticleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groupeArticle: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(groupeArticle);
      saveSubject.complete();

      // THEN
      expect(groupeArticleFormService.getGroupeArticle).toHaveBeenCalled();
      expect(groupeArticleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IGroupeArticle>();
      const groupeArticle = { id: 2930 };
      vitest.spyOn(groupeArticleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groupeArticle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(groupeArticleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
