import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LotEtiquettesDetail } from './lot-etiquettes-detail';

describe('LotEtiquettes Management Detail Component', () => {
  let comp: LotEtiquettesDetail;
  let fixture: ComponentFixture<LotEtiquettesDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./lot-etiquettes-detail').then(m => m.LotEtiquettesDetail),
              resolve: { lotEtiquettes: () => of({ id: 1087 }) },
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
    fixture = TestBed.createComponent(LotEtiquettesDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load lotEtiquettes on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LotEtiquettesDetail);

      // THEN
      expect(instance.lotEtiquettes()).toEqual(expect.objectContaining({ id: 1087 }));
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
