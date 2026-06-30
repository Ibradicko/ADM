import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneTransfertStock, NewLigneTransfertStock } from '../ligne-transfert-stock.model';

export type PartialUpdateLigneTransfertStock = Partial<ILigneTransfertStock> & Pick<ILigneTransfertStock, 'id'>;

@Injectable()
export class LigneTransfertStocksService {
  readonly ligneTransfertStocksParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly ligneTransfertStocksResource = httpResource<ILigneTransfertStock[]>(() => {
    const params = this.ligneTransfertStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneTransfertStock that have been fetched. It is updated when the ligneTransfertStocksResource emits a new value.
   * In case of error while fetching the ligneTransfertStocks, the signal is set to an empty array.
   */
  readonly ligneTransfertStocks = computed(() =>
    this.ligneTransfertStocksResource.hasValue() ? this.ligneTransfertStocksResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-transfert-stocks');
}

@Injectable({ providedIn: 'root' })
export class LigneTransfertStockService extends LigneTransfertStocksService {
  protected readonly http = inject(HttpClient);

  create(ligneTransfertStock: NewLigneTransfertStock): Observable<ILigneTransfertStock> {
    return this.http.post<ILigneTransfertStock>(this.resourceUrl, ligneTransfertStock);
  }

  update(ligneTransfertStock: ILigneTransfertStock): Observable<ILigneTransfertStock> {
    return this.http.put<ILigneTransfertStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneTransfertStockIdentifier(ligneTransfertStock))}`,
      ligneTransfertStock,
    );
  }

  partialUpdate(ligneTransfertStock: PartialUpdateLigneTransfertStock): Observable<ILigneTransfertStock> {
    return this.http.patch<ILigneTransfertStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneTransfertStockIdentifier(ligneTransfertStock))}`,
      ligneTransfertStock,
    );
  }

  find(id: number): Observable<ILigneTransfertStock> {
    return this.http.get<ILigneTransfertStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneTransfertStock[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneTransfertStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneTransfertStockIdentifier(ligneTransfertStock: Pick<ILigneTransfertStock, 'id'>): number {
    return ligneTransfertStock.id;
  }

  compareLigneTransfertStock(o1: Pick<ILigneTransfertStock, 'id'> | null, o2: Pick<ILigneTransfertStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneTransfertStockIdentifier(o1) === this.getLigneTransfertStockIdentifier(o2) : o1 === o2;
  }

  addLigneTransfertStockToCollectionIfMissing<Type extends Pick<ILigneTransfertStock, 'id'>>(
    ligneTransfertStockCollection: Type[],
    ...ligneTransfertStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneTransfertStocks: Type[] = ligneTransfertStocksToCheck.filter(isPresent);
    if (ligneTransfertStocks.length > 0) {
      const ligneTransfertStockCollectionIdentifiers = ligneTransfertStockCollection.map(ligneTransfertStockItem =>
        this.getLigneTransfertStockIdentifier(ligneTransfertStockItem),
      );
      const ligneTransfertStocksToAdd = ligneTransfertStocks.filter(ligneTransfertStockItem => {
        const ligneTransfertStockIdentifier = this.getLigneTransfertStockIdentifier(ligneTransfertStockItem);
        if (ligneTransfertStockCollectionIdentifiers.includes(ligneTransfertStockIdentifier)) {
          return false;
        }
        ligneTransfertStockCollectionIdentifiers.push(ligneTransfertStockIdentifier);
        return true;
      });
      return [...ligneTransfertStocksToAdd, ...ligneTransfertStockCollection];
    }
    return ligneTransfertStockCollection;
  }
}
