import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IReceptionProduit } from 'app/entities/reception-produit/reception-produit.model';
import { ReceptionProduitService } from 'app/entities/reception-produit/service/reception-produit.service';
import { ILigneReceptionProduit } from '../ligne-reception-produit.model';
import { LigneReceptionProduitService } from '../service/ligne-reception-produit.service';

import { LigneReceptionProduitFormService } from './ligne-reception-produit-form.service';
import { LigneReceptionProduitUpdate } from './ligne-reception-produit-update';

describe('LigneReceptionProduit Management Update Component', () => {
  let comp: LigneReceptionProduitUpdate;
  let fixture: ComponentFixture<LigneReceptionProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneReceptionProduitFormService: LigneReceptionProduitFormService;
  let ligneReceptionProduitService: LigneReceptionProduitService;
  let receptionProduitService: ReceptionProduitService;
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

    fixture = TestBed.createComponent(LigneReceptionProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneReceptionProduitFormService = TestBed.inject(LigneReceptionProduitFormService);
    ligneReceptionProduitService = TestBed.inject(LigneReceptionProduitService);
    receptionProduitService = TestBed.inject(ReceptionProduitService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ReceptionProduit query and add missing value', () => {
      const ligneReceptionProduit: ILigneReceptionProduit = { id: 14106 };
      const reception: IReceptionProduit = { id: 19661 };
      ligneReceptionProduit.reception = reception;

      const receptionProduitCollection: IReceptionProduit[] = [{ id: 19661 }];
      vitest.spyOn(receptionProduitService, 'query').mockReturnValue(of(new HttpResponse({ body: receptionProduitCollection })));
      const additionalReceptionProduits = [reception];
      const expectedCollection: IReceptionProduit[] = [...additionalReceptionProduits, ...receptionProduitCollection];
      vitest.spyOn(receptionProduitService, 'addReceptionProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneReceptionProduit });
      comp.ngOnInit();

      expect(receptionProduitService.query).toHaveBeenCalled();
      expect(receptionProduitService.addReceptionProduitToCollectionIfMissing).toHaveBeenCalledWith(
        receptionProduitCollection,
        ...additionalReceptionProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.receptionProduitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const ligneReceptionProduit: ILigneReceptionProduit = { id: 14106 };
      const produit: IProduit = { id: 28529 };
      ligneReceptionProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneReceptionProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneReceptionProduit: ILigneReceptionProduit = { id: 14106 };
      const reception: IReceptionProduit = { id: 19661 };
      ligneReceptionProduit.reception = reception;
      const produit: IProduit = { id: 28529 };
      ligneReceptionProduit.produit = produit;

      activatedRoute.data = of({ ligneReceptionProduit });
      comp.ngOnInit();

      expect(comp.receptionProduitsSharedCollection()).toContainEqual(reception);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.ligneReceptionProduit).toEqual(ligneReceptionProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneReceptionProduit>();
      const ligneReceptionProduit = { id: 9739 };
      vitest.spyOn(ligneReceptionProduitFormService, 'getLigneReceptionProduit').mockReturnValue(ligneReceptionProduit);
      vitest.spyOn(ligneReceptionProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneReceptionProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneReceptionProduit);
      saveSubject.complete();

      // THEN
      expect(ligneReceptionProduitFormService.getLigneReceptionProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneReceptionProduitService.update).toHaveBeenCalledWith(expect.objectContaining(ligneReceptionProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneReceptionProduit>();
      const ligneReceptionProduit = { id: 9739 };
      vitest.spyOn(ligneReceptionProduitFormService, 'getLigneReceptionProduit').mockReturnValue({ id: null });
      vitest.spyOn(ligneReceptionProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneReceptionProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneReceptionProduit);
      saveSubject.complete();

      // THEN
      expect(ligneReceptionProduitFormService.getLigneReceptionProduit).toHaveBeenCalled();
      expect(ligneReceptionProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneReceptionProduit>();
      const ligneReceptionProduit = { id: 9739 };
      vitest.spyOn(ligneReceptionProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneReceptionProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneReceptionProduitService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareReceptionProduit', () => {
      it('should forward to receptionProduitService', () => {
        const entity = { id: 19661 };
        const entity2 = { id: 1742 };
        vitest.spyOn(receptionProduitService, 'compareReceptionProduit');
        comp.compareReceptionProduit(entity, entity2);
        expect(receptionProduitService.compareReceptionProduit).toHaveBeenCalledWith(entity, entity2);
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
