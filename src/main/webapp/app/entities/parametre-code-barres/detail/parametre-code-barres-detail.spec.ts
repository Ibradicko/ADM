import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ParametreCodeBarresDetail } from './parametre-code-barres-detail';

describe('ParametreCodeBarres Management Detail Component', () => {
  let comp: ParametreCodeBarresDetail;
  let fixture: ComponentFixture<ParametreCodeBarresDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./parametre-code-barres-detail').then(m => m.ParametreCodeBarresDetail),
              resolve: { parametreCodeBarres: () => of({ id: 5126 }) },
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
    fixture = TestBed.createComponent(ParametreCodeBarresDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load parametreCodeBarres on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ParametreCodeBarresDetail);

      // THEN
      expect(instance.parametreCodeBarres()).toEqual(expect.objectContaining({ id: 5126 }));
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
