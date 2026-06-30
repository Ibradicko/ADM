import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IInventaireStock } from '../inventaire-stock.model';
import { InventaireStockService } from '../service/inventaire-stock.service';

import { InventaireStockFormService } from './inventaire-stock-form.service';
import { InventaireStockUpdate } from './inventaire-stock-update';

describe('InventaireStock Management Update Component', () => {
  let comp: InventaireStockUpdate;
  let fixture: ComponentFixture<InventaireStockUpdate>;
  let activatedRoute: ActivatedRoute;
  let inventaireStockFormService: InventaireStockFormService;
  let inventaireStockService: InventaireStockService;
  let boutiqueService: BoutiqueService;
  let depotStockService: DepotStockService;
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

    fixture = TestBed.createComponent(InventaireStockUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inventaireStockFormService = TestBed.inject(InventaireStockFormService);
    inventaireStockService = TestBed.inject(InventaireStockService);
    boutiqueService = TestBed.inject(BoutiqueService);
    depotStockService = TestBed.inject(DepotStockService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const inventaireStock: IInventaireStock = { id: 105 };
      const boutique: IBoutique = { id: 5005 };
      inventaireStock.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call DepotStock query and add missing value', () => {
      const inventaireStock: IInventaireStock = { id: 105 };
      const depot: IDepotStock = { id: 26721 };
      inventaireStock.depot = depot;

      const depotStockCollection: IDepotStock[] = [{ id: 26721 }];
      vitest.spyOn(depotStockService, 'query').mockReturnValue(of(new HttpResponse({ body: depotStockCollection })));
      const additionalDepotStocks = [depot];
      const expectedCollection: IDepotStock[] = [...additionalDepotStocks, ...depotStockCollection];
      vitest.spyOn(depotStockService, 'addDepotStockToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      expect(depotStockService.query).toHaveBeenCalled();
      expect(depotStockService.addDepotStockToCollectionIfMissing).toHaveBeenCalledWith(
        depotStockCollection,
        ...additionalDepotStocks.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.depotStocksSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const inventaireStock: IInventaireStock = { id: 105 };
      const utilisateur: IUser = { id: 3944 };
      inventaireStock.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const inventaireStock: IInventaireStock = { id: 105 };
      const boutique: IBoutique = { id: 5005 };
      inventaireStock.boutique = boutique;
      const depot: IDepotStock = { id: 26721 };
      inventaireStock.depot = depot;
      const utilisateur: IUser = { id: 3944 };
      inventaireStock.utilisateur = utilisateur;

      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.depotStocksSharedCollection()).toContainEqual(depot);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.inventaireStock).toEqual(inventaireStock);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInventaireStock>();
      const inventaireStock = { id: 31192 };
      vitest.spyOn(inventaireStockFormService, 'getInventaireStock').mockReturnValue(inventaireStock);
      vitest.spyOn(inventaireStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(inventaireStock);
      saveSubject.complete();

      // THEN
      expect(inventaireStockFormService.getInventaireStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inventaireStockService.update).toHaveBeenCalledWith(expect.objectContaining(inventaireStock));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IInventaireStock>();
      const inventaireStock = { id: 31192 };
      vitest.spyOn(inventaireStockFormService, 'getInventaireStock').mockReturnValue({ id: null });
      vitest.spyOn(inventaireStockService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventaireStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(inventaireStock);
      saveSubject.complete();

      // THEN
      expect(inventaireStockFormService.getInventaireStock).toHaveBeenCalled();
      expect(inventaireStockService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IInventaireStock>();
      const inventaireStock = { id: 31192 };
      vitest.spyOn(inventaireStockService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventaireStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inventaireStockService.update).toHaveBeenCalled();
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

    describe('compareDepotStock', () => {
      it('should forward to depotStockService', () => {
        const entity = { id: 26721 };
        const entity2 = { id: 25113 };
        vitest.spyOn(depotStockService, 'compareDepotStock');
        comp.compareDepotStock(entity, entity2);
        expect(depotStockService.compareDepotStock).toHaveBeenCalledWith(entity, entity2);
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
