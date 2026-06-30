import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILotEtiquettes } from '../lot-etiquettes.model';
import { LotEtiquettesService } from '../service/lot-etiquettes.service';

import { LotEtiquettesFormService } from './lot-etiquettes-form.service';
import { LotEtiquettesUpdate } from './lot-etiquettes-update';

describe('LotEtiquettes Management Update Component', () => {
  let comp: LotEtiquettesUpdate;
  let fixture: ComponentFixture<LotEtiquettesUpdate>;
  let activatedRoute: ActivatedRoute;
  let lotEtiquettesFormService: LotEtiquettesFormService;
  let lotEtiquettesService: LotEtiquettesService;

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

    fixture = TestBed.createComponent(LotEtiquettesUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lotEtiquettesFormService = TestBed.inject(LotEtiquettesFormService);
    lotEtiquettesService = TestBed.inject(LotEtiquettesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const lotEtiquettes: ILotEtiquettes = { id: 17694 };

      activatedRoute.data = of({ lotEtiquettes });
      comp.ngOnInit();

      expect(comp.lotEtiquettes).toEqual(lotEtiquettes);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILotEtiquettes>();
      const lotEtiquettes = { id: 1087 };
      vitest.spyOn(lotEtiquettesFormService, 'getLotEtiquettes').mockReturnValue(lotEtiquettes);
      vitest.spyOn(lotEtiquettesService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lotEtiquettes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(lotEtiquettes);
      saveSubject.complete();

      // THEN
      expect(lotEtiquettesFormService.getLotEtiquettes).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(lotEtiquettesService.update).toHaveBeenCalledWith(expect.objectContaining(lotEtiquettes));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILotEtiquettes>();
      const lotEtiquettes = { id: 1087 };
      vitest.spyOn(lotEtiquettesFormService, 'getLotEtiquettes').mockReturnValue({ id: null });
      vitest.spyOn(lotEtiquettesService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lotEtiquettes: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(lotEtiquettes);
      saveSubject.complete();

      // THEN
      expect(lotEtiquettesFormService.getLotEtiquettes).toHaveBeenCalled();
      expect(lotEtiquettesService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILotEtiquettes>();
      const lotEtiquettes = { id: 1087 };
      vitest.spyOn(lotEtiquettesService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lotEtiquettes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lotEtiquettesService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
