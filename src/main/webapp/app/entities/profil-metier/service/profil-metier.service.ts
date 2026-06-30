import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfilMetier, NewProfilMetier } from '../profil-metier.model';

export type PartialUpdateProfilMetier = Partial<IProfilMetier> & Pick<IProfilMetier, 'id'>;

@Injectable()
export class ProfilMetiersService {
  readonly profilMetiersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly profilMetiersResource = httpResource<IProfilMetier[]>(() => {
    const params = this.profilMetiersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profilMetier that have been fetched. It is updated when the profilMetiersResource emits a new value.
   * In case of error while fetching the profilMetiers, the signal is set to an empty array.
   */
  readonly profilMetiers = computed(() => (this.profilMetiersResource.hasValue() ? this.profilMetiersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profil-metiers');
}

@Injectable({ providedIn: 'root' })
export class ProfilMetierService extends ProfilMetiersService {
  protected readonly http = inject(HttpClient);

  create(profilMetier: NewProfilMetier): Observable<IProfilMetier> {
    return this.http.post<IProfilMetier>(this.resourceUrl, profilMetier);
  }

  update(profilMetier: IProfilMetier): Observable<IProfilMetier> {
    return this.http.put<IProfilMetier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfilMetierIdentifier(profilMetier))}`,
      profilMetier,
    );
  }

  partialUpdate(profilMetier: PartialUpdateProfilMetier): Observable<IProfilMetier> {
    return this.http.patch<IProfilMetier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfilMetierIdentifier(profilMetier))}`,
      profilMetier,
    );
  }

  find(id: number): Observable<IProfilMetier> {
    return this.http.get<IProfilMetier>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfilMetier[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfilMetier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfilMetierIdentifier(profilMetier: Pick<IProfilMetier, 'id'>): number {
    return profilMetier.id;
  }

  compareProfilMetier(o1: Pick<IProfilMetier, 'id'> | null, o2: Pick<IProfilMetier, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfilMetierIdentifier(o1) === this.getProfilMetierIdentifier(o2) : o1 === o2;
  }

  addProfilMetierToCollectionIfMissing<Type extends Pick<IProfilMetier, 'id'>>(
    profilMetierCollection: Type[],
    ...profilMetiersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profilMetiers: Type[] = profilMetiersToCheck.filter(isPresent);
    if (profilMetiers.length > 0) {
      const profilMetierCollectionIdentifiers = profilMetierCollection.map(profilMetierItem =>
        this.getProfilMetierIdentifier(profilMetierItem),
      );
      const profilMetiersToAdd = profilMetiers.filter(profilMetierItem => {
        const profilMetierIdentifier = this.getProfilMetierIdentifier(profilMetierItem);
        if (profilMetierCollectionIdentifiers.includes(profilMetierIdentifier)) {
          return false;
        }
        profilMetierCollectionIdentifiers.push(profilMetierIdentifier);
        return true;
      });
      return [...profilMetiersToAdd, ...profilMetierCollection];
    }
    return profilMetierCollection;
  }
}
