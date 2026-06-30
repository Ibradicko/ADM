import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneVente, NewLigneVente } from '../ligne-vente.model';

export type PartialUpdateLigneVente = Partial<ILigneVente> & Pick<ILigneVente, 'id'>;

@Injectable()
export class LigneVentesService {
  readonly ligneVentesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ligneVentesResource = httpResource<ILigneVente[]>(() => {
    const params = this.ligneVentesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneVente that have been fetched. It is updated when the ligneVentesResource emits a new value.
   * In case of error while fetching the ligneVentes, the signal is set to an empty array.
   */
  readonly ligneVentes = computed(() => (this.ligneVentesResource.hasValue() ? this.ligneVentesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-ventes');
}

@Injectable({ providedIn: 'root' })
export class LigneVenteService extends LigneVentesService {
  protected readonly http = inject(HttpClient);

  create(ligneVente: NewLigneVente): Observable<ILigneVente> {
    return this.http.post<ILigneVente>(this.resourceUrl, ligneVente);
  }

  update(ligneVente: ILigneVente): Observable<ILigneVente> {
    return this.http.put<ILigneVente>(`${this.resourceUrl}/${encodeURIComponent(this.getLigneVenteIdentifier(ligneVente))}`, ligneVente);
  }

  partialUpdate(ligneVente: PartialUpdateLigneVente): Observable<ILigneVente> {
    return this.http.patch<ILigneVente>(`${this.resourceUrl}/${encodeURIComponent(this.getLigneVenteIdentifier(ligneVente))}`, ligneVente);
  }

  find(id: number): Observable<ILigneVente> {
    return this.http.get<ILigneVente>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneVente[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneVente[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneVenteIdentifier(ligneVente: Pick<ILigneVente, 'id'>): number {
    return ligneVente.id;
  }

  compareLigneVente(o1: Pick<ILigneVente, 'id'> | null, o2: Pick<ILigneVente, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneVenteIdentifier(o1) === this.getLigneVenteIdentifier(o2) : o1 === o2;
  }

  addLigneVenteToCollectionIfMissing<Type extends Pick<ILigneVente, 'id'>>(
    ligneVenteCollection: Type[],
    ...ligneVentesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneVentes: Type[] = ligneVentesToCheck.filter(isPresent);
    if (ligneVentes.length > 0) {
      const ligneVenteCollectionIdentifiers = ligneVenteCollection.map(ligneVenteItem => this.getLigneVenteIdentifier(ligneVenteItem));
      const ligneVentesToAdd = ligneVentes.filter(ligneVenteItem => {
        const ligneVenteIdentifier = this.getLigneVenteIdentifier(ligneVenteItem);
        if (ligneVenteCollectionIdentifiers.includes(ligneVenteIdentifier)) {
          return false;
        }
        ligneVenteCollectionIdentifiers.push(ligneVenteIdentifier);
        return true;
      });
      return [...ligneVentesToAdd, ...ligneVenteCollection];
    }
    return ligneVenteCollection;
  }
}
