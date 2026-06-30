import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { SousFamilleArticleDetail } from './sous-famille-article-detail';

describe('SousFamilleArticle Management Detail Component', () => {
  let comp: SousFamilleArticleDetail;
  let fixture: ComponentFixture<SousFamilleArticleDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./sous-famille-article-detail').then(m => m.SousFamilleArticleDetail),
              resolve: { sousFamilleArticle: () => of({ id: 30207 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SousFamilleArticleDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load sousFamilleArticle on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SousFamilleArticleDetail);

      // THEN
      expect(instance.sousFamilleArticle()).toEqual(expect.objectContaining({ id: 30207 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
