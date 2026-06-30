import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { IRegularisationRedevance } from '../regularisation-redevance.model';
import { RegularisationRedevanceService } from '../service/regularisation-redevance.service';

import { RegularisationRedevanceFormService } from './regularisation-redevance-form.service';
import { RegularisationRedevanceUpdate } from './regularisation-redevance-update';

describe('RegularisationRedevance Management Update Component', () => {
  let comp: RegularisationRedevanceUpdate;
  let fixture: ComponentFixture<RegularisationRedevanceUpdate>;
  let activatedRoute: ActivatedRoute;
  let regularisationRedevanceFormService: RegularisationRedevanceFormService;
  let regularisationRedevanceService: RegularisationRedevanceService;
  let calculRedevanceService: CalculRedevanceService;

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

    fixture = TestBed.createComponent(RegularisationRedevanceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    regularisationRedevanceFormService = TestBed.inject(RegularisationRedevanceFormService);
    regularisationRedevanceService = TestBed.inject(RegularisationRedevanceService);
    calculRedevanceService = TestBed.inject(CalculRedevanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CalculRedevance query and add missing value', () => {
      const regularisationRedevance: IRegularisationRedevance = { id: 26564 };
      const calcul: ICalculRedevance = { id: 28461 };
      regularisationRedevance.calcul = calcul;

      const calculRedevanceCollection: ICalculRedevance[] = [{ id: 28461 }];
      vitest.spyOn(calculRedevanceService, 'query').mockReturnValue(of(new HttpResponse({ body: calculRedevanceCollection })));
      const additionalCalculRedevances = [calcul];
      const expectedCollection: ICalculRedevance[] = [...additionalCalculRedevances, ...calculRedevanceCollection];
      vitest.spyOn(calculRedevanceService, 'addCalculRedevanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ regularisationRedevance });
      comp.ngOnInit();

      expect(calculRedevanceService.query).toHaveBeenCalled();
      expect(calculRedevanceService.addCalculRedevanceToCollectionIfMissing).toHaveBeenCalledWith(
        calculRedevanceCollection,
        ...additionalCalculRedevances.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.calculRedevancesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const regularisationRedevance: IRegularisationRedevance = { id: 26564 };
      const calcul: ICalculRedevance = { id: 28461 };
      regularisationRedevance.calcul = calcul;

      activatedRoute.data = of({ regularisationRedevance });
      comp.ngOnInit();

      expect(comp.calculRedevancesSharedCollection()).toContainEqual(calcul);
      expect(comp.regularisationRedevance).toEqual(regularisationRedevance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRegularisationRedevance>();
      const regularisationRedevance = { id: 29254 };
      vitest.spyOn(regularisationRedevanceFormService, 'getRegularisationRedevance').mockReturnValue(regularisationRedevance);
      vitest.spyOn(regularisationRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regularisationRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(regularisationRedevance);
      saveSubject.complete();

      // THEN
      expect(regularisationRedevanceFormService.getRegularisationRedevance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(regularisationRedevanceService.update).toHaveBeenCalledWith(expect.objectContaining(regularisationRedevance));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRegularisationRedevance>();
      const regularisationRedevance = { id: 29254 };
      vitest.spyOn(regularisationRedevanceFormService, 'getRegularisationRedevance').mockReturnValue({ id: null });
      vitest.spyOn(regularisationRedevanceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regularisationRedevance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(regularisationRedevance);
      saveSubject.complete();

      // THEN
      expect(regularisationRedevanceFormService.getRegularisationRedevance).toHaveBeenCalled();
      expect(regularisationRedevanceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRegularisationRedevance>();
      const regularisationRedevance = { id: 29254 };
      vitest.spyOn(regularisationRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regularisationRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(regularisationRedevanceService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCalculRedevance', () => {
      it('should forward to calculRedevanceService', () => {
        const entity = { id: 28461 };
        const entity2 = { id: 4867 };
        vitest.spyOn(calculRedevanceService, 'compareCalculRedevance');
        comp.compareCalculRedevance(entity, entity2);
        expect(calculRedevanceService.compareCalculRedevance).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
