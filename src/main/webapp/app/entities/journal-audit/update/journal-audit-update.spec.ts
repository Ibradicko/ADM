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
import { IJournalAudit } from '../journal-audit.model';
import { JournalAuditService } from '../service/journal-audit.service';

import { JournalAuditFormService } from './journal-audit-form.service';
import { JournalAuditUpdate } from './journal-audit-update';

describe('JournalAudit Management Update Component', () => {
  let comp: JournalAuditUpdate;
  let fixture: ComponentFixture<JournalAuditUpdate>;
  let activatedRoute: ActivatedRoute;
  let journalAuditFormService: JournalAuditFormService;
  let journalAuditService: JournalAuditService;
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

    fixture = TestBed.createComponent(JournalAuditUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    journalAuditFormService = TestBed.inject(JournalAuditFormService);
    journalAuditService = TestBed.inject(JournalAuditService);
    boutiqueService = TestBed.inject(BoutiqueService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const journalAudit: IJournalAudit = { id: 13768 };
      const boutique: IBoutique = { id: 5005 };
      journalAudit.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ journalAudit });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const journalAudit: IJournalAudit = { id: 13768 };
      const utilisateur: IUser = { id: 3944 };
      journalAudit.utilisateur = utilisateur;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [utilisateur];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ journalAudit });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const journalAudit: IJournalAudit = { id: 13768 };
      const boutique: IBoutique = { id: 5005 };
      journalAudit.boutique = boutique;
      const utilisateur: IUser = { id: 3944 };
      journalAudit.utilisateur = utilisateur;

      activatedRoute.data = of({ journalAudit });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.usersSharedCollection()).toContainEqual(utilisateur);
      expect(comp.journalAudit).toEqual(journalAudit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalAudit>();
      const journalAudit = { id: 25137 };
      vitest.spyOn(journalAuditFormService, 'getJournalAudit').mockReturnValue(journalAudit);
      vitest.spyOn(journalAuditService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalAudit);
      saveSubject.complete();

      // THEN
      expect(journalAuditFormService.getJournalAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(journalAuditService.update).toHaveBeenCalledWith(expect.objectContaining(journalAudit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalAudit>();
      const journalAudit = { id: 25137 };
      vitest.spyOn(journalAuditFormService, 'getJournalAudit').mockReturnValue({ id: null });
      vitest.spyOn(journalAuditService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalAudit);
      saveSubject.complete();

      // THEN
      expect(journalAuditFormService.getJournalAudit).toHaveBeenCalled();
      expect(journalAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalAudit>();
      const journalAudit = { id: 25137 };
      vitest.spyOn(journalAuditService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(journalAuditService.update).toHaveBeenCalled();
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
