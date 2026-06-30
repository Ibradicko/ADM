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
import { ICalculRedevance } from '../calcul-redevance.model';
import { CalculRedevanceService } from '../service/calcul-redevance.service';

import { CalculRedevanceFormService } from './calcul-redevance-form.service';
import { CalculRedevanceUpdate } from './calcul-redevance-update';

describe('CalculRedevance Management Update Component', () => {
  let comp: CalculRedevanceUpdate;
  let fixture: ComponentFixture<CalculRedevanceUpdate>;
  let activatedRoute: ActivatedRoute;
  let calculRedevanceFormService: CalculRedevanceFormService;
  let calculRedevanceService: CalculRedevanceService;
  let boutiqueService: BoutiqueService;
  let locataireService: LocataireService;

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

    fixture = TestBed.createComponent(CalculRedevanceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    calculRedevanceFormService = TestBed.inject(CalculRedevanceFormService);
    calculRedevanceService = TestBed.inject(CalculRedevanceService);
    boutiqueService = TestBed.inject(BoutiqueService);
    locataireService = TestBed.inject(LocataireService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boutique query and add missing value', () => {
      const calculRedevance: ICalculRedevance = { id: 4867 };
      const boutique: IBoutique = { id: 5005 };
      calculRedevance.boutique = boutique;

      const boutiqueCollection: IBoutique[] = [{ id: 5005 }];
      vitest.spyOn(boutiqueService, 'query').mockReturnValue(of(new HttpResponse({ body: boutiqueCollection })));
      const additionalBoutiques = [boutique];
      const expectedCollection: IBoutique[] = [...additionalBoutiques, ...boutiqueCollection];
      vitest.spyOn(boutiqueService, 'addBoutiqueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calculRedevance });
      comp.ngOnInit();

      expect(boutiqueService.query).toHaveBeenCalled();
      expect(boutiqueService.addBoutiqueToCollectionIfMissing).toHaveBeenCalledWith(
        boutiqueCollection,
        ...additionalBoutiques.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.boutiquesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Locataire query and add missing value', () => {
      const calculRedevance: ICalculRedevance = { id: 4867 };
      const locataire: ILocataire = { id: 3768 };
      calculRedevance.locataire = locataire;

      const locataireCollection: ILocataire[] = [{ id: 3768 }];
      vitest.spyOn(locataireService, 'query').mockReturnValue(of(new HttpResponse({ body: locataireCollection })));
      const additionalLocataires = [locataire];
      const expectedCollection: ILocataire[] = [...additionalLocataires, ...locataireCollection];
      vitest.spyOn(locataireService, 'addLocataireToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calculRedevance });
      comp.ngOnInit();

      expect(locataireService.query).toHaveBeenCalled();
      expect(locataireService.addLocataireToCollectionIfMissing).toHaveBeenCalledWith(
        locataireCollection,
        ...additionalLocataires.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.locatairesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const calculRedevance: ICalculRedevance = { id: 4867 };
      const boutique: IBoutique = { id: 5005 };
      calculRedevance.boutique = boutique;
      const locataire: ILocataire = { id: 3768 };
      calculRedevance.locataire = locataire;

      activatedRoute.data = of({ calculRedevance });
      comp.ngOnInit();

      expect(comp.boutiquesSharedCollection()).toContainEqual(boutique);
      expect(comp.locatairesSharedCollection()).toContainEqual(locataire);
      expect(comp.calculRedevance).toEqual(calculRedevance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICalculRedevance>();
      const calculRedevance = { id: 28461 };
      vitest.spyOn(calculRedevanceFormService, 'getCalculRedevance').mockReturnValue(calculRedevance);
      vitest.spyOn(calculRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calculRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(calculRedevance);
      saveSubject.complete();

      // THEN
      expect(calculRedevanceFormService.getCalculRedevance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(calculRedevanceService.update).toHaveBeenCalledWith(expect.objectContaining(calculRedevance));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICalculRedevance>();
      const calculRedevance = { id: 28461 };
      vitest.spyOn(calculRedevanceFormService, 'getCalculRedevance').mockReturnValue({ id: null });
      vitest.spyOn(calculRedevanceService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calculRedevance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(calculRedevance);
      saveSubject.complete();

      // THEN
      expect(calculRedevanceFormService.getCalculRedevance).toHaveBeenCalled();
      expect(calculRedevanceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICalculRedevance>();
      const calculRedevance = { id: 28461 };
      vitest.spyOn(calculRedevanceService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calculRedevance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(calculRedevanceService.update).toHaveBeenCalled();
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
  });
});
