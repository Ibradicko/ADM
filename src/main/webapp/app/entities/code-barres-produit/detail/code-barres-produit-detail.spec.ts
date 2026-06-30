import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { CodeBarresProduitDetail } from './code-barres-produit-detail';

describe('CodeBarresProduit Management Detail Component', () => {
  let comp: CodeBarresProduitDetail;
  let fixture: ComponentFixture<CodeBarresProduitDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./code-barres-produit-detail').then(m => m.CodeBarresProduitDetail),
              resolve: { codeBarresProduit: () => of({ id: 14062 }) },
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
    fixture = TestBed.createComponent(CodeBarresProduitDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load codeBarresProduit on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CodeBarresProduitDetail);

      // THEN
      expect(instance.codeBarresProduit()).toEqual(expect.objectContaining({ id: 14062 }));
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
