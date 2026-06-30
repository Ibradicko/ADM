import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { IOperationCorrectiveVente } from '../operation-corrective-vente.model';
import { OperationCorrectiveVenteService } from '../service/operation-corrective-vente.service';

import { OperationCorrectiveVenteFormService } from './operation-corrective-vente-form.service';
import { OperationCorrectiveVenteUpdate } from './operation-corrective-vente-update';

describe('OperationCorrectiveVente Management Update Component', () => {
  let comp: OperationCorrectiveVenteUpdate;
  let fixture: ComponentFixture<OperationCorrectiveVenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let operationCorrectiveVenteFormService: OperationCorrectiveVenteFormService;
  let operationCorrectiveVenteService: OperationCorrectiveVenteService;
  let venteService: VenteService;
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

    fixture = TestBed.createComponent(OperationCorrectiveVenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    operationCorrectiveVenteFormService = TestBed.inject(OperationCorrectiveVenteFormService);
    operationCorrectiveVenteService = TestBed.inject(OperationCorrectiveVenteService);
    venteService = TestBed.inject(VenteService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Vente query and add missing value', () => {
      const operationCorrectiveVente: IOperationCorrectiveVente = { id: 24724 };
      const vente: IVente = { id: 25749 };
      operationCorrectiveVente.vente = vente;

      const venteCollection: IVente[] = [{ id: 25749 }];
      vitest.spyOn(venteService, 'query').mockReturnValue(of(new HttpResponse({ body: venteCollection })));
      const additionalVentes = [vente];
      const expectedCollection: IVente[] = [...additionalVentes, ...venteCollection];
      vitest.spyOn(venteService, 'addVenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ operationCorrectiveVente });
      comp.ngOnInit();

      expect(venteService.query).toHaveBeenCalled();
      expect(venteService.addVenteToCollectionIfMissing).toHaveBeenCalledWith(
        venteCollection,
        ...additionalVentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ventesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const operationCorrectiveVente: IOperationCorrectiveVente = { id: 24724 };
      const utilisateur: IUser = { id: 3944 };
      operationCorrectiveVente.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ operationCorrectiveVente });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const operationCorrectiveVente: IOperationCorrectiveVente = { id: 24724 };
      const vente: IVente = { id: 25749 };
      operationCorrectiveVente.vente = vente;
      const utilisateur: IUser = { id: 3944 };
      operationCorrectiveVente.utilisateur = utilisateur;

      activatedRoute.data = of({ operationCorrectiveVente });
      comp.ngOnInit();

      expect(comp.ventesSharedCollection()).toContainEqual(vente);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.operationCorrectiveVente).toEqual(operationCorrectiveVente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOperationCorrectiveVente>();
      const operationCorrectiveVente = { id: 8986 };
      vitest.spyOn(operationCorrectiveVenteFormService, 'getOperationCorrectiveVente').mockReturnValue(operationCorrectiveVente);
      vitest.spyOn(operationCorrectiveVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operationCorrectiveVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(operationCorrectiveVente);
      saveSubject.complete();

      // THEN
      expect(operationCorrectiveVenteFormService.getOperationCorrectiveVente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(operationCorrectiveVenteService.update).toHaveBeenCalledWith(expect.objectContaining(operationCorrectiveVente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOperationCorrectiveVente>();
      const operationCorrectiveVente = { id: 8986 };
      vitest.spyOn(operationCorrectiveVenteFormService, 'getOperationCorrectiveVente').mockReturnValue({ id: null });
      vitest.spyOn(operationCorrectiveVenteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operationCorrectiveVente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(operationCorrectiveVente);
      saveSubject.complete();

      // THEN
      expect(operationCorrectiveVenteFormService.getOperationCorrectiveVente).toHaveBeenCalled();
      expect(operationCorrectiveVenteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IOperationCorrectiveVente>();
      const operationCorrectiveVente = { id: 8986 };
      vitest.spyOn(operationCorrectiveVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ operationCorrectiveVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(operationCorrectiveVenteService.update).toHaveBeenCalled();
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
