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
import { IRapportExport } from '../rapport-export.model';
import { RapportExportService } from '../service/rapport-export.service';

import { RapportExportFormService } from './rapport-export-form.service';
import { RapportExportUpdate } from './rapport-export-update';

describe('RapportExport Management Update Component', () => {
  let comp: RapportExportUpdate;
  let fixture: ComponentFixture<RapportExportUpdate>;
  let activatedRoute: ActivatedRoute;
  let rapportExportFormService: RapportExportFormService;
  let rapportExportService: RapportExportService;
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

    fixture = TestBed.createComponent(RapportExportUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rapportExportFormService = TestBed.inject(RapportExportFormService);
    rapportExportService = TestBed.inject(RapportExportService);
    boutiqueService = TestBed.inject(BoutiqueService);
    locataireService = TestBed.inject(LocataireService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const rapportExport: IRapportExport = { id: 15905 };
      const boutique: IBoutique = { id: 5005 };
      rapportExport.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Locataire query and add missing value', () => {
      const rapportExport: IRapportExport = { id: 15905 };
      const locataire: ILocataire = { id: 3768 };
      rapportExport.locataire = locataire;

      const locataireCollection: ILocataire[] = [{ id: 3768 }];
      vitest.spyOn(locataireService, 'query').mockReturnValue(of(new HttpResponse({ body: locataireCollection })));
      const additionalLocataires = [locataire];
      const expectedCollection: ILocataire[] = [...additionalLocataires, ...locataireCollection];
      vitest.spyOn(locataireService, 'addLocataireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      expect(locataireService.query).toHaveBeenCalled();
      expect(locataireService.addLocataireToCollectionIfMissing).toHaveBeenCalledWith(
        locataireCollection,
        ...additionalLocataires.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.locatairesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const rapportExport: IRapportExport = { id: 15905 };
      const utilisateur: IUser = { id: 3944 };
      rapportExport.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rapportExport: IRapportExport = { id: 15905 };
      const boutique: IBoutique = { id: 5005 };
      rapportExport.boutique = boutique;
      const locataire: ILocataire = { id: 3768 };
      rapportExport.locataire = locataire;
      const utilisateur: IUser = { id: 3944 };
      rapportExport.utilisateur = utilisateur;

      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.locatairesSharedCollection()).toContainEqual(locataire);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.rapportExport).toEqual(rapportExport);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRapportExport>();
      const rapportExport = { id: 650 };
      vitest.spyOn(rapportExportFormService, 'getRapportExport').mockReturnValue(rapportExport);
      vitest.spyOn(rapportExportService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rapportExport);
      saveSubject.complete();

      // THEN
      expect(rapportExportFormService.getRapportExport).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rapportExportService.update).toHaveBeenCalledWith(expect.objectContaining(rapportExport));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRapportExport>();
      const rapportExport = { id: 650 };
      vitest.spyOn(rapportExportFormService, 'getRapportExport').mockReturnValue({ id: null });
      vitest.spyOn(rapportExportService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rapportExport: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rapportExport);
      saveSubject.complete();

      // THEN
      expect(rapportExportFormService.getRapportExport).toHaveBeenCalled();
      expect(rapportExportService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRapportExport>();
      const rapportExport = { id: 650 };
      vitest.spyOn(rapportExportService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rapportExport });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rapportExportService.update).toHaveBeenCalled();
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
