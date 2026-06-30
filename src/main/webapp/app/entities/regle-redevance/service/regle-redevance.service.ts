import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRegleRedevance, NewRegleRedevance } from '../regle-redevance.model';

export type PartialUpdateRegleRedevance = Partial<IRegleRedevance> & Pick<IRegleRedevance, 'id'>;

type RestOf<T extends IRegleRedevance | NewRegleRedevance> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

export type RestRegleRedevance = RestOf<IRegleRedevance>;

export type NewRestRegleRedevance = RestOf<NewRegleRedevance>;

export type PartialUpdateRestRegleRedevance = RestOf<PartialUpdateRegleRedevance>;

@Injectable()
export class RegleRedevancesService {
  readonly regleRedevancesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly regleRedevancesResource = httpResource<RestRegleRedevance[]>(() => {
    const params = this.regleRedevancesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of regleRedevance that have been fetched. It is updated when the regleRedevancesResource emits a new value.
   * In case of error while fetching the regleRedevances, the signal is set to an empty array.
   */
  readonly regleRedevances = computed(() =>
    (this.regleRedevancesResource.hasValue() ? this.regleRedevancesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/regle-redevances');

  protected convertValueFromServer(restRegleRedevance: RestRegleRedevance): IRegleRedevance {
    return {
      ...restRegleRedevance,
      dateDebut: restRegleRedevance.dateDebut ? dayjs(restRegleRedevance.dateDebut) : undefined,
      dateFin: restRegleRedevance.dateFin ? dayjs(restRegleRedevance.dateFin) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RegleRedevanceService extends RegleRedevancesService {
  protected readonly http = inject(HttpClient);

  create(regleRedevance: NewRegleRedevance): Observable<IRegleRedevance> {
    const copy = this.convertValueFromClient(regleRedevance);
    return this.http.post<RestRegleRedevance>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(regleRedevance: IRegleRedevance): Observable<IRegleRedevance> {
    const copy = this.convertValueFromClient(regleRedevance);
    return this.http
      .put<RestRegleRedevance>(`${this.resourceUrl}/${encodeURIComponent(this.getRegleRedevanceIdentifier(regleRedevance))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(regleRedevance: PartialUpdateRegleRedevance): Observable<IRegleRedevance> {
    const copy = this.convertValueFromClient(regleRedevance);
    return this.http
      .patch<RestRegleRedevance>(`${this.resourceUrl}/${encodeURIComponent(this.getRegleRedevanceIdentifier(regleRedevance))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRegleRedevance> {
    return this.http
      .get<RestRegleRedevance>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRegleRedevance[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRegleRedevance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRegleRedevanceIdentifier(regleRedevance: Pick<IRegleRedevance, 'id'>): number {
    return regleRedevance.id;
  }

  compareRegleRedevance(o1: Pick<IRegleRedevance, 'id'> | null, o2: Pick<IRegleRedevance, 'id'> | null): boolean {
    return o1 && o2 ? this.getRegleRedevanceIdentifier(o1) === this.getRegleRedevanceIdentifier(o2) : o1 === o2;
  }

  addRegleRedevanceToCollectionIfMissing<Type extends Pick<IRegleRedevance, 'id'>>(
    regleRedevanceCollection: Type[],
    ...regleRedevancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const regleRedevances: Type[] = regleRedevancesToCheck.filter(isPresent);
    if (regleRedevances.length > 0) {
      const regleRedevanceCollectionIdentifiers = regleRedevanceCollection.map(regleRedevanceItem =>
        this.getRegleRedevanceIdentifier(regleRedevanceItem),
      );
      const regleRedevancesToAdd = regleRedevances.filter(regleRedevanceItem => {
        const regleRedevanceIdentifier = this.getRegleRedevanceIdentifier(regleRedevanceItem);
        if (regleRedevanceCollectionIdentifiers.includes(regleRedevanceIdentifier)) {
          return false;
        }
        regleRedevanceCollectionIdentifiers.push(regleRedevanceIdentifier);
        return true;
      });
      return [...regleRedevancesToAdd, ...regleRedevanceCollection];
    }
    return regleRedevanceCollection;
  }

  protected convertValueFromClient<T extends IRegleRedevance | NewRegleRedevance | PartialUpdateRegleRedevance>(
    regleRedevance: T,
  ): RestOf<T> {
    return {
      ...regleRedevance,
      dateDebut: regleRedevance.dateDebut?.format(DATE_FORMAT) ?? null,
      dateFin: regleRedevance.dateFin?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRegleRedevance): IRegleRedevance {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRegleRedevance[]): IRegleRedevance[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
