import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBoutique, NewBoutique } from '../boutique.model';

export type PartialUpdateBoutique = Partial<IBoutique> & Pick<IBoutique, 'id'>;

type RestOf<T extends IBoutique | NewBoutique> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

export type RestBoutique = RestOf<IBoutique>;

export type NewRestBoutique = RestOf<NewBoutique>;

export type PartialUpdateRestBoutique = RestOf<PartialUpdateBoutique>;

@Injectable()
export class BoutiquesService {
  readonly boutiquesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly boutiquesResource = httpResource<RestBoutique[]>(() => {
    const params = this.boutiquesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of boutique that have been fetched. It is updated when the boutiquesResource emits a new value.
   * In case of error while fetching the boutiques, the signal is set to an empty array.
   */
  readonly boutiques = computed(() =>
    (this.boutiquesResource.hasValue() ? this.boutiquesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/boutiques');

  protected convertValueFromServer(restBoutique: RestBoutique): IBoutique {
    return {
      ...restBoutique,
      dateCreation: restBoutique.dateCreation ? dayjs(restBoutique.dateCreation) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class BoutiqueService extends BoutiquesService {
  protected readonly http = inject(HttpClient);

  create(boutique: NewBoutique): Observable<IBoutique> {
    const copy = this.convertValueFromClient(boutique);
    return this.http.post<RestBoutique>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(boutique: IBoutique): Observable<IBoutique> {
    const copy = this.convertValueFromClient(boutique);
    return this.http
      .put<RestBoutique>(`${this.resourceUrl}/${encodeURIComponent(this.getBoutiqueIdentifier(boutique))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(boutique: PartialUpdateBoutique): Observable<IBoutique> {
    const copy = this.convertValueFromClient(boutique);
    return this.http
      .patch<RestBoutique>(`${this.resourceUrl}/${encodeURIComponent(this.getBoutiqueIdentifier(boutique))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBoutique> {
    return this.http
      .get<RestBoutique>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBoutique[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBoutique[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBoutiqueIdentifier(boutique: Pick<IBoutique, 'id'>): number {
    return boutique.id;
  }

  compareBoutique(o1: Pick<IBoutique, 'id'> | null, o2: Pick<IBoutique, 'id'> | null): boolean {
    return o1 && o2 ? this.getBoutiqueIdentifier(o1) === this.getBoutiqueIdentifier(o2) : o1 === o2;
  }

  addBoutiqueToCollectionIfMissing<Type extends Pick<IBoutique, 'id'>>(
    boutiqueCollection: Type[],
    ...boutiquesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const boutiques: Type[] = boutiquesToCheck.filter(isPresent);
    if (boutiques.length > 0) {
      const boutiqueCollectionIdentifiers = boutiqueCollection.map(boutiqueItem => this.getBoutiqueIdentifier(boutiqueItem));
      const boutiquesToAdd = boutiques.filter(boutiqueItem => {
        const boutiqueIdentifier = this.getBoutiqueIdentifier(boutiqueItem);
        if (boutiqueCollectionIdentifiers.includes(boutiqueIdentifier)) {
          return false;
        }
        boutiqueCollectionIdentifiers.push(boutiqueIdentifier);
        return true;
      });
      return [...boutiquesToAdd, ...boutiqueCollection];
    }
    return boutiqueCollection;
  }

  protected convertValueFromClient<T extends IBoutique | NewBoutique | PartialUpdateBoutique>(boutique: T): RestOf<T> {
    return {
      ...boutique,
      dateCreation: boutique.dateCreation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBoutique): IBoutique {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBoutique[]): IBoutique[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
