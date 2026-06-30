import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { ICodeBarresProduit } from '../code-barres-produit.model';
import { CodeBarresProduitService } from '../service/code-barres-produit.service';

import { CodeBarresProduitFormService } from './code-barres-produit-form.service';
import { CodeBarresProduitUpdate } from './code-barres-produit-update';

describe('CodeBarresProduit Management Update Component', () => {
  let comp: CodeBarresProduitUpdate;
  let fixture: ComponentFixture<CodeBarresProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let codeBarresProduitFormService: CodeBarresProduitFormService;
  let codeBarresProduitService: CodeBarresProduitService;
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

    fixture = TestBed.createComponent(CodeBarresProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    codeBarresProduitFormService = TestBed.inject(CodeBarresProduitFormService);
    codeBarresProduitService = TestBed.inject(CodeBarresProduitService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const codeBarresProduit: ICodeBarresProduit = { id: 22765 };
      const produit: IProduit = { id: 28529 };
      codeBarresProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ codeBarresProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const codeBarresProduit: ICodeBarresProduit = { id: 22765 };
      const produit: IProduit = { id: 28529 };
      codeBarresProduit.produit = produit;

      activatedRoute.data = of({ codeBarresProduit });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.codeBarresProduit).toEqual(codeBarresProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICodeBarresProduit>();
      const codeBarresProduit = { id: 14062 };
      vitest.spyOn(codeBarresProduitFormService, 'getCodeBarresProduit').mockReturnValue(codeBarresProduit);
      vitest.spyOn(codeBarresProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ codeBarresProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(codeBarresProduit);
      saveSubject.complete();

      // THEN
      expect(codeBarresProduitFormService.getCodeBarresProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(codeBarresProduitService.update).toHaveBeenCalledWith(expect.objectContaining(codeBarresProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICodeBarresProduit>();
      const codeBarresProduit = { id: 14062 };
      vitest.spyOn(codeBarresProduitFormService, 'getCodeBarresProduit').mockReturnValue({ id: null });
      vitest.spyOn(codeBarresProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ codeBarresProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(codeBarresProduit);
      saveSubject.complete();

      // THEN
      expect(codeBarresProduitFormService.getCodeBarresProduit).toHaveBeenCalled();
      expect(codeBarresProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICodeBarresProduit>();
      const codeBarresProduit = { id: 14062 };
      vitest.spyOn(codeBarresProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ codeBarresProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(codeBarresProduitService.update).toHaveBeenCalled();
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
