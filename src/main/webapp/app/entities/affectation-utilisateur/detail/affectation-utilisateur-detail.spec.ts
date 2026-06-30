import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { AffectationUtilisateurDetail } from './affectation-utilisateur-detail';

describe('AffectationUtilisateur Management Detail Component', () => {
  let comp: AffectationUtilisateurDetail;
  let fixture: ComponentFixture<AffectationUtilisateurDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./affectation-utilisateur-detail').then(m => m.AffectationUtilisateurDetail),
              resolve: { affectationUtilisateur: () => of({ id: 4001 }) },
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
    fixture = TestBed.createComponent(AffectationUtilisateurDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load affectationUtilisateur on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AffectationUtilisateurDetail);

      // THEN
      expect(instance.affectationUtilisateur()).toEqual(expect.objectContaining({ id: 4001 }));
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
