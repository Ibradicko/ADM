import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneReceptionProduit, NewLigneReceptionProduit } from '../ligne-reception-produit.model';

export type PartialUpdateLigneReceptionProduit = Partial<ILigneReceptionProduit> & Pick<ILigneReceptionProduit, 'id'>;

@Injectable()
export class LigneReceptionProduitsService {
  readonly ligneReceptionProduitsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly ligneReceptionProduitsResource = httpResource<ILigneReceptionProduit[]>(() => {
    const params = this.ligneReceptionProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneReceptionProduit that have been fetched. It is updated when the ligneReceptionProduitsResource emits a new value.
   * In case of error while fetching the ligneReceptionProduits, the signal is set to an empty array.
   */
  readonly ligneReceptionProduits = computed(() =>
    this.ligneReceptionProduitsResource.hasValue() ? this.ligneReceptionProduitsResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-reception-produits');
}

@Injectable({ providedIn: 'root' })
export class LigneReceptionProduitService extends LigneReceptionProduitsService {
  protected readonly http = inject(HttpClient);

  create(ligneReceptionProduit: NewLigneReceptionProduit): Observable<ILigneReceptionProduit> {
    return this.http.post<ILigneReceptionProduit>(this.resourceUrl, ligneReceptionProduit);
  }

  update(ligneReceptionProduit: ILigneReceptionProduit): Observable<ILigneReceptionProduit> {
    return this.http.put<ILigneReceptionProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneReceptionProduitIdentifier(ligneReceptionProduit))}`,
      ligneReceptionProduit,
    );
  }

  partialUpdate(ligneReceptionProduit: PartialUpdateLigneReceptionProduit): Observable<ILigneReceptionProduit> {
    return this.http.patch<ILigneReceptionProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneReceptionProduitIdentifier(ligneReceptionProduit))}`,
      ligneReceptionProduit,
    );
  }

  find(id: number): Observable<ILigneReceptionProduit> {
    return this.http.get<ILigneReceptionProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneReceptionProduit[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneReceptionProduit[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneReceptionProduitIdentifier(ligneReceptionProduit: Pick<ILigneReceptionProduit, 'id'>): number {
    return ligneReceptionProduit.id;
  }

  compareLigneReceptionProduit(o1: Pick<ILigneReceptionProduit, 'id'> | null, o2: Pick<ILigneReceptionProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneReceptionProduitIdentifier(o1) === this.getLigneReceptionProduitIdentifier(o2) : o1 === o2;
  }

  addLigneReceptionProduitToCollectionIfMissing<Type extends Pick<ILigneReceptionProduit, 'id'>>(
    ligneReceptionProduitCollection: Type[],
    ...ligneReceptionProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneReceptionProduits: Type[] = ligneReceptionProduitsToCheck.filter(isPresent);
    if (ligneReceptionProduits.length > 0) {
      const ligneReceptionProduitCollectionIdentifiers = ligneReceptionProduitCollection.map(ligneReceptionProduitItem =>
        this.getLigneReceptionProduitIdentifier(ligneReceptionProduitItem),
      );
      const ligneReceptionProduitsToAdd = ligneReceptionProduits.filter(ligneReceptionProduitItem => {
        const ligneReceptionProduitIdentifier = this.getLigneReceptionProduitIdentifier(ligneReceptionProduitItem);
        if (ligneReceptionProduitCollectionIdentifiers.includes(ligneReceptionProduitIdentifier)) {
          return false;
        }
        ligneReceptionProduitCollectionIdentifiers.push(ligneReceptionProduitIdentifier);
        return true;
      });
      return [...ligneReceptionProduitsToAdd, ...ligneReceptionProduitCollection];
    }
    return ligneReceptionProduitCollection;
  }
}
