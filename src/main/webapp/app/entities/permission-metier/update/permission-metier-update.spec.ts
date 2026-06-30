import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { IPermissionMetier } from '../permission-metier.model';
import { PermissionMetierService } from '../service/permission-metier.service';

import { PermissionMetierFormService } from './permission-metier-form.service';
import { PermissionMetierUpdate } from './permission-metier-update';

describe('PermissionMetier Management Update Component', () => {
  let comp: PermissionMetierUpdate;
  let fixture: ComponentFixture<PermissionMetierUpdate>;
  let activatedRoute: ActivatedRoute;
  let permissionMetierFormService: PermissionMetierFormService;
  let permissionMetierService: PermissionMetierService;
  let profilMetierService: ProfilMetierService;

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

    fixture = TestBed.createComponent(PermissionMetierUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    permissionMetierFormService = TestBed.inject(PermissionMetierFormService);
    permissionMetierService = TestBed.inject(PermissionMetierService);
    profilMetierService = TestBed.inject(ProfilMetierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ProfilMetier query and add missing value', () => {
      const permissionMetier: IPermissionMetier = { id: 30575 };
      const profilses: IProfilMetier[] = [{ id: 12096 }];
      permissionMetier.profilses = profilses;

      const profilMetierCollection: IProfilMetier[] = [{ id: 12096 }];
      vitest.spyOn(profilMetierService, 'query').mockReturnValue(of(new HttpResponse({ body: profilMetierCollection })));
      const additionalProfilMetiers = [...profilses];
      const expectedCollection: IProfilMetier[] = [...additionalProfilMetiers, ...profilMetierCollection];
      vitest.spyOn(profilMetierService, 'addProfilMetierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ permissionMetier });
      comp.ngOnInit();

      expect(profilMetierService.query).toHaveBeenCalled();
      expect(profilMetierService.addProfilMetierToCollectionIfMissing).toHaveBeenCalledWith(
        profilMetierCollection,
        ...additionalProfilMetiers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilMetiersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const permissionMetier: IPermissionMetier = { id: 30575 };
      const profils: IProfilMetier = { id: 12096 };
      permissionMetier.profilses = [profils];

      activatedRoute.data = of({ permissionMetier });
      comp.ngOnInit();

      expect(comp.profilMetiersSharedCollection()).toContainEqual(profils);
      expect(comp.permissionMetier).toEqual(permissionMetier);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPermissionMetier>();
      const permissionMetier = { id: 28695 };
      vitest.spyOn(permissionMetierFormService, 'getPermissionMetier').mockReturnValue(permissionMetier);
      vitest.spyOn(permissionMetierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionMetier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(permissionMetier);
      saveSubject.complete();

      // THEN
      expect(permissionMetierFormService.getPermissionMetier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(permissionMetierService.update).toHaveBeenCalledWith(expect.objectContaining(permissionMetier));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPermissionMetier>();
      const permissionMetier = { id: 28695 };
      vitest.spyOn(permissionMetierFormService, 'getPermissionMetier').mockReturnValue({ id: null });
      vitest.spyOn(permissionMetierService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionMetier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(permissionMetier);
      saveSubject.complete();

      // THEN
      expect(permissionMetierFormService.getPermissionMetier).toHaveBeenCalled();
      expect(permissionMetierService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPermissionMetier>();
      const permissionMetier = { id: 28695 };
      vitest.spyOn(permissionMetierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionMetier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(permissionMetierService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfilMetier', () => {
      it('should forward to profilMetierService', () => {
        const entity = { id: 12096 };
        const entity2 = { id: 25052 };
        vitest.spyOn(profilMetierService, 'compareProfilMetier');
        comp.compareProfilMetier(entity, entity2);
        expect(profilMetierService.compareProfilMetier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
