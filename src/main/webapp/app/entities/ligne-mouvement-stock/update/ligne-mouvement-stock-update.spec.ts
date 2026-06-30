import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { IMouvementStock } from 'app/entities/mouvement-stock/mouvement-stock.model';
import { MouvementStockService } from 'app/entities/mouvement-stock/service/mouvement-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { ILigneMouvementStock } from '../ligne-mouvement-stock.model';
import { LigneMouvementStockService } from '../service/ligne-mouvement-stock.service';

import { LigneMouvementStockFormService } from './ligne-mouvement-stock-form.service';
import { LigneMouvementStockUpdate } from './ligne-mouvement-stock-update';

describe('LigneMouvementStock Management Update Component', () => {
  let comp: LigneMouvementStockUpdate;
  let fixture: ComponentFixture<LigneMouvementStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneMouvementStockFormService: LigneMouvementStockFormService;
  let ligneMouvementStockService: LigneMouvementStockService;
  let mouvementStockService: MouvementStockService;
  let produitService: ProduitService;
  let depotStockService: DepotStockService;

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

    fixture = TestBed.createComponent(LigneMouvementStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneMouvementStockFormService = TestBed.inject(LigneMouvementStockFormService);
    ligneMouvementStockService = TestBed.inject(LigneMouvementStockService);
    mouvementStockService = TestBed.inject(MouvementStockService);
    produitService = TestBed.inject(ProduitService);
    depotStockService = TestBed.inject(DepotStockService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MouvementStock query and add missing value', () => {
      const ligneMouvementStock: ILigneMouvementStock = { id: 7678 };
      const mouvement: IMouvementStock = { id: 32109 };
      ligneMouvementStock.mouvement = mouvement;

      const mouvementStockCollection: IMouvementStock[] = [{ id: 32109 }];
      vitest.spyOn(mouvementStockService, 'query').mockReturnValue(of(new HttpResponse({ body: mouvementStockCollection })));
      const additionalMouvementStocks = [mouvement];
      const expectedCollection: IMouvementStock[] = [...additionalMouvementStocks, ...mouvementStockCollection];
      vitest.spyOn(mouvementStockService, 'addMouvementStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      expect(mouvementStockService.query).toHaveBeenCalled();
      expect(mouvementStockService.addMouvementStockToCollectionIfMissing).toHaveBeenCalledWith(
        mouvementStockCollection,
        ...additionalMouvementStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.mouvementStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const ligneMouvementStock: ILigneMouvementStock = { id: 7678 };
      const produit: IProduit = { id: 28529 };
      ligneMouvementStock.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call DepotStock query and add missing value', () => {
      const ligneMouvementStock: ILigneMouvementStock = { id: 7678 };
      const depot: IDepotStock = { id: 26721 };
      ligneMouvementStock.depot = depot;

      const depotStockCollection: IDepotStock[] = [{ id: 26721 }];
      vitest.spyOn(depotStockService, 'query').mockReturnValue(of(new HttpResponse({ body: depotStockCollection })));
      const additionalDepotStocks = [depot];
      const expectedCollection: IDepotStock[] = [...additionalDepotStocks, ...depotStockCollection];
      vitest.spyOn(depotStockService, 'addDepotStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      expect(depotStockService.query).toHaveBeenCalled();
      expect(depotStockService.addDepotStockToCollectionIfMissing).toHaveBeenCalledWith(
        depotStockCollection,
        ...additionalDepotStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.depotStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneMouvementStock: ILigneMouvementStock = { id: 7678 };
      const mouvement: IMouvementStock = { id: 32109 };
      ligneMouvementStock.mouvement = mouvement;
      const produit: IProduit = { id: 28529 };
      ligneMouvementStock.produit = produit;
      const depot: IDepotStock = { id: 26721 };
      ligneMouvementStock.depot = depot;

      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      expect(comp.mouvementStocksSharedCollection()).toContainEqual(mouvement);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.depotStocksSharedCollection()).toContainEqual(depot);
      expect(comp.ligneMouvementStock).toEqual(ligneMouvementStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneMouvementStock>();
      const ligneMouvementStock = { id: 15898 };
      vitest.spyOn(ligneMouvementStockFormService, 'getLigneMouvementStock').mockReturnValue(ligneMouvementStock);
      vitest.spyOn(ligneMouvementStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneMouvementStock);
      saveSubject.complete();

      // THEN
      expect(ligneMouvementStockFormService.getLigneMouvementStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneMouvementStockService.update).toHaveBeenCalledWith(expect.objectContaining(ligneMouvementStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneMouvementStock>();
      const ligneMouvementStock = { id: 15898 };
      vitest.spyOn(ligneMouvementStockFormService, 'getLigneMouvementStock').mockReturnValue({ id: null });
      vitest.spyOn(ligneMouvementStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneMouvementStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneMouvementStock);
      saveSubject.complete();

      // THEN
      expect(ligneMouvementStockFormService.getLigneMouvementStock).toHaveBeenCalled();
      expect(ligneMouvementStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneMouvementStock>();
      const ligneMouvementStock = { id: 15898 };
      vitest.spyOn(ligneMouvementStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneMouvementStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneMouvementStockService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMouvementStock', () => {
      it('should forward to mouvementStockService', () => {
        const entity = { id: 32109 };
        const entity2 = { id: 26007 };
        vitest.spyOn(mouvementStockService, 'compareMouvementStock');
        comp.compareMouvementStock(entity, entity2);
        expect(mouvementStockService.compareMouvementStock).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProduit', () => {
      it('should forward to produitService', () => {
        const entity = { id: 28529 };
        const entity2 = { id: 21239 };
        vitest.spyOn(produitService, 'compareProduit');
        comp.compareProduit(entity, entity2);
        expect(produitService.compareProduit).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDepotStock', () => {
      it('should forward to depotStockService', () => {
        const entity = { id: 26721 };
        const entity2 = { id: 25113 };
        vitest.spyOn(depotStockService, 'compareDepotStock');
        comp.compareDepotStock(entity, entity2);
        expect(depotStockService.compareDepotStock).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
