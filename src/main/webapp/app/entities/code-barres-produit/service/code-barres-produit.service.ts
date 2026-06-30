import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProduit } from 'app/entities/produit/produit.model';
import { ICodeBarresProduit, NewCodeBarresProduit } from '../code-barres-produit.model';

export interface BarcodeScanResult {
  trouve: boolean;
  message: string;
  affectationAutorisee: boolean;
  scanInconnuId?: number | null;
  produit?: IProduit | null;
}

export type PartialUpdateCodeBarresProduit = Partial<ICodeBarresProduit> & Pick<ICodeBarresProduit, 'id'>;

type RestOf<T extends ICodeBarresProduit | NewCodeBarresProduit> = Omit<T, 'dateAffectation'> & {
  dateAffectation?: string | null;
};

export type RestCodeBarresProduit = RestOf<ICodeBarresProduit>;

export type NewRestCodeBarresProduit = RestOf<NewCodeBarresProduit>;

export type PartialUpdateRestCodeBarresProduit = RestOf<PartialUpdateCodeBarresProduit>;

@Injectable()
export class CodeBarresProduitsService {
  readonly codeBarresProduitsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly codeBarresProduitsResource = httpResource<RestCodeBarresProduit[]>(() => {
    const params = this.codeBarresProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of codeBarresProduit that have been fetched. It is updated when the codeBarresProduitsResource emits a new value.
   * In case of error while fetching the codeBarresProduits, the signal is set to an empty array.
   */
  readonly codeBarresProduits = computed(() =>
    (this.codeBarresProduitsResource.hasValue() ? this.codeBarresProduitsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/code-barres-produits');

  protected convertValueFromServer(restCodeBarresProduit: RestCodeBarresProduit): ICodeBarresProduit {
    return {
      ...restCodeBarresProduit,
      dateAffectation: restCodeBarresProduit.dateAffectation ? dayjs(restCodeBarresProduit.dateAffectation) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CodeBarresProduitService extends CodeBarresProduitsService {
  protected readonly http = inject(HttpClient);

  create(codeBarresProduit: NewCodeBarresProduit): Observable<ICodeBarresProduit> {
    const copy = this.convertValueFromClient(codeBarresProduit);
    return this.http.post<RestCodeBarresProduit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(codeBarresProduit: ICodeBarresProduit): Observable<ICodeBarresProduit> {
    const copy = this.convertValueFromClient(codeBarresProduit);
    return this.http
      .put<RestCodeBarresProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getCodeBarresProduitIdentifier(codeBarresProduit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(codeBarresProduit: PartialUpdateCodeBarresProduit): Observable<ICodeBarresProduit> {
    const copy = this.convertValueFromClient(codeBarresProduit);
    return this.http
      .patch<RestCodeBarresProduit>(
        `${this.resourceUrl}/${encodeURIComponent(this.getCodeBarresProduitIdentifier(codeBarresProduit))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICodeBarresProduit> {
    return this.http
      .get<RestCodeBarresProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICodeBarresProduit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCodeBarresProduit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  generate(produitId: number): Observable<ICodeBarresProduit> {
    return this.http
      .post<RestCodeBarresProduit>(`${this.resourceUrl}/generate/${encodeURIComponent(produitId)}`, {})
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  scan(code: string, boutiqueId: number, ecranOrigine: string): Observable<BarcodeScanResult> {
    return this.http.post<BarcodeScanResult>(this.applicationConfigService.getEndpointFor('api/barcodes/scan'), {
      code,
      boutiqueId,
      ecranOrigine,
    });
  }

  getCodeBarresProduitIdentifier(codeBarresProduit: Pick<ICodeBarresProduit, 'id'>): number {
    return codeBarresProduit.id;
  }

  compareCodeBarresProduit(o1: Pick<ICodeBarresProduit, 'id'> | null, o2: Pick<ICodeBarresProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getCodeBarresProduitIdentifier(o1) === this.getCodeBarresProduitIdentifier(o2) : o1 === o2;
  }

  addCodeBarresProduitToCollectionIfMissing<Type extends Pick<ICodeBarresProduit, 'id'>>(
    codeBarresProduitCollection: Type[],
    ...codeBarresProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const codeBarresProduits: Type[] = codeBarresProduitsToCheck.filter(isPresent);
    if (codeBarresProduits.length > 0) {
      const codeBarresProduitCollectionIdentifiers = codeBarresProduitCollection.map(codeBarresProduitItem =>
        this.getCodeBarresProduitIdentifier(codeBarresProduitItem),
      );
      const codeBarresProduitsToAdd = codeBarresProduits.filter(codeBarresProduitItem => {
        const codeBarresProduitIdentifier = this.getCodeBarresProduitIdentifier(codeBarresProduitItem);
        if (codeBarresProduitCollectionIdentifiers.includes(codeBarresProduitIdentifier)) {
          return false;
        }
        codeBarresProduitCollectionIdentifiers.push(codeBarresProduitIdentifier);
        return true;
      });
      return [...codeBarresProduitsToAdd, ...codeBarresProduitCollection];
    }
    return codeBarresProduitCollection;
  }

  protected convertValueFromClient<T extends ICodeBarresProduit | NewCodeBarresProduit | PartialUpdateCodeBarresProduit>(
    codeBarresProduit: T,
  ): RestOf<T> {
    return {
      ...codeBarresProduit,
      dateAffectation: codeBarresProduit.dateAffectation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCodeBarresProduit): ICodeBarresProduit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCodeBarresProduit[]): ICodeBarresProduit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
