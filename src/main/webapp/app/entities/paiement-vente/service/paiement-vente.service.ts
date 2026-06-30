import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPaiementVente, NewPaiementVente } from '../paiement-vente.model';

export type PartialUpdatePaiementVente = Partial<IPaiementVente> & Pick<IPaiementVente, 'id'>;

type RestOf<T extends IPaiementVente | NewPaiementVente> = Omit<T, 'datePaiement'> & {
  datePaiement?: string | null;
};

export type RestPaiementVente = RestOf<IPaiementVente>;

export type NewRestPaiementVente = RestOf<NewPaiementVente>;

export type PartialUpdateRestPaiementVente = RestOf<PartialUpdatePaiementVente>;

@Injectable()
export class PaiementVentesService {
  readonly paiementVentesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly paiementVentesResource = httpResource<RestPaiementVente[]>(() => {
    const params = this.paiementVentesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of paiementVente that have been fetched. It is updated when the paiementVentesResource emits a new value.
   * In case of error while fetching the paiementVentes, the signal is set to an empty array.
   */
  readonly paiementVentes = computed(() =>
    (this.paiementVentesResource.hasValue() ? this.paiementVentesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/paiement-ventes');

  protected convertValueFromServer(restPaiementVente: RestPaiementVente): IPaiementVente {
    return {
      ...restPaiementVente,
      datePaiement: restPaiementVente.datePaiement ? dayjs(restPaiementVente.datePaiement) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PaiementVenteService extends PaiementVentesService {
  protected readonly http = inject(HttpClient);

  create(paiementVente: NewPaiementVente): Observable<IPaiementVente> {
    const copy = this.convertValueFromClient(paiementVente);
    return this.http.post<RestPaiementVente>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(paiementVente: IPaiementVente): Observable<IPaiementVente> {
    const copy = this.convertValueFromClient(paiementVente);
    return this.http
      .put<RestPaiementVente>(`${this.resourceUrl}/${encodeURIComponent(this.getPaiementVenteIdentifier(paiementVente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(paiementVente: PartialUpdatePaiementVente): Observable<IPaiementVente> {
    const copy = this.convertValueFromClient(paiementVente);
    return this.http
      .patch<RestPaiementVente>(`${this.resourceUrl}/${encodeURIComponent(this.getPaiementVenteIdentifier(paiementVente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPaiementVente> {
    return this.http
      .get<RestPaiementVente>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPaiementVente[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaiementVente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPaiementVenteIdentifier(paiementVente: Pick<IPaiementVente, 'id'>): number {
    return paiementVente.id;
  }

  comparePaiementVente(o1: Pick<IPaiementVente, 'id'> | null, o2: Pick<IPaiementVente, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaiementVenteIdentifier(o1) === this.getPaiementVenteIdentifier(o2) : o1 === o2;
  }

  addPaiementVenteToCollectionIfMissing<Type extends Pick<IPaiementVente, 'id'>>(
    paiementVenteCollection: Type[],
    ...paiementVentesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paiementVentes: Type[] = paiementVentesToCheck.filter(isPresent);
    if (paiementVentes.length > 0) {
      const paiementVenteCollectionIdentifiers = paiementVenteCollection.map(paiementVenteItem =>
        this.getPaiementVenteIdentifier(paiementVenteItem),
      );
      const paiementVentesToAdd = paiementVentes.filter(paiementVenteItem => {
        const paiementVenteIdentifier = this.getPaiementVenteIdentifier(paiementVenteItem);
        if (paiementVenteCollectionIdentifiers.includes(paiementVenteIdentifier)) {
          return false;
        }
        paiementVenteCollectionIdentifiers.push(paiementVenteIdentifier);
        return true;
      });
      return [...paiementVentesToAdd, ...paiementVenteCollection];
    }
    return paiementVenteCollection;
  }

  protected convertValueFromClient<T extends IPaiementVente | NewPaiementVente | PartialUpdatePaiementVente>(paiementVente: T): RestOf<T> {
    return {
      ...paiementVente,
      datePaiement: paiementVente.datePaiement?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPaiementVente): IPaiementVente {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPaiementVente[]): IPaiementVente[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
