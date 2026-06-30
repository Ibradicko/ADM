import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LigneMouvementStockDetail } from './ligne-mouvement-stock-detail';

describe('LigneMouvementStock Management Detail Component', () => {
  let comp: LigneMouvementStockDetail;
  let fixture: ComponentFixture<LigneMouvementStockDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ligne-mouvement-stock-detail').then(m => m.LigneMouvementStockDetail),
              resolve: { ligneMouvementStock: () => of({ id: 15898 }) },
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
    fixture = TestBed.createComponent(LigneMouvementStockDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load ligneMouvementStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LigneMouvementStockDetail);

      // THEN
      expect(instance.ligneMouvementStock()).toEqual(expect.objectContaining({ id: 15898 }));
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
