import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { TransfertStockService } from 'app/entities/transfert-stock/service/transfert-stock.service';
import { ITransfertStock } from 'app/entities/transfert-stock/transfert-stock.model';
import { ILigneTransfertStock } from '../ligne-transfert-stock.model';
import { LigneTransfertStockService } from '../service/ligne-transfert-stock.service';

import { LigneTransfertStockFormService } from './ligne-transfert-stock-form.service';
import { LigneTransfertStockUpdate } from './ligne-transfert-stock-update';

describe('LigneTransfertStock Management Update Component', () => {
  let comp: LigneTransfertStockUpdate;
  let fixture: ComponentFixture<LigneTransfertStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneTransfertStockFormService: LigneTransfertStockFormService;
  let ligneTransfertStockService: LigneTransfertStockService;
  let transfertStockService: TransfertStockService;
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

    fixture = TestBed.createComponent(LigneTransfertStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneTransfertStockFormService = TestBed.inject(LigneTransfertStockFormService);
    ligneTransfertStockService = TestBed.inject(LigneTransfertStockService);
    transfertStockService = TestBed.inject(TransfertStockService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call TransfertStock query and add missing value', () => {
      const ligneTransfertStock: ILigneTransfertStock = { id: 1772 };
      const transfert: ITransfertStock = { id: 31780 };
      ligneTransfertStock.transfert = transfert;

      const transfertStockCollection: ITransfertStock[] = [{ id: 31780 }];
      vitest.spyOn(transfertStockService, 'query').mockReturnValue(of(new HttpResponse({ body: transfertStockCollection })));
      const additionalTransfertStocks = [transfert];
      const expectedCollection: ITransfertStock[] = [...additionalTransfertStocks, ...transfertStockCollection];
      vitest.spyOn(transfertStockService, 'addTransfertStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneTransfertStock });
      comp.ngOnInit();

      expect(transfertStockService.query).toHaveBeenCalled();
      expect(transfertStockService.addTransfertStockToCollectionIfMissing).toHaveBeenCalledWith(
        transfertStockCollection,
        ...additionalTransfertStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.transfertStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const ligneTransfertStock: ILigneTransfertStock = { id: 1772 };
      const produit: IProduit = { id: 28529 };
      ligneTransfertStock.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneTransfertStock });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneTransfertStock: ILigneTransfertStock = { id: 1772 };
      const transfert: ITransfertStock = { id: 31780 };
      ligneTransfertStock.transfert = transfert;
      const produit: IProduit = { id: 28529 };
      ligneTransfertStock.produit = produit;

      activatedRoute.data = of({ ligneTransfertStock });
      comp.ngOnInit();

      expect(comp.transfertStocksSharedCollection()).toContainEqual(transfert);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.ligneTransfertStock).toEqual(ligneTransfertStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneTransfertStock>();
      const ligneTransfertStock = { id: 19112 };
      vitest.spyOn(ligneTransfertStockFormService, 'getLigneTransfertStock').mockReturnValue(ligneTransfertStock);
      vitest.spyOn(ligneTransfertStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneTransfertStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneTransfertStock);
      saveSubject.complete();

      // THEN
      expect(ligneTransfertStockFormService.getLigneTransfertStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneTransfertStockService.update).toHaveBeenCalledWith(expect.objectContaining(ligneTransfertStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneTransfertStock>();
      const ligneTransfertStock = { id: 19112 };
      vitest.spyOn(ligneTransfertStockFormService, 'getLigneTransfertStock').mockReturnValue({ id: null });
      vitest.spyOn(ligneTransfertStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneTransfertStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneTransfertStock);
      saveSubject.complete();

      // THEN
      expect(ligneTransfertStockFormService.getLigneTransfertStock).toHaveBeenCalled();
      expect(ligneTransfertStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneTransfertStock>();
      const ligneTransfertStock = { id: 19112 };
      vitest.spyOn(ligneTransfertStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneTransfertStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneTransfertStockService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTransfertStock', () => {
      it('should forward to transfertStockService', () => {
        const entity = { id: 31780 };
        const entity2 = { id: 6468 };
        vitest.spyOn(transfertStockService, 'compareTransfertStock');
        comp.compareTransfertStock(entity, entity2);
        expect(transfertStockService.compareTransfertStock).toHaveBeenCalledWith(entity, entity2);
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
