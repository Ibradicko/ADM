import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IStockProduit, NewStockProduit } from '../stock-produit.model';

export type PartialUpdateStockProduit = Partial<IStockProduit> & Pick<IStockProduit, 'id'>;

type RestOf<T extends IStockProduit | NewStockProduit> = Omit<T, 'dateDernierMouvement'> & {
  dateDernierMouvement?: string | null;
};

export type RestStockProduit = RestOf<IStockProduit>;

export type NewRestStockProduit = RestOf<NewStockProduit>;

export type PartialUpdateRestStockProduit = RestOf<PartialUpdateStockProduit>;

@Injectable()
export class StockProduitsService {
  readonly stockProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly stockProduitsResource = httpResource<RestStockProduit[]>(() => {
    const params = this.stockProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of stockProduit that have been fetched. It is updated when the stockProduitsResource emits a new value.
   * In case of error while fetching the stockProduits, the signal is set to an empty array.
   */
  readonly stockProduits = computed(() =>
    (this.stockProduitsResource.hasValue() ? this.stockProduitsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-produits');

  protected convertValueFromServer(restStockProduit: RestStockProduit): IStockProduit {
    return {
      ...restStockProduit,
      dateDernierMouvement: restStockProduit.dateDernierMouvement ? dayjs(restStockProduit.dateDernierMouvement) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class StockProduitService extends StockProduitsService {
  protected readonly http = inject(HttpClient);

  create(stockProduit: NewStockProduit): Observable<IStockProduit> {
    const copy = this.convertValueFromClient(stockProduit);
    return this.http.post<RestStockProduit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockProduit: IStockProduit): Observable<IStockProduit> {
    const copy = this.convertValueFromClient(stockProduit);
    return this.http
      .put<RestStockProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getStockProduitIdentifier(stockProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockProduit: PartialUpdateStockProduit): Observable<IStockProduit> {
    const copy = this.convertValueFromClient(stockProduit);
    return this.http
      .patch<RestStockProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getStockProduitIdentifier(stockProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IStockProduit> {
    return this.http
      .get<RestStockProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IStockProduit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockProduit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getStockProduitIdentifier(stockProduit: Pick<IStockProduit, 'id'>): number {
    return stockProduit.id;
  }

  compareStockProduit(o1: Pick<IStockProduit, 'id'> | null, o2: Pick<IStockProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockProduitIdentifier(o1) === this.getStockProduitIdentifier(o2) : o1 === o2;
  }

  addStockProduitToCollectionIfMissing<Type extends Pick<IStockProduit, 'id'>>(
    stockProduitCollection: Type[],
    ...stockProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockProduits: Type[] = stockProduitsToCheck.filter(isPresent);
    if (stockProduits.length > 0) {
      const stockProduitCollectionIdentifiers = stockProduitCollection.map(stockProduitItem =>
        this.getStockProduitIdentifier(stockProduitItem),
      );
      const stockProduitsToAdd = stockProduits.filter(stockProduitItem => {
        const stockProduitIdentifier = this.getStockProduitIdentifier(stockProduitItem);
        if (stockProduitCollectionIdentifiers.includes(stockProduitIdentifier)) {
          return false;
        }
        stockProduitCollectionIdentifiers.push(stockProduitIdentifier);
        return true;
      });
      return [...stockProduitsToAdd, ...stockProduitCollection];
    }
    return stockProduitCollection;
  }

  protected convertValueFromClient<T extends IStockProduit | NewStockProduit | PartialUpdateStockProduit>(stockProduit: T): RestOf<T> {
    return {
      ...stockProduit,
      dateDernierMouvement: stockProduit.dateDernierMouvement?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestStockProduit): IStockProduit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestStockProduit[]): IStockProduit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
