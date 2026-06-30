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
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IRegleRedevance } from '../regle-redevance.model';
import { RegleRedevanceService } from '../service/regle-redevance.service';

import { RegleRedevanceFormService } from './regle-redevance-form.service';
import { RegleRedevanceUpdate } from './regle-redevance-update';

describe('RegleRedevance Management Update Component', () => {
  let comp: RegleRedevanceUpdate;
  let fixture: ComponentFixture<RegleRedevanceUpdate>;
  let activatedRoute: ActivatedRoute;
  let regleRedevanceFormService: RegleRedevanceFormService;
  let regleRedevanceService: RegleRedevanceService;
  let boutiqueService: BoutiqueService;
  let locataireService: LocataireService;
  let groupeArticleService: GroupeArticleService;
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

    fixture = TestBed.createComponent(RegleRedevanceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    regleRedevanceFormService = TestBed.inject(RegleRedevanceFormService);
    regleRedevanceService = TestBed.inject(RegleRedevanceService);
    boutiqueService = TestBed.inject(BoutiqueService);
    locataireService = TestBed.inject(LocataireService);
    groupeArticleService = TestBed.inject(GroupeArticleService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const regleRedevance: IRegleRedevance = { id: 28916 };
      const boutique: IBoutique = { id: 5005 };
      regleRedevance.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Locataire query and add missing value', () => {
      const regleRedevance: IRegleRedevance = { id: 28916 };
      const locataire: ILocataire = { id: 3768 };
      regleRedevance.locataire = locataire;

      const locataireCollection: ILocataire[] = [{ id: 3768 }];
      vitest.spyOn(locataireService, 'query').mockReturnValue(of(new HttpResponse({ body: locataireCollection })));
      const additionalLocataires = [locataire];
      const expectedCollection: ILocataire[] = [...additionalLocataires, ...locataireCollection];
      vitest.spyOn(locataireService, 'addLocataireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      expect(locataireService.query).toHaveBeenCalled();
      expect(locataireService.addLocataireToCollectionIfMissing).toHaveBeenCalledWith(
        locataireCollection,
        ...additionalLocataires.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.locatairesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call GroupeArticle query and add missing value', () => {
      const regleRedevance: IRegleRedevance = { id: 28916 };
      const groupeArticle: IGroupeArticle = { id: 2930 };
      regleRedevance.groupeArticle = groupeArticle;

      const groupeArticleCollection: IGroupeArticle[] = [{ id: 2930 }];
      vitest.spyOn(groupeArticleService, 'query').mockReturnValue(of(new HttpResponse({ body: groupeArticleCollection })));
      const additionalGroupeArticles = [groupeArticle];
      const expectedCollection: IGroupeArticle[] = [...additionalGroupeArticles, ...groupeArticleCollection];
      vitest.spyOn(groupeArticleService, 'addGroupeArticleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      expect(groupeArticleService.query).toHaveBeenCalled();
      expect(groupeArticleService.addGroupeArticleToCollectionIfMissing).toHaveBeenCalledWith(
        groupeArticleCollection,
        ...additionalGroupeArticles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.groupeArticlesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const regleRedevance: IRegleRedevance = { id: 28916 };
      const produit: IProduit = { id: 28529 };
      regleRedevance.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const regleRedevance: IRegleRedevance = { id: 28916 };
      const boutique: IBoutique = { id: 5005 };
      regleRedevance.boutique = boutique;
      const locataire: ILocataire = { id: 3768 };
      regleRedevance.locataire = locataire;
      const groupeArticle: IGroupeArticle = { id: 2930 };
      regleRedevance.groupeArticle = groupeArticle;
      const produit: IProduit = { id: 28529 };
      regleRedevance.produit = produit;

      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.locatairesSharedCollection()).toContainEqual(locataire);
      expect(comp.groupeArticlesSharedCollection()).toContainEqual(groupeArticle);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.regleRedevance).toEqual(regleRedevance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRegleRedevance>();
      const regleRedevance = { id: 5880 };
      vitest.spyOn(regleRedevanceFormService, 'getRegleRedevance').mockReturnValue(regleRedevance);
      vitest.spyOn(regleRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(regleRedevance);
      saveSubject.complete();

      // THEN
      expect(regleRedevanceFormService.getRegleRedevance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(regleRedevanceService.update).toHaveBeenCalledWith(expect.objectContaining(regleRedevance));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRegleRedevance>();
      const regleRedevance = { id: 5880 };
      vitest.spyOn(regleRedevanceFormService, 'getRegleRedevance').mockReturnValue({ id: null });
      vitest.spyOn(regleRedevanceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regleRedevance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(regleRedevance);
      saveSubject.complete();

      // THEN
      expect(regleRedevanceFormService.getRegleRedevance).toHaveBeenCalled();
      expect(regleRedevanceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRegleRedevance>();
      const regleRedevance = { id: 5880 };
      vitest.spyOn(regleRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ regleRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(regleRedevanceService.update).toHaveBeenCalled();
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

    describe('compareLocataire', () => {
      it('should forward to locataireService', () => {
        const entity = { id: 3768 };
        const entity2 = { id: 24112 };
        vitest.spyOn(locataireService, 'compareLocataire');
        comp.compareLocataire(entity, entity2);
        expect(locataireService.compareLocataire).toHaveBeenCalledWith(entity, entity2);
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
