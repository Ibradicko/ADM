import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { TicketCaisseService } from '../service/ticket-caisse.service';
import { ITicketCaisse } from '../ticket-caisse.model';

import { TicketCaisseFormService } from './ticket-caisse-form.service';
import { TicketCaisseUpdate } from './ticket-caisse-update';

describe('TicketCaisse Management Update Component', () => {
  let comp: TicketCaisseUpdate;
  let fixture: ComponentFixture<TicketCaisseUpdate>;
  let activatedRoute: ActivatedRoute;
  let ticketCaisseFormService: TicketCaisseFormService;
  let ticketCaisseService: TicketCaisseService;
  let venteService: VenteService;

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

    fixture = TestBed.createComponent(TicketCaisseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketCaisseFormService = TestBed.inject(TicketCaisseFormService);
    ticketCaisseService = TestBed.inject(TicketCaisseService);
    venteService = TestBed.inject(VenteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Vente query and add missing value', () => {
      const ticketCaisse: ITicketCaisse = { id: 29133 };
      const vente: IVente = { id: 25749 };
      ticketCaisse.vente = vente;

      const venteCollection: IVente[] = [{ id: 25749 }];
      vitest.spyOn(venteService, 'query').mockReturnValue(of(new HttpResponse({ body: venteCollection })));
      const additionalVentes = [vente];
      const expectedCollection: IVente[] = [...additionalVentes, ...venteCollection];
      vitest.spyOn(venteService, 'addVenteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ticketCaisse });
      comp.ngOnInit();

      expect(venteService.query).toHaveBeenCalled();
      expect(venteService.addVenteToCollectionIfMissing).toHaveBeenCalledWith(
        venteCollection,
        ...additionalVentes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ventesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ticketCaisse: ITicketCaisse = { id: 29133 };
      const vente: IVente = { id: 25749 };
      ticketCaisse.vente = vente;

      activatedRoute.data = of({ ticketCaisse });
      comp.ngOnInit();

      expect(comp.ventesSharedCollection()).toContainEqual(vente);
      expect(comp.ticketCaisse).toEqual(ticketCaisse);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketCaisse>();
      const ticketCaisse = { id: 27212 };
      vitest.spyOn(ticketCaisseFormService, 'getTicketCaisse').mockReturnValue(ticketCaisse);
      vitest.spyOn(ticketCaisseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketCaisse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticketCaisse);
      saveSubject.complete();

      // THEN
      expect(ticketCaisseFormService.getTicketCaisse).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketCaisseService.update).toHaveBeenCalledWith(expect.objectContaining(ticketCaisse));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketCaisse>();
      const ticketCaisse = { id: 27212 };
      vitest.spyOn(ticketCaisseFormService, 'getTicketCaisse').mockReturnValue({ id: null });
      vitest.spyOn(ticketCaisseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketCaisse: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ticketCaisse);
      saveSubject.complete();

      // THEN
      expect(ticketCaisseFormService.getTicketCaisse).toHaveBeenCalled();
      expect(ticketCaisseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITicketCaisse>();
      const ticketCaisse = { id: 27212 };
      vitest.spyOn(ticketCaisseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketCaisse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketCaisseService.update).toHaveBeenCalled();
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
  });
});
