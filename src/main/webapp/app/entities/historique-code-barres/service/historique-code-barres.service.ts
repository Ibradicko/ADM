import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IHistoriqueCodeBarres, NewHistoriqueCodeBarres } from '../historique-code-barres.model';

export type PartialUpdateHistoriqueCodeBarres = Partial<IHistoriqueCodeBarres> & Pick<IHistoriqueCodeBarres, 'id'>;

type RestOf<T extends IHistoriqueCodeBarres | NewHistoriqueCodeBarres> = Omit<T, 'dateChangement'> & {
  dateChangement?: string | null;
};

export type RestHistoriqueCodeBarres = RestOf<IHistoriqueCodeBarres>;

export type NewRestHistoriqueCodeBarres = RestOf<NewHistoriqueCodeBarres>;

export type PartialUpdateRestHistoriqueCodeBarres = RestOf<PartialUpdateHistoriqueCodeBarres>;

@Injectable()
export class HistoriqueCodeBarresesService {
  readonly historiqueCodeBarresesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly historiqueCodeBarresesResource = httpResource<RestHistoriqueCodeBarres[]>(() => {
    const params = this.historiqueCodeBarresesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of historiqueCodeBarres that have been fetched. It is updated when the historiqueCodeBarresesResource emits a new value.
   * In case of error while fetching the historiqueCodeBarreses, the signal is set to an empty array.
   */
  readonly historiqueCodeBarreses = computed(() =>
    (this.historiqueCodeBarresesResource.hasValue() ? this.historiqueCodeBarresesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/historique-code-barres');

  protected convertValueFromServer(restHistoriqueCodeBarres: RestHistoriqueCodeBarres): IHistoriqueCodeBarres {
    return {
      ...restHistoriqueCodeBarres,
      dateChangement: restHistoriqueCodeBarres.dateChangement ? dayjs(restHistoriqueCodeBarres.dateChangement) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class HistoriqueCodeBarresService extends HistoriqueCodeBarresesService {
  protected readonly http = inject(HttpClient);

  create(historiqueCodeBarres: NewHistoriqueCodeBarres): Observable<IHistoriqueCodeBarres> {
    const copy = this.convertValueFromClient(historiqueCodeBarres);
    return this.http.post<RestHistoriqueCodeBarres>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(historiqueCodeBarres: IHistoriqueCodeBarres): Observable<IHistoriqueCodeBarres> {
    const copy = this.convertValueFromClient(historiqueCodeBarres);
    return this.http
      .put<RestHistoriqueCodeBarres>(
        `${this.resourceUrl}/${encodeURIComponent(this.getHistoriqueCodeBarresIdentifier(historiqueCodeBarres))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(historiqueCodeBarres: PartialUpdateHistoriqueCodeBarres): Observable<IHistoriqueCodeBarres> {
    const copy = this.convertValueFromClient(historiqueCodeBarres);
    return this.http
      .patch<RestHistoriqueCodeBarres>(
        `${this.resourceUrl}/${encodeURIComponent(this.getHistoriqueCodeBarresIdentifier(historiqueCodeBarres))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IHistoriqueCodeBarres> {
    return this.http
      .get<RestHistoriqueCodeBarres>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IHistoriqueCodeBarres[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestHistoriqueCodeBarres[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getHistoriqueCodeBarresIdentifier(historiqueCodeBarres: Pick<IHistoriqueCodeBarres, 'id'>): number {
    return historiqueCodeBarres.id;
  }

  compareHistoriqueCodeBarres(o1: Pick<IHistoriqueCodeBarres, 'id'> | null, o2: Pick<IHistoriqueCodeBarres, 'id'> | null): boolean {
    return o1 && o2 ? this.getHistoriqueCodeBarresIdentifier(o1) === this.getHistoriqueCodeBarresIdentifier(o2) : o1 === o2;
  }

  addHistoriqueCodeBarresToCollectionIfMissing<Type extends Pick<IHistoriqueCodeBarres, 'id'>>(
    historiqueCodeBarresCollection: Type[],
    ...historiqueCodeBarresesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const historiqueCodeBarreses: Type[] = historiqueCodeBarresesToCheck.filter(isPresent);
    if (historiqueCodeBarreses.length > 0) {
      const historiqueCodeBarresCollectionIdentifiers = historiqueCodeBarresCollection.map(historiqueCodeBarresItem =>
        this.getHistoriqueCodeBarresIdentifier(historiqueCodeBarresItem),
      );
      const historiqueCodeBarresesToAdd = historiqueCodeBarreses.filter(historiqueCodeBarresItem => {
        const historiqueCodeBarresIdentifier = this.getHistoriqueCodeBarresIdentifier(historiqueCodeBarresItem);
        if (historiqueCodeBarresCollectionIdentifiers.includes(historiqueCodeBarresIdentifier)) {
          return false;
        }
        historiqueCodeBarresCollectionIdentifiers.push(historiqueCodeBarresIdentifier);
        return true;
      });
      return [...historiqueCodeBarresesToAdd, ...historiqueCodeBarresCollection];
    }
    return historiqueCodeBarresCollection;
  }

  protected convertValueFromClient<T extends IHistoriqueCodeBarres | NewHistoriqueCodeBarres | PartialUpdateHistoriqueCodeBarres>(
    historiqueCodeBarres: T,
  ): RestOf<T> {
    return {
      ...historiqueCodeBarres,
      dateChangement: historiqueCodeBarres.dateChangement?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestHistoriqueCodeBarres): IHistoriqueCodeBarres {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestHistoriqueCodeBarres[]): IHistoriqueCodeBarres[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
