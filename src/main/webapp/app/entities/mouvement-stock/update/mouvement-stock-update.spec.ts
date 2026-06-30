import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IMouvementStock } from '../mouvement-stock.model';
import { MouvementStockService } from '../service/mouvement-stock.service';

import { MouvementStockFormService } from './mouvement-stock-form.service';
import { MouvementStockUpdate } from './mouvement-stock-update';

describe('MouvementStock Management Update Component', () => {
  let comp: MouvementStockUpdate;
  let fixture: ComponentFixture<MouvementStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let mouvementStockFormService: MouvementStockFormService;
  let mouvementStockService: MouvementStockService;
  let boutiqueService: BoutiqueService;
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

    fixture = TestBed.createComponent(MouvementStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mouvementStockFormService = TestBed.inject(MouvementStockFormService);
    mouvementStockService = TestBed.inject(MouvementStockService);
    boutiqueService = TestBed.inject(BoutiqueService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const mouvementStock: IMouvementStock = { id: 26007 };
      const boutique: IBoutique = { id: 5005 };
      mouvementStock.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mouvementStock });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const mouvementStock: IMouvementStock = { id: 26007 };
      const utilisateur: IUser = { id: 3944 };
      mouvementStock.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mouvementStock });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const mouvementStock: IMouvementStock = { id: 26007 };
      const boutique: IBoutique = { id: 5005 };
      mouvementStock.boutique = boutique;
      const utilisateur: IUser = { id: 3944 };
      mouvementStock.utilisateur = utilisateur;

      activatedRoute.data = of({ mouvementStock });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.mouvementStock).toEqual(mouvementStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMouvementStock>();
      const mouvementStock = { id: 32109 };
      vitest.spyOn(mouvementStockFormService, 'getMouvementStock').mockReturnValue(mouvementStock);
      vitest.spyOn(mouvementStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mouvementStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(mouvementStock);
      saveSubject.complete();

      // THEN
      expect(mouvementStockFormService.getMouvementStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(mouvementStockService.update).toHaveBeenCalledWith(expect.objectContaining(mouvementStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMouvementStock>();
      const mouvementStock = { id: 32109 };
      vitest.spyOn(mouvementStockFormService, 'getMouvementStock').mockReturnValue({ id: null });
      vitest.spyOn(mouvementStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mouvementStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(mouvementStock);
      saveSubject.complete();

      // THEN
      expect(mouvementStockFormService.getMouvementStock).toHaveBeenCalled();
      expect(mouvementStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMouvementStock>();
      const mouvementStock = { id: 32109 };
      vitest.spyOn(mouvementStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mouvementStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mouvementStockService.update).toHaveBeenCalled();
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
