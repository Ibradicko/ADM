import { MockInstance, afterEach, beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import {
  faEye,
  faPencilAlt,
  faPlus,
  faSearch,
  faSort,
  faSortDown,
  faSortUp,
  faSync,
  faTimes,
  faUserCheck,
  faUserPlus,
} from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subject, of } from 'rxjs';

import { sampleWithRequiredData } from '../boutique.test-samples';
import { BoutiqueService } from '../service/boutique.service';

import { Boutique } from './boutique';

vitest.useFakeTimers();

describe('Boutique Management Component', () => {
  let httpMock: HttpTestingController;
  let comp: Boutique;
  let fixture: ComponentFixture<Boutique>;
  let service: BoutiqueService;
  let routerNavigateSpy: MockInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(Boutique);
    comp = fixture.componentInstance;
    service = TestBed.inject(BoutiqueService);
    routerNavigateSpy = vitest.spyOn(comp.router, 'navigate');

    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faEye, faPencilAlt, faPlus, faSearch, faSort, faSortDown, faSortUp, faSync, faTimes, faUserCheck, faUserPlus);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
    httpMock.verify();
  });

  it('should call load all on init', async () => {
    // WHEN
    TestBed.tick();
    const req = httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/exploitation-boutiques')).flush([]);
    req.flush([{ id: 5005 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN
    expect(comp.isLoading()).toEqual(false);
    expect(comp.boutiques()[0]).toEqual(expect.objectContaining({ id: 5005 }));
  });

  it('should cancel previous requests when loading a new page', async () => {
    // WHEN
    TestBed.tick();
    const req = httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/exploitation-boutiques')).flush([]);
    await vitest.runAllTimersAsync();

    comp.page.set(3);
    comp.load();
    await vitest.runAllTimersAsync();
    const req2 = httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    req2.flush([{ id: 5005 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN
    expect(req.cancelled).toBeTruthy();
    expect(comp.isLoading()).toEqual(false);
    expect(comp.boutiques()[0]).toEqual(expect.objectContaining({ id: 5005 }));
  });

  it('should not fail on resource error state', async () => {
    // GIVEN - first load triggers an HTTP error
    TestBed.tick();
    const errorReq = httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/exploitation-boutiques')).flush([]);
    errorReq.flush('error', { status: 500, statusText: 'Server Error' });
    await vitest.runAllTimersAsync();

    // THEN - loading state was reset and list is empty
    expect(comp.isLoading()).toBe(false);
    expect(comp.boutiques()).toEqual([]);

    // WHEN - second load should still work
    comp.load();
    TestBed.tick();
    const successReq = httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    successReq.flush([{ id: 5005 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN - subscription is still alive and second load succeeds
    expect(comp.boutiques()[0]).toEqual(expect.objectContaining({ id: 5005 }));
  });

  describe('trackId', () => {
    it('should forward to boutiqueService', () => {
      const entity = { id: 5005 };
      vitest.spyOn(service, 'getBoutiqueIdentifier');
      const id = comp.trackId(entity);
      expect(service.getBoutiqueIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/exploitation-boutiques'));

    // THEN
    expect(service.boutiquesParams()).toMatchObject(expect.objectContaining({ sort: ['id,desc'] }));
  });

  it('should calculate the filter attribute', () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/boutiques'));
    httpMock.expectOne(r => r.method === 'GET' && r.url.includes('api/exploitation-boutiques'));

    // THEN
    expect(service.boutiquesParams()).toMatchObject(expect.objectContaining({ 'someId.in': ['dc4279ea-cfb9-11ec-9d64-0242ac120002'] }));
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      // NgbModal is not a singleton using TestBed.inject.
      // ngbModal = TestBed.inject(NgbModal);
      ngbModal = (comp as any).modalService;
      vitest.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next('deleted');

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).toHaveBeenCalled();
    }));

    it('on dismiss should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next();

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).not.toHaveBeenCalled();
    }));
  });
});
