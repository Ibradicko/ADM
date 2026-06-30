import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILotEtiquettes, NewLotEtiquettes } from '../lot-etiquettes.model';

export type PartialUpdateLotEtiquettes = Partial<ILotEtiquettes> & Pick<ILotEtiquettes, 'id'>;

type RestOf<T extends ILotEtiquettes | NewLotEtiquettes> = Omit<T, 'dateGeneration'> & {
  dateGeneration?: string | null;
};

export type RestLotEtiquettes = RestOf<ILotEtiquettes>;

export type NewRestLotEtiquettes = RestOf<NewLotEtiquettes>;

export type PartialUpdateRestLotEtiquettes = RestOf<PartialUpdateLotEtiquettes>;

@Injectable()
export class LotEtiquettesesService {
  readonly lotEtiquettesesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly lotEtiquettesesResource = httpResource<RestLotEtiquettes[]>(() => {
    const params = this.lotEtiquettesesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of lotEtiquettes that have been fetched. It is updated when the lotEtiquettesesResource emits a new value.
   * In case of error while fetching the lotEtiquetteses, the signal is set to an empty array.
   */
  readonly lotEtiquetteses = computed(() =>
    (this.lotEtiquettesesResource.hasValue() ? this.lotEtiquettesesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/lot-etiquettes');

  protected convertValueFromServer(restLotEtiquettes: RestLotEtiquettes): ILotEtiquettes {
    return {
      ...restLotEtiquettes,
      dateGeneration: restLotEtiquettes.dateGeneration ? dayjs(restLotEtiquettes.dateGeneration) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class LotEtiquettesService extends LotEtiquettesesService {
  protected readonly http = inject(HttpClient);

  create(lotEtiquettes: NewLotEtiquettes): Observable<ILotEtiquettes> {
    const copy = this.convertValueFromClient(lotEtiquettes);
    return this.http.post<RestLotEtiquettes>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(lotEtiquettes: ILotEtiquettes): Observable<ILotEtiquettes> {
    const copy = this.convertValueFromClient(lotEtiquettes);
    return this.http
      .put<RestLotEtiquettes>(`${this.resourceUrl}/${encodeURIComponent(this.getLotEtiquettesIdentifier(lotEtiquettes))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(lotEtiquettes: PartialUpdateLotEtiquettes): Observable<ILotEtiquettes> {
    const copy = this.convertValueFromClient(lotEtiquettes);
    return this.http
      .patch<RestLotEtiquettes>(`${this.resourceUrl}/${encodeURIComponent(this.getLotEtiquettesIdentifier(lotEtiquettes))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ILotEtiquettes> {
    return this.http
      .get<RestLotEtiquettes>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ILotEtiquettes[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLotEtiquettes[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLotEtiquettesIdentifier(lotEtiquettes: Pick<ILotEtiquettes, 'id'>): number {
    return lotEtiquettes.id;
  }

  compareLotEtiquettes(o1: Pick<ILotEtiquettes, 'id'> | null, o2: Pick<ILotEtiquettes, 'id'> | null): boolean {
    return o1 && o2 ? this.getLotEtiquettesIdentifier(o1) === this.getLotEtiquettesIdentifier(o2) : o1 === o2;
  }

  addLotEtiquettesToCollectionIfMissing<Type extends Pick<ILotEtiquettes, 'id'>>(
    lotEtiquettesCollection: Type[],
    ...lotEtiquettesesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const lotEtiquetteses: Type[] = lotEtiquettesesToCheck.filter(isPresent);
    if (lotEtiquetteses.length > 0) {
      const lotEtiquettesCollectionIdentifiers = lotEtiquettesCollection.map(lotEtiquettesItem =>
        this.getLotEtiquettesIdentifier(lotEtiquettesItem),
      );
      const lotEtiquettesesToAdd = lotEtiquetteses.filter(lotEtiquettesItem => {
        const lotEtiquettesIdentifier = this.getLotEtiquettesIdentifier(lotEtiquettesItem);
        if (lotEtiquettesCollectionIdentifiers.includes(lotEtiquettesIdentifier)) {
          return false;
        }
        lotEtiquettesCollectionIdentifiers.push(lotEtiquettesIdentifier);
        return true;
      });
      return [...lotEtiquettesesToAdd, ...lotEtiquettesCollection];
    }
    return lotEtiquettesCollection;
  }

  protected convertValueFromClient<T extends ILotEtiquettes | NewLotEtiquettes | PartialUpdateLotEtiquettes>(lotEtiquettes: T): RestOf<T> {
    return {
      ...lotEtiquettes,
      dateGeneration: lotEtiquettes.dateGeneration?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestLotEtiquettes): ILotEtiquettes {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestLotEtiquettes[]): ILotEtiquettes[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
