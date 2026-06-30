import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { VenteService } from '../service/vente.service';
import { IVente } from '../vente.model';

import { VenteFormService } from './vente-form.service';
import { VenteUpdate } from './vente-update';

describe('Vente Management Update Component', () => {
  let comp: VenteUpdate;
  let fixture: ComponentFixture<VenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let venteFormService: VenteFormService;
  let venteService: VenteService;
  let boutiqueService: BoutiqueService;
  let locataireService: LocataireService;
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

    fixture = TestBed.createComponent(VenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    venteFormService = TestBed.inject(VenteFormService);
    venteService = TestBed.inject(VenteService);
    boutiqueService = TestBed.inject(BoutiqueService);
    locataireService = TestBed.inject(LocataireService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const vente: IVente = { id: 9754 };
      const boutique: IBoutique = { id: 5005 };
      vente.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Locataire query and add missing value', () => {
      const vente: IVente = { id: 9754 };
      const locataire: ILocataire = { id: 3768 };
      vente.locataire = locataire;

      const locataireCollection: ILocataire[] = [{ id: 3768 }];
      vitest.spyOn(locataireService, 'query').mockReturnValue(of(new HttpResponse({ body: locataireCollection })));
      const additionalLocataires = [locataire];
      const expectedCollection: ILocataire[] = [...additionalLocataires, ...locataireCollection];
      vitest.spyOn(locataireService, 'addLocataireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      expect(locataireService.query).toHaveBeenCalled();
      expect(locataireService.addLocataireToCollectionIfMissing).toHaveBeenCalledWith(
        locataireCollection,
        ...additionalLocataires.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.locatairesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const vente: IVente = { id: 9754 };
      const vendeur: IUser = { id: 3944 };
      vente.vendeur = vendeur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [vendeur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const vente: IVente = { id: 9754 };
      const boutique: IBoutique = { id: 5005 };
      vente.boutique = boutique;
      const locataire: ILocataire = { id: 3768 };
      vente.locataire = locataire;
      const vendeur: IUser = { id: 3944 };
      vente.vendeur = vendeur;

      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.locatairesSharedCollection()).toContainEqual(locataire);
      expect(comp.usersSharedCollection()).toContainEqual(vendeur);
      expect(comp.vente).toEqual(vente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVente>();
      const vente = { id: 25749 };
      vitest.spyOn(venteFormService, 'getVente').mockReturnValue(vente);
      vitest.spyOn(venteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(vente);
      saveSubject.complete();

      // THEN
      expect(venteFormService.getVente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(venteService.update).toHaveBeenCalledWith(expect.objectContaining(vente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVente>();
      const vente = { id: 25749 };
      vitest.spyOn(venteFormService, 'getVente').mockReturnValue({ id: null });
      vitest.spyOn(venteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(vente);
      saveSubject.complete();

      // THEN
      expect(venteFormService.getVente).toHaveBeenCalled();
      expect(venteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVente>();
      const vente = { id: 25749 };
      vitest.spyOn(venteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(venteService.update).toHaveBeenCalled();
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

    describe('compareLocataire', () => {
      it('should forward to locataireService', () => {
        const entity = { id: 3768 };
        const entity2 = { id: 24112 };
        vitest.spyOn(locataireService, 'compareLocataire');
        comp.compareLocataire(entity, entity2);
        expect(locataireService.compareLocataire).toHaveBeenCalledWith(entity, entity2);
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
