import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILotEtiquettes } from 'app/entities/lot-etiquettes/lot-etiquettes.model';
import { LotEtiquettesService } from 'app/entities/lot-etiquettes/service/lot-etiquettes.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IEtiquetteProduit } from '../etiquette-produit.model';
import { EtiquetteProduitService } from '../service/etiquette-produit.service';

import { EtiquetteProduitFormService } from './etiquette-produit-form.service';
import { EtiquetteProduitUpdate } from './etiquette-produit-update';

describe('EtiquetteProduit Management Update Component', () => {
  let comp: EtiquetteProduitUpdate;
  let fixture: ComponentFixture<EtiquetteProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let etiquetteProduitFormService: EtiquetteProduitFormService;
  let etiquetteProduitService: EtiquetteProduitService;
  let produitService: ProduitService;
  let lotEtiquettesService: LotEtiquettesService;

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

    fixture = TestBed.createComponent(EtiquetteProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    etiquetteProduitFormService = TestBed.inject(EtiquetteProduitFormService);
    etiquetteProduitService = TestBed.inject(EtiquetteProduitService);
    produitService = TestBed.inject(ProduitService);
    lotEtiquettesService = TestBed.inject(LotEtiquettesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const etiquetteProduit: IEtiquetteProduit = { id: 9016 };
      const produit: IProduit = { id: 28529 };
      etiquetteProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ etiquetteProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call LotEtiquettes query and add missing value', () => {
      const etiquetteProduit: IEtiquetteProduit = { id: 9016 };
      const lot: ILotEtiquettes = { id: 1087 };
      etiquetteProduit.lot = lot;

      const lotEtiquettesCollection: ILotEtiquettes[] = [{ id: 1087 }];
      vitest.spyOn(lotEtiquettesService, 'query').mockReturnValue(of(new HttpResponse({ body: lotEtiquettesCollection })));
      const additionalLotEtiquetteses = [lot];
      const expectedCollection: ILotEtiquettes[] = [...additionalLotEtiquetteses, ...lotEtiquettesCollection];
      vitest.spyOn(lotEtiquettesService, 'addLotEtiquettesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ etiquetteProduit });
      comp.ngOnInit();

      expect(lotEtiquettesService.query).toHaveBeenCalled();
      expect(lotEtiquettesService.addLotEtiquettesToCollectionIfMissing).toHaveBeenCalledWith(
        lotEtiquettesCollection,
        ...additionalLotEtiquetteses.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.lotEtiquettesesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const etiquetteProduit: IEtiquetteProduit = { id: 9016 };
      const produit: IProduit = { id: 28529 };
      etiquetteProduit.produit = produit;
      const lot: ILotEtiquettes = { id: 1087 };
      etiquetteProduit.lot = lot;

      activatedRoute.data = of({ etiquetteProduit });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.lotEtiquettesesSharedCollection()).toContainEqual(lot);
      expect(comp.etiquetteProduit).toEqual(etiquetteProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEtiquetteProduit>();
      const etiquetteProduit = { id: 25712 };
      vitest.spyOn(etiquetteProduitFormService, 'getEtiquetteProduit').mockReturnValue(etiquetteProduit);
      vitest.spyOn(etiquetteProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etiquetteProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(etiquetteProduit);
      saveSubject.complete();

      // THEN
      expect(etiquetteProduitFormService.getEtiquetteProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(etiquetteProduitService.update).toHaveBeenCalledWith(expect.objectContaining(etiquetteProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEtiquetteProduit>();
      const etiquetteProduit = { id: 25712 };
      vitest.spyOn(etiquetteProduitFormService, 'getEtiquetteProduit').mockReturnValue({ id: null });
      vitest.spyOn(etiquetteProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etiquetteProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(etiquetteProduit);
      saveSubject.complete();

      // THEN
      expect(etiquetteProduitFormService.getEtiquetteProduit).toHaveBeenCalled();
      expect(etiquetteProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEtiquetteProduit>();
      const etiquetteProduit = { id: 25712 };
      vitest.spyOn(etiquetteProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etiquetteProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(etiquetteProduitService.update).toHaveBeenCalled();
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

    describe('compareLotEtiquettes', () => {
      it('should forward to lotEtiquettesService', () => {
        const entity = { id: 1087 };
        const entity2 = { id: 17694 };
        vitest.spyOn(lotEtiquettesService, 'compareLotEtiquettes');
        comp.compareLotEtiquettes(entity, entity2);
        expect(lotEtiquettesService.compareLotEtiquettes).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
