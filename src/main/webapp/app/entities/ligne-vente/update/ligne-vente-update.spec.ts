import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { ILigneVente } from '../ligne-vente.model';
import { LigneVenteService } from '../service/ligne-vente.service';

import { LigneVenteFormService } from './ligne-vente-form.service';
import { LigneVenteUpdate } from './ligne-vente-update';

describe('LigneVente Management Update Component', () => {
  let comp: LigneVenteUpdate;
  let fixture: ComponentFixture<LigneVenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneVenteFormService: LigneVenteFormService;
  let ligneVenteService: LigneVenteService;
  let venteService: VenteService;
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

    fixture = TestBed.createComponent(LigneVenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneVenteFormService = TestBed.inject(LigneVenteFormService);
    ligneVenteService = TestBed.inject(LigneVenteService);
    venteService = TestBed.inject(VenteService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Vente query and add missing value', () => {
      const ligneVente: ILigneVente = { id: 17072 };
      const vente: IVente = { id: 25749 };
      ligneVente.vente = vente;

      const venteCollection: IVente[] = [{ id: 25749 }];
      vitest.spyOn(venteService, 'query').mockReturnValue(of(new HttpResponse({ body: venteCollection })));
      const additionalVentes = [vente];
      const expectedCollection: IVente[] = [...additionalVentes, ...venteCollection];
      vitest.spyOn(venteService, 'addVenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneVente });
      comp.ngOnInit();

      expect(venteService.query).toHaveBeenCalled();
      expect(venteService.addVenteToCollectionIfMissing).toHaveBeenCalledWith(
        venteCollection,
        ...additionalVentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ventesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const ligneVente: ILigneVente = { id: 17072 };
      const produit: IProduit = { id: 28529 };
      ligneVente.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneVente });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneVente: ILigneVente = { id: 17072 };
      const vente: IVente = { id: 25749 };
      ligneVente.vente = vente;
      const produit: IProduit = { id: 28529 };
      ligneVente.produit = produit;

      activatedRoute.data = of({ ligneVente });
      comp.ngOnInit();

      expect(comp.ventesSharedCollection()).toContainEqual(vente);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.ligneVente).toEqual(ligneVente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneVente>();
      const ligneVente = { id: 103 };
      vitest.spyOn(ligneVenteFormService, 'getLigneVente').mockReturnValue(ligneVente);
      vitest.spyOn(ligneVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneVente);
      saveSubject.complete();

      // THEN
      expect(ligneVenteFormService.getLigneVente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneVenteService.update).toHaveBeenCalledWith(expect.objectContaining(ligneVente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneVente>();
      const ligneVente = { id: 103 };
      vitest.spyOn(ligneVenteFormService, 'getLigneVente').mockReturnValue({ id: null });
      vitest.spyOn(ligneVenteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneVente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneVente);
      saveSubject.complete();

      // THEN
      expect(ligneVenteFormService.getLigneVente).toHaveBeenCalled();
      expect(ligneVenteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneVente>();
      const ligneVente = { id: 103 };
      vitest.spyOn(ligneVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneVenteService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVente', () => {
      it('should forward to venteService', () => {
        const entity = { id: 25749 };
        const entity2 = { id: 9754 };
        vitest.spyOn(venteService, 'compareVente');
        comp.compareVente(entity, entity2);
        expect(venteService.compareVente).toHaveBeenCalledWith(entity, entity2);
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
