import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { EtiquetteProduitDetail } from './etiquette-produit-detail';

describe('EtiquetteProduit Management Detail Component', () => {
  let comp: EtiquetteProduitDetail;
  let fixture: ComponentFixture<EtiquetteProduitDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./etiquette-produit-detail').then(m => m.EtiquetteProduitDetail),
              resolve: { etiquetteProduit: () => of({ id: 25712 }) },
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
    fixture = TestBed.createComponent(EtiquetteProduitDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load etiquetteProduit on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EtiquetteProduitDetail);

      // THEN
      expect(instance.etiquetteProduit()).toEqual(expect.objectContaining({ id: 25712 }));
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
