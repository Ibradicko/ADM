import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IParametreGlobal } from '../parametre-global.model';
import { ParametreGlobalService } from '../service/parametre-global.service';

import { ParametreGlobalFormService } from './parametre-global-form.service';
import { ParametreGlobalUpdate } from './parametre-global-update';

describe('ParametreGlobal Management Update Component', () => {
  let comp: ParametreGlobalUpdate;
  let fixture: ComponentFixture<ParametreGlobalUpdate>;
  let activatedRoute: ActivatedRoute;
  let parametreGlobalFormService: ParametreGlobalFormService;
  let parametreGlobalService: ParametreGlobalService;

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

    fixture = TestBed.createComponent(ParametreGlobalUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parametreGlobalFormService = TestBed.inject(ParametreGlobalFormService);
    parametreGlobalService = TestBed.inject(ParametreGlobalService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const parametreGlobal: IParametreGlobal = { id: 11141 };

      activatedRoute.data = of({ parametreGlobal });
      comp.ngOnInit();

      expect(comp.parametreGlobal).toEqual(parametreGlobal);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreGlobal>();
      const parametreGlobal = { id: 778 };
      vitest.spyOn(parametreGlobalFormService, 'getParametreGlobal').mockReturnValue(parametreGlobal);
      vitest.spyOn(parametreGlobalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreGlobal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(parametreGlobal);
      saveSubject.complete();

      // THEN
      expect(parametreGlobalFormService.getParametreGlobal).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parametreGlobalService.update).toHaveBeenCalledWith(expect.objectContaining(parametreGlobal));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreGlobal>();
      const parametreGlobal = { id: 778 };
      vitest.spyOn(parametreGlobalFormService, 'getParametreGlobal').mockReturnValue({ id: null });
      vitest.spyOn(parametreGlobalService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreGlobal: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(parametreGlobal);
      saveSubject.complete();

      // THEN
      expect(parametreGlobalFormService.getParametreGlobal).toHaveBeenCalled();
      expect(parametreGlobalService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IParametreGlobal>();
      const parametreGlobal = { id: 778 };
      vitest.spyOn(parametreGlobalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parametreGlobal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parametreGlobalService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
