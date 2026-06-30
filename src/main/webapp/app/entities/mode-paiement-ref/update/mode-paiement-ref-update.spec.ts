import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IModePaiementRef } from '../mode-paiement-ref.model';
import { ModePaiementRefService } from '../service/mode-paiement-ref.service';

import { ModePaiementRefFormService } from './mode-paiement-ref-form.service';
import { ModePaiementRefUpdate } from './mode-paiement-ref-update';

describe('ModePaiementRef Management Update Component', () => {
  let comp: ModePaiementRefUpdate;
  let fixture: ComponentFixture<ModePaiementRefUpdate>;
  let activatedRoute: ActivatedRoute;
  let modePaiementRefFormService: ModePaiementRefFormService;
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

    fixture = TestBed.createComponent(ModePaiementRefUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    modePaiementRefFormService = TestBed.inject(ModePaiementRefFormService);
    modePaiementRefService = TestBed.inject(ModePaiementRefService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const modePaiementRef: IModePaiementRef = { id: 1636 };

      activatedRoute.data = of({ modePaiementRef });
      comp.ngOnInit();

      expect(comp.modePaiementRef).toEqual(modePaiementRef);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IModePaiementRef>();
      const modePaiementRef = { id: 14388 };
      vitest.spyOn(modePaiementRefFormService, 'getModePaiementRef').mockReturnValue(modePaiementRef);
      vitest.spyOn(modePaiementRefService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ modePaiementRef });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(modePaiementRef);
      saveSubject.complete();

      // THEN
      expect(modePaiementRefFormService.getModePaiementRef).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(modePaiementRefService.update).toHaveBeenCalledWith(expect.objectContaining(modePaiementRef));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IModePaiementRef>();
      const modePaiementRef = { id: 14388 };
      vitest.spyOn(modePaiementRefFormService, 'getModePaiementRef').mockReturnValue({ id: null });
      vitest.spyOn(modePaiementRefService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ modePaiementRef: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(modePaiementRef);
      saveSubject.complete();

      // THEN
      expect(modePaiementRefFormService.getModePaiementRef).toHaveBeenCalled();
      expect(modePaiementRefService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IModePaiementRef>();
      const modePaiementRef = { id: 14388 };
      vitest.spyOn(modePaiementRefService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ modePaiementRef });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(modePaiementRefService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
