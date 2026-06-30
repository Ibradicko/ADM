import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LigneCalculRedevanceDetail } from './ligne-calcul-redevance-detail';

describe('LigneCalculRedevance Management Detail Component', () => {
  let comp: LigneCalculRedevanceDetail;
  let fixture: ComponentFixture<LigneCalculRedevanceDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ligne-calcul-redevance-detail').then(m => m.LigneCalculRedevanceDetail),
              resolve: { ligneCalculRedevance: () => of({ id: 14249 }) },
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
    fixture = TestBed.createComponent(LigneCalculRedevanceDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load ligneCalculRedevance on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LigneCalculRedevanceDetail);

      // THEN
      expect(instance.ligneCalculRedevance()).toEqual(expect.objectContaining({ id: 14249 }));
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
