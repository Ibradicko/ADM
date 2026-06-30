import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IUniteMesure, NewUniteMesure } from '../unite-mesure.model';

export type PartialUpdateUniteMesure = Partial<IUniteMesure> & Pick<IUniteMesure, 'id'>;

@Injectable()
export class UniteMesuresService {
  readonly uniteMesuresParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly uniteMesuresResource = httpResource<IUniteMesure[]>(() => {
    const params = this.uniteMesuresParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of uniteMesure that have been fetched. It is updated when the uniteMesuresResource emits a new value.
   * In case of error while fetching the uniteMesures, the signal is set to an empty array.
   */
  readonly uniteMesures = computed(() => (this.uniteMesuresResource.hasValue() ? this.uniteMesuresResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/unite-mesures');
}

@Injectable({ providedIn: 'root' })
export class UniteMesureService extends UniteMesuresService {
  protected readonly http = inject(HttpClient);

  create(uniteMesure: NewUniteMesure): Observable<IUniteMesure> {
    return this.http.post<IUniteMesure>(this.resourceUrl, uniteMesure);
  }

  update(uniteMesure: IUniteMesure): Observable<IUniteMesure> {
    return this.http.put<IUniteMesure>(
      `${this.resourceUrl}/${encodeURIComponent(this.getUniteMesureIdentifier(uniteMesure))}`,
      uniteMesure,
    );
  }

  partialUpdate(uniteMesure: PartialUpdateUniteMesure): Observable<IUniteMesure> {
    return this.http.patch<IUniteMesure>(
      `${this.resourceUrl}/${encodeURIComponent(this.getUniteMesureIdentifier(uniteMesure))}`,
      uniteMesure,
    );
  }

  find(id: number): Observable<IUniteMesure> {
    return this.http.get<IUniteMesure>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IUniteMesure[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUniteMesure[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getUniteMesureIdentifier(uniteMesure: Pick<IUniteMesure, 'id'>): number {
    return uniteMesure.id;
  }

  compareUniteMesure(o1: Pick<IUniteMesure, 'id'> | null, o2: Pick<IUniteMesure, 'id'> | null): boolean {
    return o1 && o2 ? this.getUniteMesureIdentifier(o1) === this.getUniteMesureIdentifier(o2) : o1 === o2;
  }

  addUniteMesureToCollectionIfMissing<Type extends Pick<IUniteMesure, 'id'>>(
    uniteMesureCollection: Type[],
    ...uniteMesuresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const uniteMesures: Type[] = uniteMesuresToCheck.filter(isPresent);
    if (uniteMesures.length > 0) {
      const uniteMesureCollectionIdentifiers = uniteMesureCollection.map(uniteMesureItem => this.getUniteMesureIdentifier(uniteMesureItem));
      const uniteMesuresToAdd = uniteMesures.filter(uniteMesureItem => {
        const uniteMesureIdentifier = this.getUniteMesureIdentifier(uniteMesureItem);
        if (uniteMesureCollectionIdentifiers.includes(uniteMesureIdentifier)) {
          return false;
        }
        uniteMesureCollectionIdentifiers.push(uniteMesureIdentifier);
        return true;
      });
      return [...uniteMesuresToAdd, ...uniteMesureCollection];
    }
    return uniteMesureCollection;
  }
}
