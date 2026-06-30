import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPermissionMetier } from 'app/entities/permission-metier/permission-metier.model';
import { PermissionMetierService } from 'app/entities/permission-metier/service/permission-metier.service';
import { IProfilMetier } from '../profil-metier.model';
import { ProfilMetierService } from '../service/profil-metier.service';

import { ProfilMetierFormService } from './profil-metier-form.service';
import { ProfilMetierUpdate } from './profil-metier-update';

describe('ProfilMetier Management Update Component', () => {
  let comp: ProfilMetierUpdate;
  let fixture: ComponentFixture<ProfilMetierUpdate>;
  let activatedRoute: ActivatedRoute;
  let profilMetierFormService: ProfilMetierFormService;
  let profilMetierService: ProfilMetierService;
  let permissionMetierService: PermissionMetierService;

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

    fixture = TestBed.createComponent(ProfilMetierUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profilMetierFormService = TestBed.inject(ProfilMetierFormService);
    profilMetierService = TestBed.inject(ProfilMetierService);
    permissionMetierService = TestBed.inject(PermissionMetierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PermissionMetier query and add missing value', () => {
      const profilMetier: IProfilMetier = { id: 25052 };
      const permissionses: IPermissionMetier[] = [{ id: 28695 }];
      profilMetier.permissionses = permissionses;

      const permissionMetierCollection: IPermissionMetier[] = [{ id: 28695 }];
      vitest.spyOn(permissionMetierService, 'query').mockReturnValue(of(new HttpResponse({ body: permissionMetierCollection })));
      const additionalPermissionMetiers = [...permissionses];
      const expectedCollection: IPermissionMetier[] = [...additionalPermissionMetiers, ...permissionMetierCollection];
      vitest.spyOn(permissionMetierService, 'addPermissionMetierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profilMetier });
      comp.ngOnInit();

      expect(permissionMetierService.query).toHaveBeenCalled();
      expect(permissionMetierService.addPermissionMetierToCollectionIfMissing).toHaveBeenCalledWith(
        permissionMetierCollection,
        ...additionalPermissionMetiers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.permissionMetiersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const profilMetier: IProfilMetier = { id: 25052 };
      const permissions: IPermissionMetier = { id: 28695 };
      profilMetier.permissionses = [permissions];

      activatedRoute.data = of({ profilMetier });
      comp.ngOnInit();

      expect(comp.permissionMetiersSharedCollection()).toContainEqual(permissions);
      expect(comp.profilMetier).toEqual(profilMetier);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfilMetier>();
      const profilMetier = { id: 12096 };
      vitest.spyOn(profilMetierFormService, 'getProfilMetier').mockReturnValue(profilMetier);
      vitest.spyOn(profilMetierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profilMetier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profilMetier);
      saveSubject.complete();

      // THEN
      expect(profilMetierFormService.getProfilMetier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profilMetierService.update).toHaveBeenCalledWith(expect.objectContaining(profilMetier));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfilMetier>();
      const profilMetier = { id: 12096 };
      vitest.spyOn(profilMetierFormService, 'getProfilMetier').mockReturnValue({ id: null });
      vitest.spyOn(profilMetierService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profilMetier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profilMetier);
      saveSubject.complete();

      // THEN
      expect(profilMetierFormService.getProfilMetier).toHaveBeenCalled();
      expect(profilMetierService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfilMetier>();
      const profilMetier = { id: 12096 };
      vitest.spyOn(profilMetierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profilMetier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profilMetierService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePermissionMetier', () => {
      it('should forward to permissionMetierService', () => {
        const entity = { id: 28695 };
        const entity2 = { id: 30575 };
        vitest.spyOn(permissionMetierService, 'comparePermissionMetier');
        comp.comparePermissionMetier(entity, entity2);
        expect(permissionMetierService.comparePermissionMetier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
