import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { ModePaiementRefDetail } from './mode-paiement-ref-detail';

describe('ModePaiementRef Management Detail Component', () => {
  let comp: ModePaiementRefDetail;
  let fixture: ComponentFixture<ModePaiementRefDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mode-paiement-ref-detail').then(m => m.ModePaiementRefDetail),
              resolve: { modePaiementRef: () => of({ id: 14388 }) },
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
    fixture = TestBed.createComponent(ModePaiementRefDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load modePaiementRef on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ModePaiementRefDetail);

      // THEN
      expect(instance.modePaiementRef()).toEqual(expect.objectContaining({ id: 14388 }));
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
