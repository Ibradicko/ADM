import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IParametreCodeBarres } from '../parametre-code-barres.model';
import { ParametreCodeBarresService } from '../service/parametre-code-barres.service';

import { ParametreCodeBarresFormService } from './parametre-code-barres-form.service';
import { ParametreCodeBarresUpdate } from './parametre-code-barres-update';

describe('ParametreCodeBarres Management Update Component', () => {
  let comp: ParametreCodeBarresUpdate;
  let fixture: ComponentFixture<ParametreCodeBarresUpdate>;
  let activatedRoute: ActivatedRoute;
  let parametreCodeBarresFormService: ParametreCodeBarresFormService;
  let parametreCodeBarresService: ParametreCodeBarresService;

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

    fixture = TestBed.createComponent(ParametreCodeBarresUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parametreCodeBarresFormService = TestBed.inject(ParametreCodeBarresFormService);
    parametreCodeBarresService = TestBed.inject(ParametreCodeBarresService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const parametreCodeBarres: IParametreCodeBarres = { id: 14628 };

      activatedRoute.data = of({ parametreCodeBarres });
      comp.ngOnInit();

      expect(comp.parametreCodeBarres).toEqual(parametreCodeBarres);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreCodeBarres>();
      const parametreCodeBarres = { id: 5126 };
      vitest.spyOn(parametreCodeBarresFormService, 'getParametreCodeBarres').mockReturnValue(parametreCodeBarres);
      vitest.spyOn(parametreCodeBarresService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreCodeBarres });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(parametreCodeBarres);
      saveSubject.complete();

      // THEN
      expect(parametreCodeBarresFormService.getParametreCodeBarres).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parametreCodeBarresService.update).toHaveBeenCalledWith(expect.objectContaining(parametreCodeBarres));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreCodeBarres>();
      const parametreCodeBarres = { id: 5126 };
      vitest.spyOn(parametreCodeBarresFormService, 'getParametreCodeBarres').mockReturnValue({ id: null });
      vitest.spyOn(parametreCodeBarresService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreCodeBarres: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(parametreCodeBarres);
      saveSubject.complete();

      // THEN
      expect(parametreCodeBarresFormService.getParametreCodeBarres).toHaveBeenCalled();
      expect(parametreCodeBarresService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreCodeBarres>();
      const parametreCodeBarres = { id: 5126 };
      vitest.spyOn(parametreCodeBarresService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreCodeBarres });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parametreCodeBarresService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
