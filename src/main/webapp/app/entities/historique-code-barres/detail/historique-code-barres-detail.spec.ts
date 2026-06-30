import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { HistoriqueCodeBarresDetail } from './historique-code-barres-detail';

describe('HistoriqueCodeBarres Management Detail Component', () => {
  let comp: HistoriqueCodeBarresDetail;
  let fixture: ComponentFixture<HistoriqueCodeBarresDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./historique-code-barres-detail').then(m => m.HistoriqueCodeBarresDetail),
              resolve: { historiqueCodeBarres: () => of({ id: 29449 }) },
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
    fixture = TestBed.createComponent(HistoriqueCodeBarresDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load historiqueCodeBarres on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HistoriqueCodeBarresDetail);

      // THEN
      expect(instance.historiqueCodeBarres()).toEqual(expect.objectContaining({ id: 29449 }));
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
