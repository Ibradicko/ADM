import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBoutique } from '../boutique.model';
import { BoutiqueService } from '../service/boutique.service';

import { BoutiqueFormService } from './boutique-form.service';
import { BoutiqueUpdate } from './boutique-update';

describe('Boutique Management Update Component', () => {
  let comp: BoutiqueUpdate;
  let fixture: ComponentFixture<BoutiqueUpdate>;
  let activatedRoute: ActivatedRoute;
  let boutiqueFormService: BoutiqueFormService;
  let boutiqueService: BoutiqueService;

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

    fixture = TestBed.createComponent(BoutiqueUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    boutiqueFormService = TestBed.inject(BoutiqueFormService);
    boutiqueService = TestBed.inject(BoutiqueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const boutique: IBoutique = { id: 26278 };

      activatedRoute.data = of({ boutique });
      comp.ngOnInit();

      expect(comp.boutique).toEqual(boutique);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBoutique>();
      const boutique = { id: 5005 };
      vitest.spyOn(boutiqueFormService, 'getBoutique').mockReturnValue(boutique);
      vitest.spyOn(boutiqueService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boutique });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(boutique);
      saveSubject.complete();

      // THEN
      expect(boutiqueFormService.getBoutique).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(boutiqueService.update).toHaveBeenCalledWith(expect.objectContaining(boutique));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBoutique>();
      const boutique = { id: 5005 };
      vitest.spyOn(boutiqueFormService, 'getBoutique').mockReturnValue({ id: null });
      vitest.spyOn(boutiqueService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boutique: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(boutique);
      saveSubject.complete();

      // THEN
      expect(boutiqueFormService.getBoutique).toHaveBeenCalled();
      expect(boutiqueService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBoutique>();
      const boutique = { id: 5005 };
      vitest.spyOn(boutiqueService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boutique });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(boutiqueService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
