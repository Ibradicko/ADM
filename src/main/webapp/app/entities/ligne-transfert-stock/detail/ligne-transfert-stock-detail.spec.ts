import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LigneTransfertStockDetail } from './ligne-transfert-stock-detail';

describe('LigneTransfertStock Management Detail Component', () => {
  let comp: LigneTransfertStockDetail;
  let fixture: ComponentFixture<LigneTransfertStockDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ligne-transfert-stock-detail').then(m => m.LigneTransfertStockDetail),
              resolve: { ligneTransfertStock: () => of({ id: 19112 }) },
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
    fixture = TestBed.createComponent(LigneTransfertStockDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load ligneTransfertStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LigneTransfertStockDetail);

      // THEN
      expect(instance.ligneTransfertStock()).toEqual(expect.objectContaining({ id: 19112 }));
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
