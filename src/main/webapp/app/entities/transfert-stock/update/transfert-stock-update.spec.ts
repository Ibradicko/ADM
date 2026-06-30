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
import { TransfertStockService } from '../service/transfert-stock.service';
import { ITransfertStock } from '../transfert-stock.model';

import { TransfertStockFormService } from './transfert-stock-form.service';
import { TransfertStockUpdate } from './transfert-stock-update';

describe('TransfertStock Management Update Component', () => {
  let comp: TransfertStockUpdate;
  let fixture: ComponentFixture<TransfertStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let transfertStockFormService: TransfertStockFormService;
  let transfertStockService: TransfertStockService;
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

    fixture = TestBed.createComponent(TransfertStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transfertStockFormService = TestBed.inject(TransfertStockFormService);
    transfertStockService = TestBed.inject(TransfertStockService);
    boutiqueService = TestBed.inject(BoutiqueService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const transfertStock: ITransfertStock = { id: 6468 };
      const boutiqueOrigine: IBoutique = { id: 5005 };
      transfertStock.boutiqueOrigine = boutiqueOrigine;
      const boutiqueDestination: IBoutique = { id: 5005 };
      transfertStock.boutiqueDestination = boutiqueDestination;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutiqueOrigine, boutiqueDestination];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transfertStock });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const transfertStock: ITransfertStock = { id: 6468 };
      const utilisateur: IUser = { id: 3944 };
      transfertStock.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transfertStock });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const transfertStock: ITransfertStock = { id: 6468 };
      const boutiqueOrigine: IBoutique = { id: 5005 };
      transfertStock.boutiqueOrigine = boutiqueOrigine;
      const boutiqueDestination: IBoutique = { id: 5005 };
      transfertStock.boutiqueDestination = boutiqueDestination;
      const utilisateur: IUser = { id: 3944 };
      transfertStock.utilisateur = utilisateur;

      activatedRoute.data = of({ transfertStock });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutiqueOrigine);
      expect(comp.boutiquesSharedCollection()).toContainEqual(boutiqueDestination);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.transfertStock).toEqual(transfertStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITransfertStock>();
      const transfertStock = { id: 31780 };
      vitest.spyOn(transfertStockFormService, 'getTransfertStock').mockReturnValue(transfertStock);
      vitest.spyOn(transfertStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transfertStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(transfertStock);
      saveSubject.complete();

      // THEN
      expect(transfertStockFormService.getTransfertStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transfertStockService.update).toHaveBeenCalledWith(expect.objectContaining(transfertStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITransfertStock>();
      const transfertStock = { id: 31780 };
      vitest.spyOn(transfertStockFormService, 'getTransfertStock').mockReturnValue({ id: null });
      vitest.spyOn(transfertStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transfertStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(transfertStock);
      saveSubject.complete();

      // THEN
      expect(transfertStockFormService.getTransfertStock).toHaveBeenCalled();
      expect(transfertStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITransfertStock>();
      const transfertStock = { id: 31780 };
      vitest.spyOn(transfertStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transfertStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transfertStockService.update).toHaveBeenCalled();
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
