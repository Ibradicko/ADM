import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IHistoriqueCodeBarres } from '../historique-code-barres.model';
import { HistoriqueCodeBarresService } from '../service/historique-code-barres.service';

import { HistoriqueCodeBarresFormService } from './historique-code-barres-form.service';
import { HistoriqueCodeBarresUpdate } from './historique-code-barres-update';

describe('HistoriqueCodeBarres Management Update Component', () => {
  let comp: HistoriqueCodeBarresUpdate;
  let fixture: ComponentFixture<HistoriqueCodeBarresUpdate>;
  let activatedRoute: ActivatedRoute;
  let historiqueCodeBarresFormService: HistoriqueCodeBarresFormService;
  let historiqueCodeBarresService: HistoriqueCodeBarresService;
  let produitService: ProduitService;
  let userService: UserService;

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

    fixture = TestBed.createComponent(HistoriqueCodeBarresUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    historiqueCodeBarresFormService = TestBed.inject(HistoriqueCodeBarresFormService);
    historiqueCodeBarresService = TestBed.inject(HistoriqueCodeBarresService);
    produitService = TestBed.inject(ProduitService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const historiqueCodeBarres: IHistoriqueCodeBarres = { id: 26931 };
      const produit: IProduit = { id: 28529 };
      historiqueCodeBarres.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ historiqueCodeBarres });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const historiqueCodeBarres: IHistoriqueCodeBarres = { id: 26931 };
      const utilisateur: IUser = { id: 3944 };
      historiqueCodeBarres.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ historiqueCodeBarres });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const historiqueCodeBarres: IHistoriqueCodeBarres = { id: 26931 };
      const produit: IProduit = { id: 28529 };
      historiqueCodeBarres.produit = produit;
      const utilisateur: IUser = { id: 3944 };
      historiqueCodeBarres.utilisateur = utilisateur;

      activatedRoute.data = of({ historiqueCodeBarres });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.historiqueCodeBarres).toEqual(historiqueCodeBarres);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueCodeBarres>();
      const historiqueCodeBarres = { id: 29449 };
      vitest.spyOn(historiqueCodeBarresFormService, 'getHistoriqueCodeBarres').mockReturnValue(historiqueCodeBarres);
      vitest.spyOn(historiqueCodeBarresService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueCodeBarres });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(historiqueCodeBarres);
      saveSubject.complete();

      // THEN
      expect(historiqueCodeBarresFormService.getHistoriqueCodeBarres).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(historiqueCodeBarresService.update).toHaveBeenCalledWith(expect.objectContaining(historiqueCodeBarres));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueCodeBarres>();
      const historiqueCodeBarres = { id: 29449 };
      vitest.spyOn(historiqueCodeBarresFormService, 'getHistoriqueCodeBarres').mockReturnValue({ id: null });
      vitest.spyOn(historiqueCodeBarresService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueCodeBarres: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(historiqueCodeBarres);
      saveSubject.complete();

      // THEN
      expect(historiqueCodeBarresFormService.getHistoriqueCodeBarres).toHaveBeenCalled();
      expect(historiqueCodeBarresService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IHistoriqueCodeBarres>();
      const historiqueCodeBarres = { id: 29449 };
      vitest.spyOn(historiqueCodeBarresService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ historiqueCodeBarres });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(historiqueCodeBarresService.update).toHaveBeenCalled();
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

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
