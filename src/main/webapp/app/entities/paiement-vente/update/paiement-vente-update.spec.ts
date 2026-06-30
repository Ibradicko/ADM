import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IModePaiementRef } from 'app/entities/mode-paiement-ref/mode-paiement-ref.model';
import { ModePaiementRefService } from 'app/entities/mode-paiement-ref/service/mode-paiement-ref.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { IPaiementVente } from '../paiement-vente.model';
import { PaiementVenteService } from '../service/paiement-vente.service';

import { PaiementVenteFormService } from './paiement-vente-form.service';
import { PaiementVenteUpdate } from './paiement-vente-update';

describe('PaiementVente Management Update Component', () => {
  let comp: PaiementVenteUpdate;
  let fixture: ComponentFixture<PaiementVenteUpdate>;
  let activatedRoute: ActivatedRoute;
  let paiementVenteFormService: PaiementVenteFormService;
  let paiementVenteService: PaiementVenteService;
  let venteService: VenteService;
  let modePaiementRefService: ModePaiementRefService;

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

    fixture = TestBed.createComponent(PaiementVenteUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paiementVenteFormService = TestBed.inject(PaiementVenteFormService);
    paiementVenteService = TestBed.inject(PaiementVenteService);
    venteService = TestBed.inject(VenteService);
    modePaiementRefService = TestBed.inject(ModePaiementRefService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Vente query and add missing value', () => {
      const paiementVente: IPaiementVente = { id: 20053 };
      const vente: IVente = { id: 25749 };
      paiementVente.vente = vente;

      const venteCollection: IVente[] = [{ id: 25749 }];
      vitest.spyOn(venteService, 'query').mockReturnValue(of(new HttpResponse({ body: venteCollection })));
      const additionalVentes = [vente];
      const expectedCollection: IVente[] = [...additionalVentes, ...venteCollection];
      vitest.spyOn(venteService, 'addVenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paiementVente });
      comp.ngOnInit();

      expect(venteService.query).toHaveBeenCalled();
      expect(venteService.addVenteToCollectionIfMissing).toHaveBeenCalledWith(
        venteCollection,
        ...additionalVentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ventesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call ModePaiementRef query and add missing value', () => {
      const paiementVente: IPaiementVente = { id: 20053 };
      const modePaiement: IModePaiementRef = { id: 14388 };
      paiementVente.modePaiement = modePaiement;

      const modePaiementRefCollection: IModePaiementRef[] = [{ id: 14388 }];
      vitest.spyOn(modePaiementRefService, 'query').mockReturnValue(of(new HttpResponse({ body: modePaiementRefCollection })));
      const additionalModePaiementRefs = [modePaiement];
      const expectedCollection: IModePaiementRef[] = [...additionalModePaiementRefs, ...modePaiementRefCollection];
      vitest.spyOn(modePaiementRefService, 'addModePaiementRefToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paiementVente });
      comp.ngOnInit();

      expect(modePaiementRefService.query).toHaveBeenCalled();
      expect(modePaiementRefService.addModePaiementRefToCollectionIfMissing).toHaveBeenCalledWith(
        modePaiementRefCollection,
        ...additionalModePaiementRefs.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.modePaiementRefsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const paiementVente: IPaiementVente = { id: 20053 };
      const vente: IVente = { id: 25749 };
      paiementVente.vente = vente;
      const modePaiement: IModePaiementRef = { id: 14388 };
      paiementVente.modePaiement = modePaiement;

      activatedRoute.data = of({ paiementVente });
      comp.ngOnInit();

      expect(comp.ventesSharedCollection()).toContainEqual(vente);
      expect(comp.modePaiementRefsSharedCollection()).toContainEqual(modePaiement);
      expect(comp.paiementVente).toEqual(paiementVente);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementVente>();
      const paiementVente = { id: 30087 };
      vitest.spyOn(paiementVenteFormService, 'getPaiementVente').mockReturnValue(paiementVente);
      vitest.spyOn(paiementVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiementVente);
      saveSubject.complete();

      // THEN
      expect(paiementVenteFormService.getPaiementVente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paiementVenteService.update).toHaveBeenCalledWith(expect.objectContaining(paiementVente));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementVente>();
      const paiementVente = { id: 30087 };
      vitest.spyOn(paiementVenteFormService, 'getPaiementVente').mockReturnValue({ id: null });
      vitest.spyOn(paiementVenteService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementVente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiementVente);
      saveSubject.complete();

      // THEN
      expect(paiementVenteFormService.getPaiementVente).toHaveBeenCalled();
      expect(paiementVenteService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiementVente>();
      const paiementVente = { id: 30087 };
      vitest.spyOn(paiementVenteService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiementVente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paiementVenteService.update).toHaveBeenCalled();
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

    describe('compareModePaiementRef', () => {
      it('should forward to modePaiementRefService', () => {
        const entity = { id: 14388 };
        const entity2 = { id: 1636 };
        vitest.spyOn(modePaiementRefService, 'compareModePaiementRef');
        comp.compareModePaiementRef(entity, entity2);
        expect(modePaiementRefService.compareModePaiementRef).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
