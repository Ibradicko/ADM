import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILocataire } from '../locataire.model';
import { LocataireService } from '../service/locataire.service';

import { LocataireFormService } from './locataire-form.service';
import { LocataireUpdate } from './locataire-update';

describe('Locataire Management Update Component', () => {
  let comp: LocataireUpdate;
  let fixture: ComponentFixture<LocataireUpdate>;
  let activatedRoute: ActivatedRoute;
  let locataireFormService: LocataireFormService;
  let locataireService: LocataireService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(LocataireUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    locataireFormService = TestBed.inject(LocataireFormService);
    locataireService = TestBed.inject(LocataireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const locataire: ILocataire = { id: 24112 };

      activatedRoute.data = of({ locataire });
      comp.ngOnInit();

      expect(comp.locataire).toEqual(locataire);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILocataire>();
      const locataire = { id: 3768 };
      vitest.spyOn(locataireFormService, 'getLocataire').mockReturnValue(locataire);
      vitest.spyOn(locataireService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locataire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(locataire);
      saveSubject.complete();

      // THEN
      expect(locataireFormService.getLocataire).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(locataireService.update).toHaveBeenCalledWith(expect.objectContaining(locataire));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILocataire>();
      const locataire = { id: 3768 };
      vitest.spyOn(locataireFormService, 'getLocataire').mockReturnValue({ id: null });
      vitest.spyOn(locataireService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locataire: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(locataire);
      saveSubject.complete();

      // THEN
      expect(locataireFormService.getLocataire).toHaveBeenCalled();
      expect(locataireService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILocataire>();
      const locataire = { id: 3768 };
      vitest.spyOn(locataireService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locataire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(locataireService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
