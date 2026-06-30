import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IOperationCorrectiveVente, NewOperationCorrectiveVente } from '../operation-corrective-vente.model';

export type PartialUpdateOperationCorrectiveVente = Partial<IOperationCorrectiveVente> & Pick<IOperationCorrectiveVente, 'id'>;

type RestOf<T extends IOperationCorrectiveVente | NewOperationCorrectiveVente> = Omit<T, 'dateOperation'> & {
  dateOperation?: string | null;
};

export type RestOperationCorrectiveVente = RestOf<IOperationCorrectiveVente>;

export type NewRestOperationCorrectiveVente = RestOf<NewOperationCorrectiveVente>;

export type PartialUpdateRestOperationCorrectiveVente = RestOf<PartialUpdateOperationCorrectiveVente>;

@Injectable()
export class OperationCorrectiveVentesService {
  readonly operationCorrectiveVentesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly operationCorrectiveVentesResource = httpResource<RestOperationCorrectiveVente[]>(() => {
    const params = this.operationCorrectiveVentesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of operationCorrectiveVente that have been fetched. It is updated when the operationCorrectiveVentesResource emits a new value.
   * In case of error while fetching the operationCorrectiveVentes, the signal is set to an empty array.
   */
  readonly operationCorrectiveVentes = computed(() =>
    (this.operationCorrectiveVentesResource.hasValue() ? this.operationCorrectiveVentesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/operation-corrective-ventes');

  protected convertValueFromServer(restOperationCorrectiveVente: RestOperationCorrectiveVente): IOperationCorrectiveVente {
    return {
      ...restOperationCorrectiveVente,
      dateOperation: restOperationCorrectiveVente.dateOperation ? dayjs(restOperationCorrectiveVente.dateOperation) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class OperationCorrectiveVenteService extends OperationCorrectiveVentesService {
  protected readonly http = inject(HttpClient);

  create(operationCorrectiveVente: NewOperationCorrectiveVente): Observable<IOperationCorrectiveVente> {
    const copy = this.convertValueFromClient(operationCorrectiveVente);
    return this.http.post<RestOperationCorrectiveVente>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(operationCorrectiveVente: IOperationCorrectiveVente): Observable<IOperationCorrectiveVente> {
    const copy = this.convertValueFromClient(operationCorrectiveVente);
    return this.http
      .put<RestOperationCorrectiveVente>(
        `${this.resourceUrl}/${encodeURIComponent(this.getOperationCorrectiveVenteIdentifier(operationCorrectiveVente))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(operationCorrectiveVente: PartialUpdateOperationCorrectiveVente): Observable<IOperationCorrectiveVente> {
    const copy = this.convertValueFromClient(operationCorrectiveVente);
    return this.http
      .patch<RestOperationCorrectiveVente>(
        `${this.resourceUrl}/${encodeURIComponent(this.getOperationCorrectiveVenteIdentifier(operationCorrectiveVente))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IOperationCorrectiveVente> {
    return this.http
      .get<RestOperationCorrectiveVente>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IOperationCorrectiveVente[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOperationCorrectiveVente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getOperationCorrectiveVenteIdentifier(operationCorrectiveVente: Pick<IOperationCorrectiveVente, 'id'>): number {
    return operationCorrectiveVente.id;
  }

  compareOperationCorrectiveVente(
    o1: Pick<IOperationCorrectiveVente, 'id'> | null,
    o2: Pick<IOperationCorrectiveVente, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getOperationCorrectiveVenteIdentifier(o1) === this.getOperationCorrectiveVenteIdentifier(o2) : o1 === o2;
  }

  addOperationCorrectiveVenteToCollectionIfMissing<Type extends Pick<IOperationCorrectiveVente, 'id'>>(
    operationCorrectiveVenteCollection: Type[],
    ...operationCorrectiveVentesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const operationCorrectiveVentes: Type[] = operationCorrectiveVentesToCheck.filter(isPresent);
    if (operationCorrectiveVentes.length > 0) {
      const operationCorrectiveVenteCollectionIdentifiers = operationCorrectiveVenteCollection.map(operationCorrectiveVenteItem =>
        this.getOperationCorrectiveVenteIdentifier(operationCorrectiveVenteItem),
      );
      const operationCorrectiveVentesToAdd = operationCorrectiveVentes.filter(operationCorrectiveVenteItem => {
        const operationCorrectiveVenteIdentifier = this.getOperationCorrectiveVenteIdentifier(operationCorrectiveVenteItem);
        if (operationCorrectiveVenteCollectionIdentifiers.includes(operationCorrectiveVenteIdentifier)) {
          return false;
        }
        operationCorrectiveVenteCollectionIdentifiers.push(operationCorrectiveVenteIdentifier);
        return true;
      });
      return [...operationCorrectiveVentesToAdd, ...operationCorrectiveVenteCollection];
    }
    return operationCorrectiveVenteCollection;
  }

  protected convertValueFromClient<
    T extends IOperationCorrectiveVente | NewOperationCorrectiveVente | PartialUpdateOperationCorrectiveVente,
  >(operationCorrectiveVente: T): RestOf<T> {
    return {
      ...operationCorrectiveVente,
      dateOperation: operationCorrectiveVente.dateOperation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestOperationCorrectiveVente): IOperationCorrectiveVente {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestOperationCorrectiveVente[]): IOperationCorrectiveVente[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
