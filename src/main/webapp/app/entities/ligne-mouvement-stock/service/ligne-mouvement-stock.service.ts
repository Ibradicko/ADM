import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneMouvementStock, NewLigneMouvementStock } from '../ligne-mouvement-stock.model';

export type PartialUpdateLigneMouvementStock = Partial<ILigneMouvementStock> & Pick<ILigneMouvementStock, 'id'>;

@Injectable()
export class LigneMouvementStocksService {
  readonly ligneMouvementStocksParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly ligneMouvementStocksResource = httpResource<ILigneMouvementStock[]>(() => {
    const params = this.ligneMouvementStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneMouvementStock that have been fetched. It is updated when the ligneMouvementStocksResource emits a new value.
   * In case of error while fetching the ligneMouvementStocks, the signal is set to an empty array.
   */
  readonly ligneMouvementStocks = computed(() =>
    this.ligneMouvementStocksResource.hasValue() ? this.ligneMouvementStocksResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-mouvement-stocks');
}

@Injectable({ providedIn: 'root' })
export class LigneMouvementStockService extends LigneMouvementStocksService {
  protected readonly http = inject(HttpClient);

  create(ligneMouvementStock: NewLigneMouvementStock): Observable<ILigneMouvementStock> {
    return this.http.post<ILigneMouvementStock>(this.resourceUrl, ligneMouvementStock);
  }

  update(ligneMouvementStock: ILigneMouvementStock): Observable<ILigneMouvementStock> {
    return this.http.put<ILigneMouvementStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneMouvementStockIdentifier(ligneMouvementStock))}`,
      ligneMouvementStock,
    );
  }

  partialUpdate(ligneMouvementStock: PartialUpdateLigneMouvementStock): Observable<ILigneMouvementStock> {
    return this.http.patch<ILigneMouvementStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneMouvementStockIdentifier(ligneMouvementStock))}`,
      ligneMouvementStock,
    );
  }

  find(id: number): Observable<ILigneMouvementStock> {
    return this.http.get<ILigneMouvementStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneMouvementStock[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneMouvementStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneMouvementStockIdentifier(ligneMouvementStock: Pick<ILigneMouvementStock, 'id'>): number {
    return ligneMouvementStock.id;
  }

  compareLigneMouvementStock(o1: Pick<ILigneMouvementStock, 'id'> | null, o2: Pick<ILigneMouvementStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneMouvementStockIdentifier(o1) === this.getLigneMouvementStockIdentifier(o2) : o1 === o2;
  }

  addLigneMouvementStockToCollectionIfMissing<Type extends Pick<ILigneMouvementStock, 'id'>>(
    ligneMouvementStockCollection: Type[],
    ...ligneMouvementStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneMouvementStocks: Type[] = ligneMouvementStocksToCheck.filter(isPresent);
    if (ligneMouvementStocks.length > 0) {
      const ligneMouvementStockCollectionIdentifiers = ligneMouvementStockCollection.map(ligneMouvementStockItem =>
        this.getLigneMouvementStockIdentifier(ligneMouvementStockItem),
      );
      const ligneMouvementStocksToAdd = ligneMouvementStocks.filter(ligneMouvementStockItem => {
        const ligneMouvementStockIdentifier = this.getLigneMouvementStockIdentifier(ligneMouvementStockItem);
        if (ligneMouvementStockCollectionIdentifiers.includes(ligneMouvementStockIdentifier)) {
          return false;
        }
        ligneMouvementStockCollectionIdentifiers.push(ligneMouvementStockIdentifier);
        return true;
      });
      return [...ligneMouvementStocksToAdd, ...ligneMouvementStockCollection];
    }
    return ligneMouvementStockCollection;
  }
}
