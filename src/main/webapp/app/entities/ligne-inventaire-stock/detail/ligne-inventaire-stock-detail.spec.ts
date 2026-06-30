import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LigneInventaireStockDetail } from './ligne-inventaire-stock-detail';

describe('LigneInventaireStock Management Detail Component', () => {
  let comp: LigneInventaireStockDetail;
  let fixture: ComponentFixture<LigneInventaireStockDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ligne-inventaire-stock-detail').then(m => m.LigneInventaireStockDetail),
              resolve: { ligneInventaireStock: () => of({ id: 20417 }) },
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
    fixture = TestBed.createComponent(LigneInventaireStockDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load ligneInventaireStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LigneInventaireStockDetail);

      // THEN
      expect(instance.ligneInventaireStock()).toEqual(expect.objectContaining({ id: 20417 }));
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
