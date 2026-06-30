import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILocataire, NewLocataire } from '../locataire.model';

export type PartialUpdateLocataire = Partial<ILocataire> & Pick<ILocataire, 'id'>;

type RestOf<T extends ILocataire | NewLocataire> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

export type RestLocataire = RestOf<ILocataire>;

export type NewRestLocataire = RestOf<NewLocataire>;

export type PartialUpdateRestLocataire = RestOf<PartialUpdateLocataire>;

export interface ResetLocatairePasswordResponse {
  login: string;
  temporaryPassword: string;
}

@Injectable()
export class LocatairesService {
  readonly locatairesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly locatairesResource = httpResource<RestLocataire[]>(() => {
    const params = this.locatairesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of locataire that have been fetched. It is updated when the locatairesResource emits a new value.
   * In case of error while fetching the locataires, the signal is set to an empty array.
   */
  readonly locataires = computed(() =>
    (this.locatairesResource.hasValue() ? this.locatairesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/locataires');

  protected convertValueFromServer(restLocataire: RestLocataire): ILocataire {
    return {
      ...restLocataire,
      dateCreation: restLocataire.dateCreation ? dayjs(restLocataire.dateCreation) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class LocataireService extends LocatairesService {
  protected readonly http = inject(HttpClient);

  create(locataire: NewLocataire): Observable<ILocataire> {
    const copy = this.convertValueFromClient(locataire);
    return this.http.post<RestLocataire>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(locataire: ILocataire): Observable<ILocataire> {
    const copy = this.convertValueFromClient(locataire);
    return this.http
      .put<RestLocataire>(`${this.resourceUrl}/${encodeURIComponent(this.getLocataireIdentifier(locataire))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(locataire: PartialUpdateLocataire): Observable<ILocataire> {
    const copy = this.convertValueFromClient(locataire);
    return this.http
      .patch<RestLocataire>(`${this.resourceUrl}/${encodeURIComponent(this.getLocataireIdentifier(locataire))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ILocataire> {
    return this.http
      .get<RestLocataire>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ILocataire[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLocataire[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  reinitialiserMotDePasse(id: number): Observable<ResetLocatairePasswordResponse> {
    return this.http.post<ResetLocatairePasswordResponse>(`${this.resourceUrl}/${encodeURIComponent(id)}/reinitialiser-mot-de-passe`, {});
  }

  getLocataireIdentifier(locataire: Pick<ILocataire, 'id'>): number {
    return locataire.id;
  }

  compareLocataire(o1: Pick<ILocataire, 'id'> | null, o2: Pick<ILocataire, 'id'> | null): boolean {
    return o1 && o2 ? this.getLocataireIdentifier(o1) === this.getLocataireIdentifier(o2) : o1 === o2;
  }

  addLocataireToCollectionIfMissing<Type extends Pick<ILocataire, 'id'>>(
    locataireCollection: Type[],
    ...locatairesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const locataires: Type[] = locatairesToCheck.filter(isPresent);
    if (locataires.length > 0) {
      const locataireCollectionIdentifiers = locataireCollection.map(locataireItem => this.getLocataireIdentifier(locataireItem));
      const locatairesToAdd = locataires.filter(locataireItem => {
        const locataireIdentifier = this.getLocataireIdentifier(locataireItem);
        if (locataireCollectionIdentifiers.includes(locataireIdentifier)) {
          return false;
        }
        locataireCollectionIdentifiers.push(locataireIdentifier);
        return true;
      });
      return [...locatairesToAdd, ...locataireCollection];
    }
    return locataireCollection;
  }

  protected convertValueFromClient<T extends ILocataire | NewLocataire | PartialUpdateLocataire>(locataire: T): RestOf<T> {
    return {
      ...locataire,
      dateCreation: locataire.dateCreation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestLocataire): ILocataire {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestLocataire[]): ILocataire[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
