import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IModePaiementRef, NewModePaiementRef } from '../mode-paiement-ref.model';

export type PartialUpdateModePaiementRef = Partial<IModePaiementRef> & Pick<IModePaiementRef, 'id'>;

@Injectable()
export class ModePaiementRefsService {
  readonly modePaiementRefsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly modePaiementRefsResource = httpResource<IModePaiementRef[]>(() => {
    const params = this.modePaiementRefsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of modePaiementRef that have been fetched. It is updated when the modePaiementRefsResource emits a new value.
   * In case of error while fetching the modePaiementRefs, the signal is set to an empty array.
   */
  readonly modePaiementRefs = computed(() => (this.modePaiementRefsResource.hasValue() ? this.modePaiementRefsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mode-paiement-refs');
}

@Injectable({ providedIn: 'root' })
export class ModePaiementRefService extends ModePaiementRefsService {
  protected readonly http = inject(HttpClient);

  create(modePaiementRef: NewModePaiementRef): Observable<IModePaiementRef> {
    return this.http.post<IModePaiementRef>(this.resourceUrl, modePaiementRef);
  }

  update(modePaiementRef: IModePaiementRef): Observable<IModePaiementRef> {
    return this.http.put<IModePaiementRef>(
      `${this.resourceUrl}/${encodeURIComponent(this.getModePaiementRefIdentifier(modePaiementRef))}`,
      modePaiementRef,
    );
  }

  partialUpdate(modePaiementRef: PartialUpdateModePaiementRef): Observable<IModePaiementRef> {
    return this.http.patch<IModePaiementRef>(
      `${this.resourceUrl}/${encodeURIComponent(this.getModePaiementRefIdentifier(modePaiementRef))}`,
      modePaiementRef,
    );
  }

  find(id: number): Observable<IModePaiementRef> {
    return this.http.get<IModePaiementRef>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IModePaiementRef[]>> {
    const options = createRequestOption(req);
    return this.http.get<IModePaiementRef[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getModePaiementRefIdentifier(modePaiementRef: Pick<IModePaiementRef, 'id'>): number {
    return modePaiementRef.id;
  }

  compareModePaiementRef(o1: Pick<IModePaiementRef, 'id'> | null, o2: Pick<IModePaiementRef, 'id'> | null): boolean {
    return o1 && o2 ? this.getModePaiementRefIdentifier(o1) === this.getModePaiementRefIdentifier(o2) : o1 === o2;
  }

  addModePaiementRefToCollectionIfMissing<Type extends Pick<IModePaiementRef, 'id'>>(
    modePaiementRefCollection: Type[],
    ...modePaiementRefsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const modePaiementRefs: Type[] = modePaiementRefsToCheck.filter(isPresent);
    if (modePaiementRefs.length > 0) {
      const modePaiementRefCollectionIdentifiers = modePaiementRefCollection.map(modePaiementRefItem =>
        this.getModePaiementRefIdentifier(modePaiementRefItem),
      );
      const modePaiementRefsToAdd = modePaiementRefs.filter(modePaiementRefItem => {
        const modePaiementRefIdentifier = this.getModePaiementRefIdentifier(modePaiementRefItem);
        if (modePaiementRefCollectionIdentifiers.includes(modePaiementRefIdentifier)) {
          return false;
        }
        modePaiementRefCollectionIdentifiers.push(modePaiementRefIdentifier);
        return true;
      });
      return [...modePaiementRefsToAdd, ...modePaiementRefCollection];
    }
    return modePaiementRefCollection;
  }
}
