import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITransfertStock, NewTransfertStock } from '../transfert-stock.model';

export type PartialUpdateTransfertStock = Partial<ITransfertStock> & Pick<ITransfertStock, 'id'>;

type RestOf<T extends ITransfertStock | NewTransfertStock> = Omit<T, 'dateTransfert'> & {
  dateTransfert?: string | null;
};

export type RestTransfertStock = RestOf<ITransfertStock>;

export type NewRestTransfertStock = RestOf<NewTransfertStock>;

export type PartialUpdateRestTransfertStock = RestOf<PartialUpdateTransfertStock>;

@Injectable()
export class TransfertStocksService {
  readonly transfertStocksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly transfertStocksResource = httpResource<RestTransfertStock[]>(() => {
    const params = this.transfertStocksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of transfertStock that have been fetched. It is updated when the transfertStocksResource emits a new value.
   * In case of error while fetching the transfertStocks, the signal is set to an empty array.
   */
  readonly transfertStocks = computed(() =>
    (this.transfertStocksResource.hasValue() ? this.transfertStocksResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/transfert-stocks');

  protected convertValueFromServer(restTransfertStock: RestTransfertStock): ITransfertStock {
    return {
      ...restTransfertStock,
      dateTransfert: restTransfertStock.dateTransfert ? dayjs(restTransfertStock.dateTransfert) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TransfertStockService extends TransfertStocksService {
  protected readonly http = inject(HttpClient);

  create(transfertStock: NewTransfertStock): Observable<ITransfertStock> {
    const copy = this.convertValueFromClient(transfertStock);
    return this.http.post<RestTransfertStock>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(transfertStock: ITransfertStock): Observable<ITransfertStock> {
    const copy = this.convertValueFromClient(transfertStock);
    return this.http
      .put<RestTransfertStock>(`${this.resourceUrl}/${encodeURIComponent(this.getTransfertStockIdentifier(transfertStock))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(transfertStock: PartialUpdateTransfertStock): Observable<ITransfertStock> {
    const copy = this.convertValueFromClient(transfertStock);
    return this.http
      .patch<RestTransfertStock>(`${this.resourceUrl}/${encodeURIComponent(this.getTransfertStockIdentifier(transfertStock))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITransfertStock> {
    return this.http
      .get<RestTransfertStock>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITransfertStock[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTransfertStock[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTransfertStockIdentifier(transfertStock: Pick<ITransfertStock, 'id'>): number {
    return transfertStock.id;
  }

  compareTransfertStock(o1: Pick<ITransfertStock, 'id'> | null, o2: Pick<ITransfertStock, 'id'> | null): boolean {
    return o1 && o2 ? this.getTransfertStockIdentifier(o1) === this.getTransfertStockIdentifier(o2) : o1 === o2;
  }

  addTransfertStockToCollectionIfMissing<Type extends Pick<ITransfertStock, 'id'>>(
    transfertStockCollection: Type[],
    ...transfertStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const transfertStocks: Type[] = transfertStocksToCheck.filter(isPresent);
    if (transfertStocks.length > 0) {
      const transfertStockCollectionIdentifiers = transfertStockCollection.map(transfertStockItem =>
        this.getTransfertStockIdentifier(transfertStockItem),
      );
      const transfertStocksToAdd = transfertStocks.filter(transfertStockItem => {
        const transfertStockIdentifier = this.getTransfertStockIdentifier(transfertStockItem);
        if (transfertStockCollectionIdentifiers.includes(transfertStockIdentifier)) {
          return false;
        }
        transfertStockCollectionIdentifiers.push(transfertStockIdentifier);
        return true;
      });
      return [...transfertStocksToAdd, ...transfertStockCollection];
    }
    return transfertStockCollection;
  }

  protected convertValueFromClient<T extends ITransfertStock | NewTransfertStock | PartialUpdateTransfertStock>(
    transfertStock: T,
  ): RestOf<T> {
    return {
      ...transfertStock,
      dateTransfert: transfertStock.dateTransfert?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTransfertStock): ITransfertStock {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTransfertStock[]): ITransfertStock[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
