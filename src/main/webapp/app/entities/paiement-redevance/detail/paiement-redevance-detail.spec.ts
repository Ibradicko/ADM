import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { PaiementRedevanceDetail } from './paiement-redevance-detail';

describe('PaiementRedevance Management Detail Component', () => {
  let comp: PaiementRedevanceDetail;
  let fixture: ComponentFixture<PaiementRedevanceDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./paiement-redevance-detail').then(m => m.PaiementRedevanceDetail),
              resolve: { paiementRedevance: () => of({ id: 32698 }) },
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
    fixture = TestBed.createComponent(PaiementRedevanceDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load paiementRedevance on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PaiementRedevanceDetail);

      // THEN
      expect(instance.paiementRedevance()).toEqual(expect.objectContaining({ id: 32698 }));
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
