import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IReceptionProduit, NewReceptionProduit } from '../reception-produit.model';

export type PartialUpdateReceptionProduit = Partial<IReceptionProduit> & Pick<IReceptionProduit, 'id'>;

type RestOf<T extends IReceptionProduit | NewReceptionProduit> = Omit<T, 'dateReception'> & {
  dateReception?: string | null;
};

export type RestReceptionProduit = RestOf<IReceptionProduit>;

export type NewRestReceptionProduit = RestOf<NewReceptionProduit>;

export type PartialUpdateRestReceptionProduit = RestOf<PartialUpdateReceptionProduit>;

@Injectable()
export class ReceptionProduitsService {
  readonly receptionProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly receptionProduitsResource = httpResource<RestReceptionProduit[]>(() => {
    const params = this.receptionProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of receptionProduit that have been fetched. It is updated when the receptionProduitsResource emits a new value.
   * In case of error while fetching the receptionProduits, the signal is set to an empty array.
   */
  readonly receptionProduits = computed(() =>
    (this.receptionProduitsResource.hasValue() ? this.receptionProduitsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/reception-produits');

  protected convertValueFromServer(restReceptionProduit: RestReceptionProduit): IReceptionProduit {
    return {
      ...restReceptionProduit,
      dateReception: restReceptionProduit.dateReception ? dayjs(restReceptionProduit.dateReception) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ReceptionProduitService extends ReceptionProduitsService {
  protected readonly http = inject(HttpClient);

  create(receptionProduit: NewReceptionProduit): Observable<IReceptionProduit> {
    const copy = this.convertValueFromClient(receptionProduit);
    return this.http.post<RestReceptionProduit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(receptionProduit: IReceptionProduit): Observable<IReceptionProduit> {
    const copy = this.convertValueFromClient(receptionProduit);
    return this.http
      .put<RestReceptionProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getReceptionProduitIdentifier(receptionProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(receptionProduit: PartialUpdateReceptionProduit): Observable<IReceptionProduit> {
    const copy = this.convertValueFromClient(receptionProduit);
    return this.http
      .patch<RestReceptionProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getReceptionProduitIdentifier(receptionProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IReceptionProduit> {
    return this.http
      .get<RestReceptionProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IReceptionProduit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReceptionProduit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getReceptionProduitIdentifier(receptionProduit: Pick<IReceptionProduit, 'id'>): number {
    return receptionProduit.id;
  }

  compareReceptionProduit(o1: Pick<IReceptionProduit, 'id'> | null, o2: Pick<IReceptionProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getReceptionProduitIdentifier(o1) === this.getReceptionProduitIdentifier(o2) : o1 === o2;
  }

  addReceptionProduitToCollectionIfMissing<Type extends Pick<IReceptionProduit, 'id'>>(
    receptionProduitCollection: Type[],
    ...receptionProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const receptionProduits: Type[] = receptionProduitsToCheck.filter(isPresent);
    if (receptionProduits.length > 0) {
      const receptionProduitCollectionIdentifiers = receptionProduitCollection.map(receptionProduitItem =>
        this.getReceptionProduitIdentifier(receptionProduitItem),
      );
      const receptionProduitsToAdd = receptionProduits.filter(receptionProduitItem => {
        const receptionProduitIdentifier = this.getReceptionProduitIdentifier(receptionProduitItem);
        if (receptionProduitCollectionIdentifiers.includes(receptionProduitIdentifier)) {
          return false;
        }
        receptionProduitCollectionIdentifiers.push(receptionProduitIdentifier);
        return true;
      });
      return [...receptionProduitsToAdd, ...receptionProduitCollection];
    }
    return receptionProduitCollection;
  }

  protected convertValueFromClient<T extends IReceptionProduit | NewReceptionProduit | PartialUpdateReceptionProduit>(
    receptionProduit: T,
  ): RestOf<T> {
    return {
      ...receptionProduit,
      dateReception: receptionProduit.dateReception?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestReceptionProduit): IReceptionProduit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestReceptionProduit[]): IReceptionProduit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
