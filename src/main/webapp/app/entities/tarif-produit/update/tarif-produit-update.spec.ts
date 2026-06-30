import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { TarifProduitService } from '../service/tarif-produit.service';
import { ITarifProduit } from '../tarif-produit.model';

import { TarifProduitFormService } from './tarif-produit-form.service';
import { TarifProduitUpdate } from './tarif-produit-update';

describe('TarifProduit Management Update Component', () => {
  let comp: TarifProduitUpdate;
  let fixture: ComponentFixture<TarifProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let tarifProduitFormService: TarifProduitFormService;
  let tarifProduitService: TarifProduitService;
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

    fixture = TestBed.createComponent(TarifProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tarifProduitFormService = TestBed.inject(TarifProduitFormService);
    tarifProduitService = TestBed.inject(TarifProduitService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const tarifProduit: ITarifProduit = { id: 9822 };
      const produit: IProduit = { id: 28529 };
      tarifProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tarifProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tarifProduit: ITarifProduit = { id: 9822 };
      const produit: IProduit = { id: 28529 };
      tarifProduit.produit = produit;

      activatedRoute.data = of({ tarifProduit });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.tarifProduit).toEqual(tarifProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITarifProduit>();
      const tarifProduit = { id: 16387 };
      vitest.spyOn(tarifProduitFormService, 'getTarifProduit').mockReturnValue(tarifProduit);
      vitest.spyOn(tarifProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tarifProduit);
      saveSubject.complete();

      // THEN
      expect(tarifProduitFormService.getTarifProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tarifProduitService.update).toHaveBeenCalledWith(expect.objectContaining(tarifProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITarifProduit>();
      const tarifProduit = { id: 16387 };
      vitest.spyOn(tarifProduitFormService, 'getTarifProduit').mockReturnValue({ id: null });
      vitest.spyOn(tarifProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tarifProduit);
      saveSubject.complete();

      // THEN
      expect(tarifProduitFormService.getTarifProduit).toHaveBeenCalled();
      expect(tarifProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITarifProduit>();
      const tarifProduit = { id: 16387 };
      vitest.spyOn(tarifProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tarifProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tarifProduitService.update).toHaveBeenCalled();
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
  });
});
