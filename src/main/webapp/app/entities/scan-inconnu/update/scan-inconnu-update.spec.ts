import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IScanInconnu } from '../scan-inconnu.model';
import { ScanInconnuService } from '../service/scan-inconnu.service';

import { ScanInconnuFormService } from './scan-inconnu-form.service';
import { ScanInconnuUpdate } from './scan-inconnu-update';

describe('ScanInconnu Management Update Component', () => {
  let comp: ScanInconnuUpdate;
  let fixture: ComponentFixture<ScanInconnuUpdate>;
  let activatedRoute: ActivatedRoute;
  let scanInconnuFormService: ScanInconnuFormService;
  let scanInconnuService: ScanInconnuService;
  let boutiqueService: BoutiqueService;
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

    fixture = TestBed.createComponent(ScanInconnuUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scanInconnuFormService = TestBed.inject(ScanInconnuFormService);
    scanInconnuService = TestBed.inject(ScanInconnuService);
    boutiqueService = TestBed.inject(BoutiqueService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const scanInconnu: IScanInconnu = { id: 18351 };
      const boutique: IBoutique = { id: 5005 };
      scanInconnu.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scanInconnu });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const scanInconnu: IScanInconnu = { id: 18351 };
      const produitAffecte: IProduit = { id: 28529 };
      scanInconnu.produitAffecte = produitAffecte;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produitAffecte];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scanInconnu });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const scanInconnu: IScanInconnu = { id: 18351 };
      const boutique: IBoutique = { id: 5005 };
      scanInconnu.boutique = boutique;
      const produitAffecte: IProduit = { id: 28529 };
      scanInconnu.produitAffecte = produitAffecte;

      activatedRoute.data = of({ scanInconnu });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.produitsSharedCollection()).toContainEqual(produitAffecte);
      expect(comp.scanInconnu).toEqual(scanInconnu);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScanInconnu>();
      const scanInconnu = { id: 4379 };
      vitest.spyOn(scanInconnuFormService, 'getScanInconnu').mockReturnValue(scanInconnu);
      vitest.spyOn(scanInconnuService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scanInconnu });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scanInconnu);
      saveSubject.complete();

      // THEN
      expect(scanInconnuFormService.getScanInconnu).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(scanInconnuService.update).toHaveBeenCalledWith(expect.objectContaining(scanInconnu));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IScanInconnu>();
      const scanInconnu = { id: 4379 };
      vitest.spyOn(scanInconnuFormService, 'getScanInconnu').mockReturnValue({ id: null });
      vitest.spyOn(scanInconnuService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scanInconnu: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(scanInconnu);
      saveSubject.complete();

      // THEN
      expect(scanInconnuFormService.getScanInconnu).toHaveBeenCalled();
      expect(scanInconnuService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IScanInconnu>();
      const scanInconnu = { id: 4379 };
      vitest.spyOn(scanInconnuService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scanInconnu });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scanInconnuService.update).toHaveBeenCalled();
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
