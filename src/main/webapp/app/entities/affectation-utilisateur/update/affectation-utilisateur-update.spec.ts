import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IAffectationUtilisateur } from '../affectation-utilisateur.model';
import { AffectationUtilisateurService } from '../service/affectation-utilisateur.service';

import { AffectationUtilisateurFormService } from './affectation-utilisateur-form.service';
import { AffectationUtilisateurUpdate } from './affectation-utilisateur-update';

describe('AffectationUtilisateur Management Update Component', () => {
  let comp: AffectationUtilisateurUpdate;
  let fixture: ComponentFixture<AffectationUtilisateurUpdate>;
  let activatedRoute: ActivatedRoute;
  let affectationUtilisateurFormService: AffectationUtilisateurFormService;
  let affectationUtilisateurService: AffectationUtilisateurService;
  let userService: UserService;
  let boutiqueService: BoutiqueService;
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

    fixture = TestBed.createComponent(AffectationUtilisateurUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    affectationUtilisateurFormService = TestBed.inject(AffectationUtilisateurFormService);
    affectationUtilisateurService = TestBed.inject(AffectationUtilisateurService);
    userService = TestBed.inject(UserService);
    boutiqueService = TestBed.inject(BoutiqueService);
    profilMetierService = TestBed.inject(ProfilMetierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const affectationUtilisateur: IAffectationUtilisateur = { id: 21243 };
      const user: IUser = { id: 3944 };
      affectationUtilisateur.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Boutique query and add missing value', () => {
      const affectationUtilisateur: IAffectationUtilisateur = { id: 21243 };
      const boutique: IBoutique = { id: 5005 };
      affectationUtilisateur.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call ProfilMetier query and add missing value', () => {
      const affectationUtilisateur: IAffectationUtilisateur = { id: 21243 };
      const profil: IProfilMetier = { id: 12096 };
      affectationUtilisateur.profil = profil;

      const profilMetierCollection: IProfilMetier[] = [{ id: 12096 }];
      vitest.spyOn(profilMetierService, 'query').mockReturnValue(of(new HttpResponse({ body: profilMetierCollection })));
      const additionalProfilMetiers = [profil];
      const expectedCollection: IProfilMetier[] = [...additionalProfilMetiers, ...profilMetierCollection];
      vitest.spyOn(profilMetierService, 'addProfilMetierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      expect(profilMetierService.query).toHaveBeenCalled();
      expect(profilMetierService.addProfilMetierToCollectionIfMissing).toHaveBeenCalledWith(
        profilMetierCollection,
        ...additionalProfilMetiers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilMetiersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const affectationUtilisateur: IAffectationUtilisateur = { id: 21243 };
      const user: IUser = { id: 3944 };
      affectationUtilisateur.user = user;
      const boutique: IBoutique = { id: 5005 };
      affectationUtilisateur.boutique = boutique;
      const profil: IProfilMetier = { id: 12096 };
      affectationUtilisateur.profil = profil;

      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.profilMetiersSharedCollection()).toContainEqual(profil);
      expect(comp.affectationUtilisateur).toEqual(affectationUtilisateur);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAffectationUtilisateur>();
      const affectationUtilisateur = { id: 4001 };
      vitest.spyOn(affectationUtilisateurFormService, 'getAffectationUtilisateur').mockReturnValue(affectationUtilisateur);
      vitest.spyOn(affectationUtilisateurService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(affectationUtilisateur);
      saveSubject.complete();

      // THEN
      expect(affectationUtilisateurFormService.getAffectationUtilisateur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(affectationUtilisateurService.update).toHaveBeenCalledWith(expect.objectContaining(affectationUtilisateur));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAffectationUtilisateur>();
      const affectationUtilisateur = { id: 4001 };
      vitest.spyOn(affectationUtilisateurFormService, 'getAffectationUtilisateur').mockReturnValue({ id: null });
      vitest.spyOn(affectationUtilisateurService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ affectationUtilisateur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(affectationUtilisateur);
      saveSubject.complete();

      // THEN
      expect(affectationUtilisateurFormService.getAffectationUtilisateur).toHaveBeenCalled();
      expect(affectationUtilisateurService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAffectationUtilisateur>();
      const affectationUtilisateur = { id: 4001 };
      vitest.spyOn(affectationUtilisateurService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ affectationUtilisateur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(affectationUtilisateurService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBoutique', () => {
      it('should forward to boutiqueService', () => {
        const entity = { id: 5005 };
        const entity2 = { id: 26278 };
        vitest.spyOn(boutiqueService, 'compareBoutique');
        comp.compareBoutique(entity, entity2);
        expect(boutiqueService.compareBoutique).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
