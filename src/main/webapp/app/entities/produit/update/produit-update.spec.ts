import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { UniteMesureService } from 'app/entities/unite-mesure/service/unite-mesure.service';
import { IUniteMesure } from 'app/entities/unite-mesure/unite-mesure.model';
import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

import { ProduitFormService } from './produit-form.service';
import { ProduitUpdate } from './produit-update';

describe('Produit Management Update Component', () => {
  let comp: ProduitUpdate;
  let fixture: ComponentFixture<ProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let produitFormService: ProduitFormService;
  let produitService: ProduitService;
  let boutiqueService: BoutiqueService;
  let groupeArticleService: GroupeArticleService;
  let uniteMesureService: UniteMesureService;

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

    fixture = TestBed.createComponent(ProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    produitFormService = TestBed.inject(ProduitFormService);
    produitService = TestBed.inject(ProduitService);
    boutiqueService = TestBed.inject(BoutiqueService);
    groupeArticleService = TestBed.inject(GroupeArticleService);
    uniteMesureService = TestBed.inject(UniteMesureService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const produit: IProduit = { id: 21239 };
      const boutique: IBoutique = { id: 5005 };
      produit.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call GroupeArticle query and add missing value', () => {
      const produit: IProduit = { id: 21239 };
      const groupeArticle: IGroupeArticle = { id: 2930 };
      produit.groupeArticle = groupeArticle;

      const groupeArticleCollection: IGroupeArticle[] = [{ id: 2930 }];
      vitest.spyOn(groupeArticleService, 'query').mockReturnValue(of(new HttpResponse({ body: groupeArticleCollection })));
      const additionalGroupeArticles = [groupeArticle];
      const expectedCollection: IGroupeArticle[] = [...additionalGroupeArticles, ...groupeArticleCollection];
      vitest.spyOn(groupeArticleService, 'addGroupeArticleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(groupeArticleService.query).toHaveBeenCalled();
      expect(groupeArticleService.addGroupeArticleToCollectionIfMissing).toHaveBeenCalledWith(
        groupeArticleCollection,
        ...additionalGroupeArticles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.groupeArticlesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call UniteMesure query and add missing value', () => {
      const produit: IProduit = { id: 21239 };
      const uniteMesure: IUniteMesure = { id: 4120 };
      produit.uniteMesure = uniteMesure;

      const uniteMesureCollection: IUniteMesure[] = [{ id: 4120 }];
      vitest.spyOn(uniteMesureService, 'query').mockReturnValue(of(new HttpResponse({ body: uniteMesureCollection })));
      const additionalUniteMesures = [uniteMesure];
      const expectedCollection: IUniteMesure[] = [...additionalUniteMesures, ...uniteMesureCollection];
      vitest.spyOn(uniteMesureService, 'addUniteMesureToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(uniteMesureService.query).toHaveBeenCalled();
      expect(uniteMesureService.addUniteMesureToCollectionIfMissing).toHaveBeenCalledWith(
        uniteMesureCollection,
        ...additionalUniteMesures.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.uniteMesuresSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const produit: IProduit = { id: 21239 };
      const boutique: IBoutique = { id: 5005 };
      produit.boutique = boutique;
      const groupeArticle: IGroupeArticle = { id: 2930 };
      produit.groupeArticle = groupeArticle;
      const uniteMesure: IUniteMesure = { id: 4120 };
      produit.uniteMesure = uniteMesure;

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.groupeArticlesSharedCollection()).toContainEqual(groupeArticle);
      expect(comp.uniteMesuresSharedCollection()).toContainEqual(uniteMesure);
      expect(comp.produit).toEqual(produit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitFormService, 'getProduit').mockReturnValue(produit);
      vitest.spyOn(produitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(produit);
      saveSubject.complete();

      // THEN
      expect(produitFormService.getProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(produitService.update).toHaveBeenCalledWith(expect.objectContaining(produit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitFormService, 'getProduit').mockReturnValue({ id: null });
      vitest.spyOn(produitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(produit);
      saveSubject.complete();

      // THEN
      expect(produitFormService.getProduit).toHaveBeenCalled();
      expect(produitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(produitService.update).toHaveBeenCalled();
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

    describe('compareGroupeArticle', () => {
      it('should forward to groupeArticleService', () => {
        const entity = { id: 2930 };
        const entity2 = { id: 28799 };
        vitest.spyOn(groupeArticleService, 'compareGroupeArticle');
        comp.compareGroupeArticle(entity, entity2);
        expect(groupeArticleService.compareGroupeArticle).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUniteMesure', () => {
      it('should forward to uniteMesureService', () => {
        const entity = { id: 4120 };
        const entity2 = { id: 22001 };
        vitest.spyOn(uniteMesureService, 'compareUniteMesure');
        comp.compareUniteMesure(entity, entity2);
        expect(uniteMesureService.compareUniteMesure).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
