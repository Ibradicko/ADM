import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { IPaiementRedevance } from '../paiement-redevance.model';
import { PaiementRedevanceService } from '../service/paiement-redevance.service';

import { PaiementRedevanceFormService } from './paiement-redevance-form.service';
import { PaiementRedevanceUpdate } from './paiement-redevance-update';

describe('PaiementRedevance Management Update Component', () => {
  let comp: PaiementRedevanceUpdate;
  let fixture: ComponentFixture<PaiementRedevanceUpdate>;
  let activatedRoute: ActivatedRoute;
  let paiementRedevanceFormService: PaiementRedevanceFormService;
  let paiementRedevanceService: PaiementRedevanceService;
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

    fixture = TestBed.createComponent(PaiementRedevanceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paiementRedevanceFormService = TestBed.inject(PaiementRedevanceFormService);
    paiementRedevanceService = TestBed.inject(PaiementRedevanceService);
    calculRedevanceService = TestBed.inject(CalculRedevanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CalculRedevance query and add missing value', () => {
      const paiementRedevance: IPaiementRedevance = { id: 27581 };
      const calcul: ICalculRedevance = { id: 28461 };
      paiementRedevance.calcul = calcul;

      const calculRedevanceCollection: ICalculRedevance[] = [{ id: 28461 }];
      vitest.spyOn(calculRedevanceService, 'query').mockReturnValue(of(new HttpResponse({ body: calculRedevanceCollection })));
      const additionalCalculRedevances = [calcul];
      const expectedCollection: ICalculRedevance[] = [...additionalCalculRedevances, ...calculRedevanceCollection];
      vitest.spyOn(calculRedevanceService, 'addCalculRedevanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paiementRedevance });
      comp.ngOnInit();

      expect(calculRedevanceService.query).toHaveBeenCalled();
      expect(calculRedevanceService.addCalculRedevanceToCollectionIfMissing).toHaveBeenCalledWith(
        calculRedevanceCollection,
        ...additionalCalculRedevances.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.calculRedevancesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const paiementRedevance: IPaiementRedevance = { id: 27581 };
      const calcul: ICalculRedevance = { id: 28461 };
      paiementRedevance.calcul = calcul;

      activatedRoute.data = of({ paiementRedevance });
      comp.ngOnInit();

      expect(comp.calculRedevancesSharedCollection()).toContainEqual(calcul);
      expect(comp.paiementRedevance).toEqual(paiementRedevance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementRedevance>();
      const paiementRedevance = { id: 32698 };
      vitest.spyOn(paiementRedevanceFormService, 'getPaiementRedevance').mockReturnValue(paiementRedevance);
      vitest.spyOn(paiementRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiementRedevance);
      saveSubject.complete();

      // THEN
      expect(paiementRedevanceFormService.getPaiementRedevance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paiementRedevanceService.update).toHaveBeenCalledWith(expect.objectContaining(paiementRedevance));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementRedevance>();
      const paiementRedevance = { id: 32698 };
      vitest.spyOn(paiementRedevanceFormService, 'getPaiementRedevance').mockReturnValue({ id: null });
      vitest.spyOn(paiementRedevanceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementRedevance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiementRedevance);
      saveSubject.complete();

      // THEN
      expect(paiementRedevanceFormService.getPaiementRedevance).toHaveBeenCalled();
      expect(paiementRedevanceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementRedevance>();
      const paiementRedevance = { id: 32698 };
      vitest.spyOn(paiementRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paiementRedevanceService.update).toHaveBeenCalled();
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
