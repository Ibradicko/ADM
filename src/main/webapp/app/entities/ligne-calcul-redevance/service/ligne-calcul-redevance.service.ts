import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneCalculRedevance, NewLigneCalculRedevance } from '../ligne-calcul-redevance.model';

export type PartialUpdateLigneCalculRedevance = Partial<ILigneCalculRedevance> & Pick<ILigneCalculRedevance, 'id'>;

@Injectable()
export class LigneCalculRedevancesService {
  readonly ligneCalculRedevancesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly ligneCalculRedevancesResource = httpResource<ILigneCalculRedevance[]>(() => {
    const params = this.ligneCalculRedevancesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneCalculRedevance that have been fetched. It is updated when the ligneCalculRedevancesResource emits a new value.
   * In case of error while fetching the ligneCalculRedevances, the signal is set to an empty array.
   */
  readonly ligneCalculRedevances = computed(() =>
    this.ligneCalculRedevancesResource.hasValue() ? this.ligneCalculRedevancesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-calcul-redevances');
}

@Injectable({ providedIn: 'root' })
export class LigneCalculRedevanceService extends LigneCalculRedevancesService {
  protected readonly http = inject(HttpClient);

  create(ligneCalculRedevance: NewLigneCalculRedevance): Observable<ILigneCalculRedevance> {
    return this.http.post<ILigneCalculRedevance>(this.resourceUrl, ligneCalculRedevance);
  }

  update(ligneCalculRedevance: ILigneCalculRedevance): Observable<ILigneCalculRedevance> {
    return this.http.put<ILigneCalculRedevance>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneCalculRedevanceIdentifier(ligneCalculRedevance))}`,
      ligneCalculRedevance,
    );
  }

  partialUpdate(ligneCalculRedevance: PartialUpdateLigneCalculRedevance): Observable<ILigneCalculRedevance> {
    return this.http.patch<ILigneCalculRedevance>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneCalculRedevanceIdentifier(ligneCalculRedevance))}`,
      ligneCalculRedevance,
    );
  }

  find(id: number): Observable<ILigneCalculRedevance> {
    return this.http.get<ILigneCalculRedevance>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneCalculRedevance[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneCalculRedevance[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneCalculRedevanceIdentifier(ligneCalculRedevance: Pick<ILigneCalculRedevance, 'id'>): number {
    return ligneCalculRedevance.id;
  }

  compareLigneCalculRedevance(o1: Pick<ILigneCalculRedevance, 'id'> | null, o2: Pick<ILigneCalculRedevance, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneCalculRedevanceIdentifier(o1) === this.getLigneCalculRedevanceIdentifier(o2) : o1 === o2;
  }

  addLigneCalculRedevanceToCollectionIfMissing<Type extends Pick<ILigneCalculRedevance, 'id'>>(
    ligneCalculRedevanceCollection: Type[],
    ...ligneCalculRedevancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneCalculRedevances: Type[] = ligneCalculRedevancesToCheck.filter(isPresent);
    if (ligneCalculRedevances.length > 0) {
      const ligneCalculRedevanceCollectionIdentifiers = ligneCalculRedevanceCollection.map(ligneCalculRedevanceItem =>
        this.getLigneCalculRedevanceIdentifier(ligneCalculRedevanceItem),
      );
      const ligneCalculRedevancesToAdd = ligneCalculRedevances.filter(ligneCalculRedevanceItem => {
        const ligneCalculRedevanceIdentifier = this.getLigneCalculRedevanceIdentifier(ligneCalculRedevanceItem);
        if (ligneCalculRedevanceCollectionIdentifiers.includes(ligneCalculRedevanceIdentifier)) {
          return false;
        }
        ligneCalculRedevanceCollectionIdentifiers.push(ligneCalculRedevanceIdentifier);
        return true;
      });
      return [...ligneCalculRedevancesToAdd, ...ligneCalculRedevanceCollection];
    }
    return ligneCalculRedevanceCollection;
  }
}
