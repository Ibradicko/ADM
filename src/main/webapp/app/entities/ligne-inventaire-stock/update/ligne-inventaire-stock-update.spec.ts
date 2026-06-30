import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IInventaireStock } from 'app/entities/inventaire-stock/inventaire-stock.model';
import { InventaireStockService } from 'app/entities/inventaire-stock/service/inventaire-stock.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { ILigneInventaireStock } from '../ligne-inventaire-stock.model';
import { LigneInventaireStockService } from '../service/ligne-inventaire-stock.service';

import { LigneInventaireStockFormService } from './ligne-inventaire-stock-form.service';
import { LigneInventaireStockUpdate } from './ligne-inventaire-stock-update';

describe('LigneInventaireStock Management Update Component', () => {
  let comp: LigneInventaireStockUpdate;
  let fixture: ComponentFixture<LigneInventaireStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneInventaireStockFormService: LigneInventaireStockFormService;
  let ligneInventaireStockService: LigneInventaireStockService;
  let inventaireStockService: InventaireStockService;
  let produitService: ProduitService;

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

    fixture = TestBed.createComponent(LigneInventaireStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneInventaireStockFormService = TestBed.inject(LigneInventaireStockFormService);
    ligneInventaireStockService = TestBed.inject(LigneInventaireStockService);
    inventaireStockService = TestBed.inject(InventaireStockService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call InventaireStock query and add missing value', () => {
      const ligneInventaireStock: ILigneInventaireStock = { id: 1239 };
      const inventaire: IInventaireStock = { id: 31192 };
      ligneInventaireStock.inventaire = inventaire;

      const inventaireStockCollection: IInventaireStock[] = [{ id: 31192 }];
      vitest.spyOn(inventaireStockService, 'query').mockReturnValue(of(new HttpResponse({ body: inventaireStockCollection })));
      const additionalInventaireStocks = [inventaire];
      const expectedCollection: IInventaireStock[] = [...additionalInventaireStocks, ...inventaireStockCollection];
      vitest.spyOn(inventaireStockService, 'addInventaireStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneInventaireStock });
      comp.ngOnInit();

      expect(inventaireStockService.query).toHaveBeenCalled();
      expect(inventaireStockService.addInventaireStockToCollectionIfMissing).toHaveBeenCalledWith(
        inventaireStockCollection,
        ...additionalInventaireStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.inventaireStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const ligneInventaireStock: ILigneInventaireStock = { id: 1239 };
      const produit: IProduit = { id: 28529 };
      ligneInventaireStock.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneInventaireStock });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneInventaireStock: ILigneInventaireStock = { id: 1239 };
      const inventaire: IInventaireStock = { id: 31192 };
      ligneInventaireStock.inventaire = inventaire;
      const produit: IProduit = { id: 28529 };
      ligneInventaireStock.produit = produit;

      activatedRoute.data = of({ ligneInventaireStock });
      comp.ngOnInit();

      expect(comp.inventaireStocksSharedCollection()).toContainEqual(inventaire);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.ligneInventaireStock).toEqual(ligneInventaireStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneInventaireStock>();
      const ligneInventaireStock = { id: 20417 };
      vitest.spyOn(ligneInventaireStockFormService, 'getLigneInventaireStock').mockReturnValue(ligneInventaireStock);
      vitest.spyOn(ligneInventaireStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneInventaireStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneInventaireStock);
      saveSubject.complete();

      // THEN
      expect(ligneInventaireStockFormService.getLigneInventaireStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneInventaireStockService.update).toHaveBeenCalledWith(expect.objectContaining(ligneInventaireStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneInventaireStock>();
      const ligneInventaireStock = { id: 20417 };
      vitest.spyOn(ligneInventaireStockFormService, 'getLigneInventaireStock').mockReturnValue({ id: null });
      vitest.spyOn(ligneInventaireStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneInventaireStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneInventaireStock);
      saveSubject.complete();

      // THEN
      expect(ligneInventaireStockFormService.getLigneInventaireStock).toHaveBeenCalled();
      expect(ligneInventaireStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneInventaireStock>();
      const ligneInventaireStock = { id: 20417 };
      vitest.spyOn(ligneInventaireStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneInventaireStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneInventaireStockService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareInventaireStock', () => {
      it('should forward to inventaireStockService', () => {
        const entity = { id: 31192 };
        const entity2 = { id: 105 };
        vitest.spyOn(inventaireStockService, 'compareInventaireStock');
        comp.compareInventaireStock(entity, entity2);
        expect(inventaireStockService.compareInventaireStock).toHaveBeenCalledWith(entity, entity2);
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
  });
});
