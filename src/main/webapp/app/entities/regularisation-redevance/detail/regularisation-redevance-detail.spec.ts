import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { RegularisationRedevanceDetail } from './regularisation-redevance-detail';

describe('RegularisationRedevance Management Detail Component', () => {
  let comp: RegularisationRedevanceDetail;
  let fixture: ComponentFixture<RegularisationRedevanceDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./regularisation-redevance-detail').then(m => m.RegularisationRedevanceDetail),
              resolve: { regularisationRedevance: () => of({ id: 29254 }) },
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
    fixture = TestBed.createComponent(RegularisationRedevanceDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load regularisationRedevance on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RegularisationRedevanceDetail);

      // THEN
      expect(instance.regularisationRedevance()).toEqual(expect.objectContaining({ id: 29254 }));
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
