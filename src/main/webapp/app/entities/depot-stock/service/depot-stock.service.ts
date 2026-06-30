import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDepotStock, NewDepotStock } from '../depot-stock.model';

export type PartialUpdateDepotStock = Partial<IDepotStock> & Pick<IDepotStock, 'id'>;

@Injectable()
export class DepotStocksService {
  readonly depotStocksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly depotStocksResource = httpResource<IDepotStock[]>(() => {
    const params = this.depotStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of depotStock that have been fetched. It is updated when the depotStocksResource emits a new value.
   * In case of error while fetching the depotStocks, the signal is set to an empty array.
   */
  readonly depotStocks = computed(() => (this.depotStocksResource.hasValue() ? this.depotStocksResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/depot-stocks');
}

@Injectable({ providedIn: 'root' })
export class DepotStockService extends DepotStocksService {
  protected readonly http = inject(HttpClient);

  create(depotStock: NewDepotStock): Observable<IDepotStock> {
    return this.http.post<IDepotStock>(this.resourceUrl, depotStock);
  }

  update(depotStock: IDepotStock): Observable<IDepotStock> {
    return this.http.put<IDepotStock>(`${this.resourceUrl}/${encodeURIComponent(this.getDepotStockIdentifier(depotStock))}`, depotStock);
  }

  partialUpdate(depotStock: PartialUpdateDepotStock): Observable<IDepotStock> {
    return this.http.patch<IDepotStock>(`${this.resourceUrl}/${encodeURIComponent(this.getDepotStockIdentifier(depotStock))}`, depotStock);
  }

  find(id: number): Observable<IDepotStock> {
    return this.http.get<IDepotStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IDepotStock[]>> {
    const options = createRequestOption(req);
    return this.http.get<IDepotStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDepotStockIdentifier(depotStock: Pick<IDepotStock, 'id'>): number {
    return depotStock.id;
  }

  compareDepotStock(o1: Pick<IDepotStock, 'id'> | null, o2: Pick<IDepotStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getDepotStockIdentifier(o1) === this.getDepotStockIdentifier(o2) : o1 === o2;
  }

  addDepotStockToCollectionIfMissing<Type extends Pick<IDepotStock, 'id'>>(
    depotStockCollection: Type[],
    ...depotStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const depotStocks: Type[] = depotStocksToCheck.filter(isPresent);
    if (depotStocks.length > 0) {
      const depotStockCollectionIdentifiers = depotStockCollection.map(depotStockItem => this.getDepotStockIdentifier(depotStockItem));
      const depotStocksToAdd = depotStocks.filter(depotStockItem => {
        const depotStockIdentifier = this.getDepotStockIdentifier(depotStockItem);
        if (depotStockCollectionIdentifiers.includes(depotStockIdentifier)) {
          return false;
        }
        depotStockCollectionIdentifiers.push(depotStockIdentifier);
        return true;
      });
      return [...depotStocksToAdd, ...depotStockCollection];
    }
    return depotStockCollection;
  }
}
