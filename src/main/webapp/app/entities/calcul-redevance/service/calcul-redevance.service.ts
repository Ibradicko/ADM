import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICalculRedevance, NewCalculRedevance } from '../calcul-redevance.model';

export type PartialUpdateCalculRedevance = Partial<ICalculRedevance> & Pick<ICalculRedevance, 'id'>;

export interface GenerateCalculRedevanceRequest {
  periodeDebut: string;
  periodeFin: string;
  boutiqueId: number;
  locataireId: number;
}

type RestOf<T extends ICalculRedevance | NewCalculRedevance> = Omit<T, 'periodeDebut' | 'periodeFin' | 'dateCalcul'> & {
  periodeDebut?: string | null;
  periodeFin?: string | null;
  dateCalcul?: string | null;
};

export type RestCalculRedevance = RestOf<ICalculRedevance>;

export type NewRestCalculRedevance = RestOf<NewCalculRedevance>;

export type PartialUpdateRestCalculRedevance = RestOf<PartialUpdateCalculRedevance>;

@Injectable()
export class CalculRedevancesService {
  readonly calculRedevancesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly calculRedevancesResource = httpResource<RestCalculRedevance[]>(() => {
    const params = this.calculRedevancesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of calculRedevance that have been fetched. It is updated when the calculRedevancesResource emits a new value.
   * In case of error while fetching the calculRedevances, the signal is set to an empty array.
   */
  readonly calculRedevances = computed(() =>
    (this.calculRedevancesResource.hasValue() ? this.calculRedevancesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/calcul-redevances');

  protected convertValueFromServer(restCalculRedevance: RestCalculRedevance): ICalculRedevance {
    return {
      ...restCalculRedevance,
      periodeDebut: restCalculRedevance.periodeDebut ? dayjs(restCalculRedevance.periodeDebut) : undefined,
      periodeFin: restCalculRedevance.periodeFin ? dayjs(restCalculRedevance.periodeFin) : undefined,
      dateCalcul: restCalculRedevance.dateCalcul ? dayjs(restCalculRedevance.dateCalcul) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CalculRedevanceService extends CalculRedevancesService {
  protected readonly http = inject(HttpClient);

  create(calculRedevance: NewCalculRedevance): Observable<ICalculRedevance> {
    const copy = this.convertValueFromClient(calculRedevance);
    return this.http.post<RestCalculRedevance>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  generate(request: GenerateCalculRedevanceRequest): Observable<ICalculRedevance> {
    return this.http
      .post<RestCalculRedevance>(`${this.resourceUrl}/generate`, request)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(calculRedevance: ICalculRedevance): Observable<ICalculRedevance> {
    const copy = this.convertValueFromClient(calculRedevance);
    return this.http
      .put<RestCalculRedevance>(`${this.resourceUrl}/${encodeURIComponent(this.getCalculRedevanceIdentifier(calculRedevance))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(calculRedevance: PartialUpdateCalculRedevance): Observable<ICalculRedevance> {
    const copy = this.convertValueFromClient(calculRedevance);
    return this.http
      .patch<RestCalculRedevance>(`${this.resourceUrl}/${encodeURIComponent(this.getCalculRedevanceIdentifier(calculRedevance))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICalculRedevance> {
    return this.http
      .get<RestCalculRedevance>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICalculRedevance[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCalculRedevance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCalculRedevanceIdentifier(calculRedevance: Pick<ICalculRedevance, 'id'>): number {
    return calculRedevance.id;
  }

  compareCalculRedevance(o1: Pick<ICalculRedevance, 'id'> | null, o2: Pick<ICalculRedevance, 'id'> | null): boolean {
    return o1 && o2 ? this.getCalculRedevanceIdentifier(o1) === this.getCalculRedevanceIdentifier(o2) : o1 === o2;
  }

  addCalculRedevanceToCollectionIfMissing<Type extends Pick<ICalculRedevance, 'id'>>(
    calculRedevanceCollection: Type[],
    ...calculRedevancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const calculRedevances: Type[] = calculRedevancesToCheck.filter(isPresent);
    if (calculRedevances.length > 0) {
      const calculRedevanceCollectionIdentifiers = calculRedevanceCollection.map(calculRedevanceItem =>
        this.getCalculRedevanceIdentifier(calculRedevanceItem),
      );
      const calculRedevancesToAdd = calculRedevances.filter(calculRedevanceItem => {
        const calculRedevanceIdentifier = this.getCalculRedevanceIdentifier(calculRedevanceItem);
        if (calculRedevanceCollectionIdentifiers.includes(calculRedevanceIdentifier)) {
          return false;
        }
        calculRedevanceCollectionIdentifiers.push(calculRedevanceIdentifier);
        return true;
      });
      return [...calculRedevancesToAdd, ...calculRedevanceCollection];
    }
    return calculRedevanceCollection;
  }

  protected convertValueFromClient<T extends ICalculRedevance | NewCalculRedevance | PartialUpdateCalculRedevance>(
    calculRedevance: T,
  ): RestOf<T> {
    return {
      ...calculRedevance,
      periodeDebut: calculRedevance.periodeDebut?.format(DATE_FORMAT) ?? null,
      periodeFin: calculRedevance.periodeFin?.format(DATE_FORMAT) ?? null,
      dateCalcul: calculRedevance.dateCalcul?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCalculRedevance): ICalculRedevance {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCalculRedevance[]): ICalculRedevance[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
