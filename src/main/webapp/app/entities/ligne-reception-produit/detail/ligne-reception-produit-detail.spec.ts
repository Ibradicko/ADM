import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { LigneReceptionProduitDetail } from './ligne-reception-produit-detail';

describe('LigneReceptionProduit Management Detail Component', () => {
  let comp: LigneReceptionProduitDetail;
  let fixture: ComponentFixture<LigneReceptionProduitDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./ligne-reception-produit-detail').then(m => m.LigneReceptionProduitDetail),
              resolve: { ligneReceptionProduit: () => of({ id: 9739 }) },
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
    fixture = TestBed.createComponent(LigneReceptionProduitDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load ligneReceptionProduit on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LigneReceptionProduitDetail);

      // THEN
      expect(instance.ligneReceptionProduit()).toEqual(expect.objectContaining({ id: 9739 }));
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
