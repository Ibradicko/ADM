import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { ILigneCalculRedevance } from '../ligne-calcul-redevance.model';
import { LigneCalculRedevanceService } from '../service/ligne-calcul-redevance.service';

import { LigneCalculRedevanceFormService } from './ligne-calcul-redevance-form.service';
import { LigneCalculRedevanceUpdate } from './ligne-calcul-redevance-update';

describe('LigneCalculRedevance Management Update Component', () => {
  let comp: LigneCalculRedevanceUpdate;
  let fixture: ComponentFixture<LigneCalculRedevanceUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneCalculRedevanceFormService: LigneCalculRedevanceFormService;
  let ligneCalculRedevanceService: LigneCalculRedevanceService;
  let calculRedevanceService: CalculRedevanceService;
  let venteService: VenteService;

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

    fixture = TestBed.createComponent(LigneCalculRedevanceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneCalculRedevanceFormService = TestBed.inject(LigneCalculRedevanceFormService);
    ligneCalculRedevanceService = TestBed.inject(LigneCalculRedevanceService);
    calculRedevanceService = TestBed.inject(CalculRedevanceService);
    venteService = TestBed.inject(VenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CalculRedevance query and add missing value', () => {
      const ligneCalculRedevance: ILigneCalculRedevance = { id: 29287 };
      const calcul: ICalculRedevance = { id: 28461 };
      ligneCalculRedevance.calcul = calcul;

      const calculRedevanceCollection: ICalculRedevance[] = [{ id: 28461 }];
      vitest.spyOn(calculRedevanceService, 'query').mockReturnValue(of(new HttpResponse({ body: calculRedevanceCollection })));
      const additionalCalculRedevances = [calcul];
      const expectedCollection: ICalculRedevance[] = [...additionalCalculRedevances, ...calculRedevanceCollection];
      vitest.spyOn(calculRedevanceService, 'addCalculRedevanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneCalculRedevance });
      comp.ngOnInit();

      expect(calculRedevanceService.query).toHaveBeenCalled();
      expect(calculRedevanceService.addCalculRedevanceToCollectionIfMissing).toHaveBeenCalledWith(
        calculRedevanceCollection,
        ...additionalCalculRedevances.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.calculRedevancesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Vente query and add missing value', () => {
      const ligneCalculRedevance: ILigneCalculRedevance = { id: 29287 };
      const vente: IVente = { id: 25749 };
      ligneCalculRedevance.vente = vente;

      const venteCollection: IVente[] = [{ id: 25749 }];
      vitest.spyOn(venteService, 'query').mockReturnValue(of(new HttpResponse({ body: venteCollection })));
      const additionalVentes = [vente];
      const expectedCollection: IVente[] = [...additionalVentes, ...venteCollection];
      vitest.spyOn(venteService, 'addVenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneCalculRedevance });
      comp.ngOnInit();

      expect(venteService.query).toHaveBeenCalled();
      expect(venteService.addVenteToCollectionIfMissing).toHaveBeenCalledWith(
        venteCollection,
        ...additionalVentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ventesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneCalculRedevance: ILigneCalculRedevance = { id: 29287 };
      const calcul: ICalculRedevance = { id: 28461 };
      ligneCalculRedevance.calcul = calcul;
      const vente: IVente = { id: 25749 };
      ligneCalculRedevance.vente = vente;

      activatedRoute.data = of({ ligneCalculRedevance });
      comp.ngOnInit();

      expect(comp.calculRedevancesSharedCollection()).toContainEqual(calcul);
      expect(comp.ventesSharedCollection()).toContainEqual(vente);
      expect(comp.ligneCalculRedevance).toEqual(ligneCalculRedevance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCalculRedevance>();
      const ligneCalculRedevance = { id: 14249 };
      vitest.spyOn(ligneCalculRedevanceFormService, 'getLigneCalculRedevance').mockReturnValue(ligneCalculRedevance);
      vitest.spyOn(ligneCalculRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCalculRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneCalculRedevance);
      saveSubject.complete();

      // THEN
      expect(ligneCalculRedevanceFormService.getLigneCalculRedevance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneCalculRedevanceService.update).toHaveBeenCalledWith(expect.objectContaining(ligneCalculRedevance));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCalculRedevance>();
      const ligneCalculRedevance = { id: 14249 };
      vitest.spyOn(ligneCalculRedevanceFormService, 'getLigneCalculRedevance').mockReturnValue({ id: null });
      vitest.spyOn(ligneCalculRedevanceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCalculRedevance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneCalculRedevance);
      saveSubject.complete();

      // THEN
      expect(ligneCalculRedevanceFormService.getLigneCalculRedevance).toHaveBeenCalled();
      expect(ligneCalculRedevanceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCalculRedevance>();
      const ligneCalculRedevance = { id: 14249 };
      vitest.spyOn(ligneCalculRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCalculRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneCalculRedevanceService.update).toHaveBeenCalled();
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

    describe('compareVente', () => {
      it('should forward to venteService', () => {
        const entity = { id: 25749 };
        const entity2 = { id: 9754 };
        vitest.spyOn(venteService, 'compareVente');
        comp.compareVente(entity, entity2);
        expect(venteService.compareVente).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
