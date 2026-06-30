import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEtiquetteProduit, NewEtiquetteProduit } from '../etiquette-produit.model';

export type PartialUpdateEtiquetteProduit = Partial<IEtiquetteProduit> & Pick<IEtiquetteProduit, 'id'>;

type RestOf<T extends IEtiquetteProduit | NewEtiquetteProduit> = Omit<T, 'dateImpression'> & {
  dateImpression?: string | null;
};

export type RestEtiquetteProduit = RestOf<IEtiquetteProduit>;

export type NewRestEtiquetteProduit = RestOf<NewEtiquetteProduit>;

export type PartialUpdateRestEtiquetteProduit = RestOf<PartialUpdateEtiquetteProduit>;

@Injectable()
export class EtiquetteProduitsService {
  readonly etiquetteProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly etiquetteProduitsResource = httpResource<RestEtiquetteProduit[]>(() => {
    const params = this.etiquetteProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of etiquetteProduit that have been fetched. It is updated when the etiquetteProduitsResource emits a new value.
   * In case of error while fetching the etiquetteProduits, the signal is set to an empty array.
   */
  readonly etiquetteProduits = computed(() =>
    (this.etiquetteProduitsResource.hasValue() ? this.etiquetteProduitsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/etiquette-produits');

  protected convertValueFromServer(restEtiquetteProduit: RestEtiquetteProduit): IEtiquetteProduit {
    return {
      ...restEtiquetteProduit,
      dateImpression: restEtiquetteProduit.dateImpression ? dayjs(restEtiquetteProduit.dateImpression) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class EtiquetteProduitService extends EtiquetteProduitsService {
  protected readonly http = inject(HttpClient);

  create(etiquetteProduit: NewEtiquetteProduit): Observable<IEtiquetteProduit> {
    const copy = this.convertValueFromClient(etiquetteProduit);
    return this.http.post<RestEtiquetteProduit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(etiquetteProduit: IEtiquetteProduit): Observable<IEtiquetteProduit> {
    const copy = this.convertValueFromClient(etiquetteProduit);
    return this.http
      .put<RestEtiquetteProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getEtiquetteProduitIdentifier(etiquetteProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(etiquetteProduit: PartialUpdateEtiquetteProduit): Observable<IEtiquetteProduit> {
    const copy = this.convertValueFromClient(etiquetteProduit);
    return this.http
      .patch<RestEtiquetteProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getEtiquetteProduitIdentifier(etiquetteProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IEtiquetteProduit> {
    return this.http
      .get<RestEtiquetteProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IEtiquetteProduit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEtiquetteProduit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEtiquetteProduitIdentifier(etiquetteProduit: Pick<IEtiquetteProduit, 'id'>): number {
    return etiquetteProduit.id;
  }

  compareEtiquetteProduit(o1: Pick<IEtiquetteProduit, 'id'> | null, o2: Pick<IEtiquetteProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getEtiquetteProduitIdentifier(o1) === this.getEtiquetteProduitIdentifier(o2) : o1 === o2;
  }

  addEtiquetteProduitToCollectionIfMissing<Type extends Pick<IEtiquetteProduit, 'id'>>(
    etiquetteProduitCollection: Type[],
    ...etiquetteProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const etiquetteProduits: Type[] = etiquetteProduitsToCheck.filter(isPresent);
    if (etiquetteProduits.length > 0) {
      const etiquetteProduitCollectionIdentifiers = etiquetteProduitCollection.map(etiquetteProduitItem =>
        this.getEtiquetteProduitIdentifier(etiquetteProduitItem),
      );
      const etiquetteProduitsToAdd = etiquetteProduits.filter(etiquetteProduitItem => {
        const etiquetteProduitIdentifier = this.getEtiquetteProduitIdentifier(etiquetteProduitItem);
        if (etiquetteProduitCollectionIdentifiers.includes(etiquetteProduitIdentifier)) {
          return false;
        }
        etiquetteProduitCollectionIdentifiers.push(etiquetteProduitIdentifier);
        return true;
      });
      return [...etiquetteProduitsToAdd, ...etiquetteProduitCollection];
    }
    return etiquetteProduitCollection;
  }

  protected convertValueFromClient<T extends IEtiquetteProduit | NewEtiquetteProduit | PartialUpdateEtiquetteProduit>(
    etiquetteProduit: T,
  ): RestOf<T> {
    return {
      ...etiquetteProduit,
      dateImpression: etiquetteProduit.dateImpression?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestEtiquetteProduit): IEtiquetteProduit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestEtiquetteProduit[]): IEtiquetteProduit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
