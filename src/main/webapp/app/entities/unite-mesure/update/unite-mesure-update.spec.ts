import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UniteMesureService } from '../service/unite-mesure.service';
import { IUniteMesure } from '../unite-mesure.model';

import { UniteMesureFormService } from './unite-mesure-form.service';
import { UniteMesureUpdate } from './unite-mesure-update';

describe('UniteMesure Management Update Component', () => {
  let comp: UniteMesureUpdate;
  let fixture: ComponentFixture<UniteMesureUpdate>;
  let activatedRoute: ActivatedRoute;
  let uniteMesureFormService: UniteMesureFormService;
  let uniteMesureService: UniteMesureService;

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

    fixture = TestBed.createComponent(UniteMesureUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    uniteMesureFormService = TestBed.inject(UniteMesureFormService);
    uniteMesureService = TestBed.inject(UniteMesureService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const uniteMesure: IUniteMesure = { id: 22001 };

      activatedRoute.data = of({ uniteMesure });
      comp.ngOnInit();

      expect(comp.uniteMesure).toEqual(uniteMesure);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IUniteMesure>();
      const uniteMesure = { id: 4120 };
      vitest.spyOn(uniteMesureFormService, 'getUniteMesure').mockReturnValue(uniteMesure);
      vitest.spyOn(uniteMesureService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uniteMesure });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(uniteMesure);
      saveSubject.complete();

      // THEN
      expect(uniteMesureFormService.getUniteMesure).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(uniteMesureService.update).toHaveBeenCalledWith(expect.objectContaining(uniteMesure));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IUniteMesure>();
      const uniteMesure = { id: 4120 };
      vitest.spyOn(uniteMesureFormService, 'getUniteMesure').mockReturnValue({ id: null });
      vitest.spyOn(uniteMesureService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uniteMesure: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(uniteMesure);
      saveSubject.complete();

      // THEN
      expect(uniteMesureFormService.getUniteMesure).toHaveBeenCalled();
      expect(uniteMesureService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IUniteMesure>();
      const uniteMesure = { id: 4120 };
      vitest.spyOn(uniteMesureService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uniteMesure });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(uniteMesureService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
