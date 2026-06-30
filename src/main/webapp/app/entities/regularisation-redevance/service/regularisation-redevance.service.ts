import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRegularisationRedevance, NewRegularisationRedevance } from '../regularisation-redevance.model';

export type PartialUpdateRegularisationRedevance = Partial<IRegularisationRedevance> & Pick<IRegularisationRedevance, 'id'>;

type RestOf<T extends IRegularisationRedevance | NewRegularisationRedevance> = Omit<T, 'dateRegularisation'> & {
  dateRegularisation?: string | null;
};

export type RestRegularisationRedevance = RestOf<IRegularisationRedevance>;

export type NewRestRegularisationRedevance = RestOf<NewRegularisationRedevance>;

export type PartialUpdateRestRegularisationRedevance = RestOf<PartialUpdateRegularisationRedevance>;

@Injectable()
export class RegularisationRedevancesService {
  readonly regularisationRedevancesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly regularisationRedevancesResource = httpResource<RestRegularisationRedevance[]>(() => {
    const params = this.regularisationRedevancesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of regularisationRedevance that have been fetched. It is updated when the regularisationRedevancesResource emits a new value.
   * In case of error while fetching the regularisationRedevances, the signal is set to an empty array.
   */
  readonly regularisationRedevances = computed(() =>
    (this.regularisationRedevancesResource.hasValue() ? this.regularisationRedevancesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/regularisation-redevances');

  protected convertValueFromServer(restRegularisationRedevance: RestRegularisationRedevance): IRegularisationRedevance {
    return {
      ...restRegularisationRedevance,
      dateRegularisation: restRegularisationRedevance.dateRegularisation
        ? dayjs(restRegularisationRedevance.dateRegularisation)
        : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RegularisationRedevanceService extends RegularisationRedevancesService {
  protected readonly http = inject(HttpClient);

  create(regularisationRedevance: NewRegularisationRedevance): Observable<IRegularisationRedevance> {
    const copy = this.convertValueFromClient(regularisationRedevance);
    return this.http.post<RestRegularisationRedevance>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(regularisationRedevance: IRegularisationRedevance): Observable<IRegularisationRedevance> {
    const copy = this.convertValueFromClient(regularisationRedevance);
    return this.http
      .put<RestRegularisationRedevance>(
        `${this.resourceUrl}/${encodeURIComponent(this.getRegularisationRedevanceIdentifier(regularisationRedevance))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(regularisationRedevance: PartialUpdateRegularisationRedevance): Observable<IRegularisationRedevance> {
    const copy = this.convertValueFromClient(regularisationRedevance);
    return this.http
      .patch<RestRegularisationRedevance>(
        `${this.resourceUrl}/${encodeURIComponent(this.getRegularisationRedevanceIdentifier(regularisationRedevance))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRegularisationRedevance> {
    return this.http
      .get<RestRegularisationRedevance>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRegularisationRedevance[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRegularisationRedevance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRegularisationRedevanceIdentifier(regularisationRedevance: Pick<IRegularisationRedevance, 'id'>): number {
    return regularisationRedevance.id;
  }

  compareRegularisationRedevance(
    o1: Pick<IRegularisationRedevance, 'id'> | null,
    o2: Pick<IRegularisationRedevance, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getRegularisationRedevanceIdentifier(o1) === this.getRegularisationRedevanceIdentifier(o2) : o1 === o2;
  }

  addRegularisationRedevanceToCollectionIfMissing<Type extends Pick<IRegularisationRedevance, 'id'>>(
    regularisationRedevanceCollection: Type[],
    ...regularisationRedevancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const regularisationRedevances: Type[] = regularisationRedevancesToCheck.filter(isPresent);
    if (regularisationRedevances.length > 0) {
      const regularisationRedevanceCollectionIdentifiers = regularisationRedevanceCollection.map(regularisationRedevanceItem =>
        this.getRegularisationRedevanceIdentifier(regularisationRedevanceItem),
      );
      const regularisationRedevancesToAdd = regularisationRedevances.filter(regularisationRedevanceItem => {
        const regularisationRedevanceIdentifier = this.getRegularisationRedevanceIdentifier(regularisationRedevanceItem);
        if (regularisationRedevanceCollectionIdentifiers.includes(regularisationRedevanceIdentifier)) {
          return false;
        }
        regularisationRedevanceCollectionIdentifiers.push(regularisationRedevanceIdentifier);
        return true;
      });
      return [...regularisationRedevancesToAdd, ...regularisationRedevanceCollection];
    }
    return regularisationRedevanceCollection;
  }

  protected convertValueFromClient<T extends IRegularisationRedevance | NewRegularisationRedevance | PartialUpdateRegularisationRedevance>(
    regularisationRedevance: T,
  ): RestOf<T> {
    return {
      ...regularisationRedevance,
      dateRegularisation: regularisationRedevance.dateRegularisation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRegularisationRedevance): IRegularisationRedevance {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRegularisationRedevance[]): IRegularisationRedevance[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
