import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPermissionMetier, NewPermissionMetier } from '../permission-metier.model';

export type PartialUpdatePermissionMetier = Partial<IPermissionMetier> & Pick<IPermissionMetier, 'id'>;

@Injectable()
export class PermissionMetiersService {
  readonly permissionMetiersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly permissionMetiersResource = httpResource<IPermissionMetier[]>(() => {
    const params = this.permissionMetiersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of permissionMetier that have been fetched. It is updated when the permissionMetiersResource emits a new value.
   * In case of error while fetching the permissionMetiers, the signal is set to an empty array.
   */
  readonly permissionMetiers = computed(() => (this.permissionMetiersResource.hasValue() ? this.permissionMetiersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/permission-metiers');
}

@Injectable({ providedIn: 'root' })
export class PermissionMetierService extends PermissionMetiersService {
  protected readonly http = inject(HttpClient);

  create(permissionMetier: NewPermissionMetier): Observable<IPermissionMetier> {
    return this.http.post<IPermissionMetier>(this.resourceUrl, permissionMetier);
  }

  update(permissionMetier: IPermissionMetier): Observable<IPermissionMetier> {
    return this.http.put<IPermissionMetier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPermissionMetierIdentifier(permissionMetier))}`,
      permissionMetier,
    );
  }

  partialUpdate(permissionMetier: PartialUpdatePermissionMetier): Observable<IPermissionMetier> {
    return this.http.patch<IPermissionMetier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPermissionMetierIdentifier(permissionMetier))}`,
      permissionMetier,
    );
  }

  find(id: number): Observable<IPermissionMetier> {
    return this.http.get<IPermissionMetier>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPermissionMetier[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPermissionMetier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPermissionMetierIdentifier(permissionMetier: Pick<IPermissionMetier, 'id'>): number {
    return permissionMetier.id;
  }

  comparePermissionMetier(o1: Pick<IPermissionMetier, 'id'> | null, o2: Pick<IPermissionMetier, 'id'> | null): boolean {
    return o1 && o2 ? this.getPermissionMetierIdentifier(o1) === this.getPermissionMetierIdentifier(o2) : o1 === o2;
  }

  addPermissionMetierToCollectionIfMissing<Type extends Pick<IPermissionMetier, 'id'>>(
    permissionMetierCollection: Type[],
    ...permissionMetiersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const permissionMetiers: Type[] = permissionMetiersToCheck.filter(isPresent);
    if (permissionMetiers.length > 0) {
      const permissionMetierCollectionIdentifiers = permissionMetierCollection.map(permissionMetierItem =>
        this.getPermissionMetierIdentifier(permissionMetierItem),
      );
      const permissionMetiersToAdd = permissionMetiers.filter(permissionMetierItem => {
        const permissionMetierIdentifier = this.getPermissionMetierIdentifier(permissionMetierItem);
        if (permissionMetierCollectionIdentifiers.includes(permissionMetierIdentifier)) {
          return false;
        }
        permissionMetierCollectionIdentifiers.push(permissionMetierIdentifier);
        return true;
      });
      return [...permissionMetiersToAdd, ...permissionMetierCollection];
    }
    return permissionMetierCollection;
  }
}
