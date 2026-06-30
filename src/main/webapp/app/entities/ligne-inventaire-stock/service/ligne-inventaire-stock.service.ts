import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneInventaireStock, NewLigneInventaireStock } from '../ligne-inventaire-stock.model';

export type PartialUpdateLigneInventaireStock = Partial<ILigneInventaireStock> & Pick<ILigneInventaireStock, 'id'>;

@Injectable()
export class LigneInventaireStocksService {
  readonly ligneInventaireStocksParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly ligneInventaireStocksResource = httpResource<ILigneInventaireStock[]>(() => {
    const params = this.ligneInventaireStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneInventaireStock that have been fetched. It is updated when the ligneInventaireStocksResource emits a new value.
   * In case of error while fetching the ligneInventaireStocks, the signal is set to an empty array.
   */
  readonly ligneInventaireStocks = computed(() =>
    this.ligneInventaireStocksResource.hasValue() ? this.ligneInventaireStocksResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-inventaire-stocks');
}

@Injectable({ providedIn: 'root' })
export class LigneInventaireStockService extends LigneInventaireStocksService {
  protected readonly http = inject(HttpClient);

  create(ligneInventaireStock: NewLigneInventaireStock): Observable<ILigneInventaireStock> {
    return this.http.post<ILigneInventaireStock>(this.resourceUrl, ligneInventaireStock);
  }

  update(ligneInventaireStock: ILigneInventaireStock): Observable<ILigneInventaireStock> {
    return this.http.put<ILigneInventaireStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneInventaireStockIdentifier(ligneInventaireStock))}`,
      ligneInventaireStock,
    );
  }

  partialUpdate(ligneInventaireStock: PartialUpdateLigneInventaireStock): Observable<ILigneInventaireStock> {
    return this.http.patch<ILigneInventaireStock>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneInventaireStockIdentifier(ligneInventaireStock))}`,
      ligneInventaireStock,
    );
  }

  find(id: number): Observable<ILigneInventaireStock> {
    return this.http.get<ILigneInventaireStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneInventaireStock[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneInventaireStock[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneInventaireStockIdentifier(ligneInventaireStock: Pick<ILigneInventaireStock, 'id'>): number {
    return ligneInventaireStock.id;
  }

  compareLigneInventaireStock(o1: Pick<ILigneInventaireStock, 'id'> | null, o2: Pick<ILigneInventaireStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneInventaireStockIdentifier(o1) === this.getLigneInventaireStockIdentifier(o2) : o1 === o2;
  }

  addLigneInventaireStockToCollectionIfMissing<Type extends Pick<ILigneInventaireStock, 'id'>>(
    ligneInventaireStockCollection: Type[],
    ...ligneInventaireStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneInventaireStocks: Type[] = ligneInventaireStocksToCheck.filter(isPresent);
    if (ligneInventaireStocks.length > 0) {
      const ligneInventaireStockCollectionIdentifiers = ligneInventaireStockCollection.map(ligneInventaireStockItem =>
        this.getLigneInventaireStockIdentifier(ligneInventaireStockItem),
      );
      const ligneInventaireStocksToAdd = ligneInventaireStocks.filter(ligneInventaireStockItem => {
        const ligneInventaireStockIdentifier = this.getLigneInventaireStockIdentifier(ligneInventaireStockItem);
        if (ligneInventaireStockCollectionIdentifiers.includes(ligneInventaireStockIdentifier)) {
          return false;
        }
        ligneInventaireStockCollectionIdentifiers.push(ligneInventaireStockIdentifier);
        return true;
      });
      return [...ligneInventaireStocksToAdd, ...ligneInventaireStockCollection];
    }
    return ligneInventaireStockCollection;
  }
}
