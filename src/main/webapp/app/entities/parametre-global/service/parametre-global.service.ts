import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IParametreGlobal, NewParametreGlobal } from '../parametre-global.model';

export type PartialUpdateParametreGlobal = Partial<IParametreGlobal> & Pick<IParametreGlobal, 'id'>;

@Injectable()
export class ParametreGlobalsService {
  readonly parametreGlobalsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly parametreGlobalsResource = httpResource<IParametreGlobal[]>(() => {
    const params = this.parametreGlobalsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of parametreGlobal that have been fetched. It is updated when the parametreGlobalsResource emits a new value.
   * In case of error while fetching the parametreGlobals, the signal is set to an empty array.
   */
  readonly parametreGlobals = computed(() => (this.parametreGlobalsResource.hasValue() ? this.parametreGlobalsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/parametre-globals');
}

@Injectable({ providedIn: 'root' })
export class ParametreGlobalService extends ParametreGlobalsService {
  protected readonly http = inject(HttpClient);

  create(parametreGlobal: NewParametreGlobal): Observable<IParametreGlobal> {
    return this.http.post<IParametreGlobal>(this.resourceUrl, parametreGlobal);
  }

  update(parametreGlobal: IParametreGlobal): Observable<IParametreGlobal> {
    return this.http.put<IParametreGlobal>(
      `${this.resourceUrl}/${encodeURIComponent(this.getParametreGlobalIdentifier(parametreGlobal))}`,
      parametreGlobal,
    );
  }

  partialUpdate(parametreGlobal: PartialUpdateParametreGlobal): Observable<IParametreGlobal> {
    return this.http.patch<IParametreGlobal>(
      `${this.resourceUrl}/${encodeURIComponent(this.getParametreGlobalIdentifier(parametreGlobal))}`,
      parametreGlobal,
    );
  }

  find(id: number): Observable<IParametreGlobal> {
    return this.http.get<IParametreGlobal>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IParametreGlobal[]>> {
    const options = createRequestOption(req);
    return this.http.get<IParametreGlobal[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getParametreGlobalIdentifier(parametreGlobal: Pick<IParametreGlobal, 'id'>): number {
    return parametreGlobal.id;
  }

  compareParametreGlobal(o1: Pick<IParametreGlobal, 'id'> | null, o2: Pick<IParametreGlobal, 'id'> | null): boolean {
    return o1 && o2 ? this.getParametreGlobalIdentifier(o1) === this.getParametreGlobalIdentifier(o2) : o1 === o2;
  }

  addParametreGlobalToCollectionIfMissing<Type extends Pick<IParametreGlobal, 'id'>>(
    parametreGlobalCollection: Type[],
    ...parametreGlobalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parametreGlobals: Type[] = parametreGlobalsToCheck.filter(isPresent);
    if (parametreGlobals.length > 0) {
      const parametreGlobalCollectionIdentifiers = parametreGlobalCollection.map(parametreGlobalItem =>
        this.getParametreGlobalIdentifier(parametreGlobalItem),
      );
      const parametreGlobalsToAdd = parametreGlobals.filter(parametreGlobalItem => {
        const parametreGlobalIdentifier = this.getParametreGlobalIdentifier(parametreGlobalItem);
        if (parametreGlobalCollectionIdentifiers.includes(parametreGlobalIdentifier)) {
          return false;
        }
        parametreGlobalCollectionIdentifiers.push(parametreGlobalIdentifier);
        return true;
      });
      return [...parametreGlobalsToAdd, ...parametreGlobalCollection];
    }
    return parametreGlobalCollection;
  }
}
