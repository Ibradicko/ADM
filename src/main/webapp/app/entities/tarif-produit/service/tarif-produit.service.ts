import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITarifProduit, NewTarifProduit } from '../tarif-produit.model';

export type PartialUpdateTarifProduit = Partial<ITarifProduit> & Pick<ITarifProduit, 'id'>;

type RestOf<T extends ITarifProduit | NewTarifProduit> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

export type RestTarifProduit = RestOf<ITarifProduit>;

export type NewRestTarifProduit = RestOf<NewTarifProduit>;

export type PartialUpdateRestTarifProduit = RestOf<PartialUpdateTarifProduit>;

@Injectable()
export class TarifProduitsService {
  readonly tarifProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tarifProduitsResource = httpResource<RestTarifProduit[]>(() => {
    const params = this.tarifProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tarifProduit that have been fetched. It is updated when the tarifProduitsResource emits a new value.
   * In case of error while fetching the tarifProduits, the signal is set to an empty array.
   */
  readonly tarifProduits = computed(() =>
    (this.tarifProduitsResource.hasValue() ? this.tarifProduitsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/tarif-produits');

  protected convertValueFromServer(restTarifProduit: RestTarifProduit): ITarifProduit {
    return {
      ...restTarifProduit,
      dateDebut: restTarifProduit.dateDebut ? dayjs(restTarifProduit.dateDebut) : undefined,
      dateFin: restTarifProduit.dateFin ? dayjs(restTarifProduit.dateFin) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TarifProduitService extends TarifProduitsService {
  protected readonly http = inject(HttpClient);

  create(tarifProduit: NewTarifProduit): Observable<ITarifProduit> {
    const copy = this.convertValueFromClient(tarifProduit);
    return this.http.post<RestTarifProduit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tarifProduit: ITarifProduit): Observable<ITarifProduit> {
    const copy = this.convertValueFromClient(tarifProduit);
    return this.http
      .put<RestTarifProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getTarifProduitIdentifier(tarifProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tarifProduit: PartialUpdateTarifProduit): Observable<ITarifProduit> {
    const copy = this.convertValueFromClient(tarifProduit);
    return this.http
      .patch<RestTarifProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getTarifProduitIdentifier(tarifProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITarifProduit> {
    return this.http
      .get<RestTarifProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITarifProduit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTarifProduit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTarifProduitIdentifier(tarifProduit: Pick<ITarifProduit, 'id'>): number {
    return tarifProduit.id;
  }

  compareTarifProduit(o1: Pick<ITarifProduit, 'id'> | null, o2: Pick<ITarifProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getTarifProduitIdentifier(o1) === this.getTarifProduitIdentifier(o2) : o1 === o2;
  }

  addTarifProduitToCollectionIfMissing<Type extends Pick<ITarifProduit, 'id'>>(
    tarifProduitCollection: Type[],
    ...tarifProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tarifProduits: Type[] = tarifProduitsToCheck.filter(isPresent);
    if (tarifProduits.length > 0) {
      const tarifProduitCollectionIdentifiers = tarifProduitCollection.map(tarifProduitItem =>
        this.getTarifProduitIdentifier(tarifProduitItem),
      );
      const tarifProduitsToAdd = tarifProduits.filter(tarifProduitItem => {
        const tarifProduitIdentifier = this.getTarifProduitIdentifier(tarifProduitItem);
        if (tarifProduitCollectionIdentifiers.includes(tarifProduitIdentifier)) {
          return false;
        }
        tarifProduitCollectionIdentifiers.push(tarifProduitIdentifier);
        return true;
      });
      return [...tarifProduitsToAdd, ...tarifProduitCollection];
    }
    return tarifProduitCollection;
  }

  protected convertValueFromClient<T extends ITarifProduit | NewTarifProduit | PartialUpdateTarifProduit>(tarifProduit: T): RestOf<T> {
    return {
      ...tarifProduit,
      dateDebut: tarifProduit.dateDebut?.format(DATE_FORMAT) ?? null,
      dateFin: tarifProduit.dateFin?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTarifProduit): ITarifProduit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTarifProduit[]): ITarifProduit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
