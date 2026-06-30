import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from '../depot-stock.model';
import { DepotStockService } from '../service/depot-stock.service';

import { DepotStockFormService } from './depot-stock-form.service';
import { DepotStockUpdate } from './depot-stock-update';

describe('DepotStock Management Update Component', () => {
  let comp: DepotStockUpdate;
  let fixture: ComponentFixture<DepotStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let depotStockFormService: DepotStockFormService;
  let depotStockService: DepotStockService;
  let boutiqueService: BoutiqueService;

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

    fixture = TestBed.createComponent(DepotStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    depotStockFormService = TestBed.inject(DepotStockFormService);
    depotStockService = TestBed.inject(DepotStockService);
    boutiqueService = TestBed.inject(BoutiqueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const depotStock: IDepotStock = { id: 25113 };
      const boutique: IBoutique = { id: 5005 };
      depotStock.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ depotStock });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const depotStock: IDepotStock = { id: 25113 };
      const boutique: IBoutique = { id: 5005 };
      depotStock.boutique = boutique;

      activatedRoute.data = of({ depotStock });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.depotStock).toEqual(depotStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDepotStock>();
      const depotStock = { id: 26721 };
      vitest.spyOn(depotStockFormService, 'getDepotStock').mockReturnValue(depotStock);
      vitest.spyOn(depotStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ depotStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(depotStock);
      saveSubject.complete();

      // THEN
      expect(depotStockFormService.getDepotStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(depotStockService.update).toHaveBeenCalledWith(expect.objectContaining(depotStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDepotStock>();
      const depotStock = { id: 26721 };
      vitest.spyOn(depotStockFormService, 'getDepotStock').mockReturnValue({ id: null });
      vitest.spyOn(depotStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ depotStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(depotStock);
      saveSubject.complete();

      // THEN
      expect(depotStockFormService.getDepotStock).toHaveBeenCalled();
      expect(depotStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDepotStock>();
      const depotStock = { id: 26721 };
      vitest.spyOn(depotStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ depotStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(depotStockService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBoutique', () => {
      it('should forward to boutiqueService', () => {
        const entity = { id: 5005 };
        const entity2 = { id: 26278 };
        vitest.spyOn(boutiqueService, 'compareBoutique');
        comp.compareBoutique(entity, entity2);
        expect(boutiqueService.compareBoutique).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
