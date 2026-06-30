import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPaiementRedevance, NewPaiementRedevance } from '../paiement-redevance.model';

export type PartialUpdatePaiementRedevance = Partial<IPaiementRedevance> & Pick<IPaiementRedevance, 'id'>;

type RestOf<T extends IPaiementRedevance | NewPaiementRedevance> = Omit<T, 'datePaiement'> & {
  datePaiement?: string | null;
};

export type RestPaiementRedevance = RestOf<IPaiementRedevance>;

export type NewRestPaiementRedevance = RestOf<NewPaiementRedevance>;

export type PartialUpdateRestPaiementRedevance = RestOf<PartialUpdatePaiementRedevance>;

@Injectable()
export class PaiementRedevancesService {
  readonly paiementRedevancesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly paiementRedevancesResource = httpResource<RestPaiementRedevance[]>(() => {
    const params = this.paiementRedevancesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of paiementRedevance that have been fetched. It is updated when the paiementRedevancesResource emits a new value.
   * In case of error while fetching the paiementRedevances, the signal is set to an empty array.
   */
  readonly paiementRedevances = computed(() =>
    (this.paiementRedevancesResource.hasValue() ? this.paiementRedevancesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/paiement-redevances');

  protected convertValueFromServer(restPaiementRedevance: RestPaiementRedevance): IPaiementRedevance {
    return {
      ...restPaiementRedevance,
      datePaiement: restPaiementRedevance.datePaiement ? dayjs(restPaiementRedevance.datePaiement) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PaiementRedevanceService extends PaiementRedevancesService {
  protected readonly http = inject(HttpClient);

  create(paiementRedevance: NewPaiementRedevance): Observable<IPaiementRedevance> {
    const copy = this.convertValueFromClient(paiementRedevance);
    return this.http.post<RestPaiementRedevance>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(paiementRedevance: IPaiementRedevance): Observable<IPaiementRedevance> {
    const copy = this.convertValueFromClient(paiementRedevance);
    return this.http
      .put<RestPaiementRedevance>(`${this.resourceUrl}/${encodeURIComponent(this.getPaiementRedevanceIdentifier(paiementRedevance))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(paiementRedevance: PartialUpdatePaiementRedevance): Observable<IPaiementRedevance> {
    const copy = this.convertValueFromClient(paiementRedevance);
    return this.http
      .patch<RestPaiementRedevance>(
        `${this.resourceUrl}/${encodeURIComponent(this.getPaiementRedevanceIdentifier(paiementRedevance))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPaiementRedevance> {
    return this.http
      .get<RestPaiementRedevance>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPaiementRedevance[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaiementRedevance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPaiementRedevanceIdentifier(paiementRedevance: Pick<IPaiementRedevance, 'id'>): number {
    return paiementRedevance.id;
  }

  comparePaiementRedevance(o1: Pick<IPaiementRedevance, 'id'> | null, o2: Pick<IPaiementRedevance, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaiementRedevanceIdentifier(o1) === this.getPaiementRedevanceIdentifier(o2) : o1 === o2;
  }

  addPaiementRedevanceToCollectionIfMissing<Type extends Pick<IPaiementRedevance, 'id'>>(
    paiementRedevanceCollection: Type[],
    ...paiementRedevancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paiementRedevances: Type[] = paiementRedevancesToCheck.filter(isPresent);
    if (paiementRedevances.length > 0) {
      const paiementRedevanceCollectionIdentifiers = paiementRedevanceCollection.map(paiementRedevanceItem =>
        this.getPaiementRedevanceIdentifier(paiementRedevanceItem),
      );
      const paiementRedevancesToAdd = paiementRedevances.filter(paiementRedevanceItem => {
        const paiementRedevanceIdentifier = this.getPaiementRedevanceIdentifier(paiementRedevanceItem);
        if (paiementRedevanceCollectionIdentifiers.includes(paiementRedevanceIdentifier)) {
          return false;
        }
        paiementRedevanceCollectionIdentifiers.push(paiementRedevanceIdentifier);
        return true;
      });
      return [...paiementRedevancesToAdd, ...paiementRedevanceCollection];
    }
    return paiementRedevanceCollection;
  }

  protected convertValueFromClient<T extends IPaiementRedevance | NewPaiementRedevance | PartialUpdatePaiementRedevance>(
    paiementRedevance: T,
  ): RestOf<T> {
    return {
      ...paiementRedevance,
      datePaiement: paiementRedevance.datePaiement?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPaiementRedevance): IPaiementRedevance {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPaiementRedevance[]): IPaiementRedevance[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
