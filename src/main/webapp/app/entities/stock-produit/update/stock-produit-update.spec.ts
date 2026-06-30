import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { StockProduitService } from '../service/stock-produit.service';
import { IStockProduit } from '../stock-produit.model';

import { StockProduitFormService } from './stock-produit-form.service';
import { StockProduitUpdate } from './stock-produit-update';

describe('StockProduit Management Update Component', () => {
  let comp: StockProduitUpdate;
  let fixture: ComponentFixture<StockProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let stockProduitFormService: StockProduitFormService;
  let stockProduitService: StockProduitService;
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

    fixture = TestBed.createComponent(StockProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockProduitFormService = TestBed.inject(StockProduitFormService);
    stockProduitService = TestBed.inject(StockProduitService);
    produitService = TestBed.inject(ProduitService);
    depotStockService = TestBed.inject(DepotStockService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const stockProduit: IStockProduit = { id: 29002 };
      const produit: IProduit = { id: 28529 };
      stockProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call DepotStock query and add missing value', () => {
      const stockProduit: IStockProduit = { id: 29002 };
      const depot: IDepotStock = { id: 26721 };
      stockProduit.depot = depot;

      const depotStockCollection: IDepotStock[] = [{ id: 26721 }];
      vitest.spyOn(depotStockService, 'query').mockReturnValue(of(new HttpResponse({ body: depotStockCollection })));
      const additionalDepotStocks = [depot];
      const expectedCollection: IDepotStock[] = [...additionalDepotStocks, ...depotStockCollection];
      vitest.spyOn(depotStockService, 'addDepotStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockProduit });
      comp.ngOnInit();

      expect(depotStockService.query).toHaveBeenCalled();
      expect(depotStockService.addDepotStockToCollectionIfMissing).toHaveBeenCalledWith(
        depotStockCollection,
        ...additionalDepotStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.depotStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const stockProduit: IStockProduit = { id: 29002 };
      const produit: IProduit = { id: 28529 };
      stockProduit.produit = produit;
      const depot: IDepotStock = { id: 26721 };
      stockProduit.depot = depot;

      activatedRoute.data = of({ stockProduit });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.depotStocksSharedCollection()).toContainEqual(depot);
      expect(comp.stockProduit).toEqual(stockProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockProduit>();
      const stockProduit = { id: 21740 };
      vitest.spyOn(stockProduitFormService, 'getStockProduit').mockReturnValue(stockProduit);
      vitest.spyOn(stockProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockProduit);
      saveSubject.complete();

      // THEN
      expect(stockProduitFormService.getStockProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockProduitService.update).toHaveBeenCalledWith(expect.objectContaining(stockProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IStockProduit>();
      const stockProduit = { id: 21740 };
      vitest.spyOn(stockProduitFormService, 'getStockProduit').mockReturnValue({ id: null });
      vitest.spyOn(stockProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(stockProduit);
      saveSubject.complete();

      // THEN
      expect(stockProduitFormService.getStockProduit).toHaveBeenCalled();
      expect(stockProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IStockProduit>();
      const stockProduit = { id: 21740 };
      vitest.spyOn(stockProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockProduitService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
