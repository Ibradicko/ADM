import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IInventaireStock, NewInventaireStock } from '../inventaire-stock.model';

export type PartialUpdateInventaireStock = Partial<IInventaireStock> & Pick<IInventaireStock, 'id'>;

type RestOf<T extends IInventaireStock | NewInventaireStock> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

export type RestInventaireStock = RestOf<IInventaireStock>;

export type NewRestInventaireStock = RestOf<NewInventaireStock>;

export type PartialUpdateRestInventaireStock = RestOf<PartialUpdateInventaireStock>;

@Injectable()
export class InventaireStocksService {
  readonly inventaireStocksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly inventaireStocksResource = httpResource<RestInventaireStock[]>(() => {
    const params = this.inventaireStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of inventaireStock that have been fetched. It is updated when the inventaireStocksResource emits a new value.
   * In case of error while fetching the inventaireStocks, the signal is set to an empty array.
   */
  readonly inventaireStocks = computed(() =>
    (this.inventaireStocksResource.hasValue() ? this.inventaireStocksResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/inventaire-stocks');

  protected convertValueFromServer(restInventaireStock: RestInventaireStock): IInventaireStock {
    return {
      ...restInventaireStock,
      dateDebut: restInventaireStock.dateDebut ? dayjs(restInventaireStock.dateDebut) : undefined,
      dateFin: restInventaireStock.dateFin ? dayjs(restInventaireStock.dateFin) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class InventaireStockService extends InventaireStocksService {
  protected readonly http = inject(HttpClient);

  create(inventaireStock: NewInventaireStock): Observable<IInventaireStock> {
    const copy = this.convertValueFromClient(inventaireStock);
    return this.http.post<RestInventaireStock>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(inventaireStock: IInventaireStock): Observable<IInventaireStock> {
    const copy = this.convertValueFromClient(inventaireStock);
    return this.http
      .put<RestInventaireStock>(`${this.resourceUrl}/${encodeURIComponent(this.getInventaireStockIdentifier(inventaireStock))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(inventaireStock: PartialUpdateInventaireStock): Observable<IInventaireStock> {
    const copy = this.convertValueFromClient(inventaireStock);
    return this.http
      .patch<RestInventaireStock>(`${this.resourceUrl}/${encodeURIComponent(this.getInventaireStockIdentifier(inventaireStock))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IInventaireStock> {
    return this.http
      .get<RestInventaireStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IInventaireStock[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInventaireStock[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getInventaireStockIdentifier(inventaireStock: Pick<IInventaireStock, 'id'>): number {
    return inventaireStock.id;
  }

  compareInventaireStock(o1: Pick<IInventaireStock, 'id'> | null, o2: Pick<IInventaireStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getInventaireStockIdentifier(o1) === this.getInventaireStockIdentifier(o2) : o1 === o2;
  }

  addInventaireStockToCollectionIfMissing<Type extends Pick<IInventaireStock, 'id'>>(
    inventaireStockCollection: Type[],
    ...inventaireStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inventaireStocks: Type[] = inventaireStocksToCheck.filter(isPresent);
    if (inventaireStocks.length > 0) {
      const inventaireStockCollectionIdentifiers = inventaireStockCollection.map(inventaireStockItem =>
        this.getInventaireStockIdentifier(inventaireStockItem),
      );
      const inventaireStocksToAdd = inventaireStocks.filter(inventaireStockItem => {
        const inventaireStockIdentifier = this.getInventaireStockIdentifier(inventaireStockItem);
        if (inventaireStockCollectionIdentifiers.includes(inventaireStockIdentifier)) {
          return false;
        }
        inventaireStockCollectionIdentifiers.push(inventaireStockIdentifier);
        return true;
      });
      return [...inventaireStocksToAdd, ...inventaireStockCollection];
    }
    return inventaireStockCollection;
  }

  protected convertValueFromClient<T extends IInventaireStock | NewInventaireStock | PartialUpdateInventaireStock>(
    inventaireStock: T,
  ): RestOf<T> {
    return {
      ...inventaireStock,
      dateDebut: inventaireStock.dateDebut?.toJSON() ?? null,
      dateFin: inventaireStock.dateFin?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestInventaireStock): IInventaireStock {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestInventaireStock[]): IInventaireStock[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
