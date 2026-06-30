import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IParametreCodeBarres, NewParametreCodeBarres } from '../parametre-code-barres.model';

export type PartialUpdateParametreCodeBarres = Partial<IParametreCodeBarres> & Pick<IParametreCodeBarres, 'id'>;

@Injectable()
export class ParametreCodeBarresesService {
  readonly parametreCodeBarresesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly parametreCodeBarresesResource = httpResource<IParametreCodeBarres[]>(() => {
    const params = this.parametreCodeBarresesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of parametreCodeBarres that have been fetched. It is updated when the parametreCodeBarresesResource emits a new value.
   * In case of error while fetching the parametreCodeBarreses, the signal is set to an empty array.
   */
  readonly parametreCodeBarreses = computed(() =>
    this.parametreCodeBarresesResource.hasValue() ? this.parametreCodeBarresesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/parametre-code-barres');
}

@Injectable({ providedIn: 'root' })
export class ParametreCodeBarresService extends ParametreCodeBarresesService {
  protected readonly http = inject(HttpClient);

  create(parametreCodeBarres: NewParametreCodeBarres): Observable<IParametreCodeBarres> {
    return this.http.post<IParametreCodeBarres>(this.resourceUrl, parametreCodeBarres);
  }

  update(parametreCodeBarres: IParametreCodeBarres): Observable<IParametreCodeBarres> {
    return this.http.put<IParametreCodeBarres>(
      `${this.resourceUrl}/${encodeURIComponent(this.getParametreCodeBarresIdentifier(parametreCodeBarres))}`,
      parametreCodeBarres,
    );
  }

  partialUpdate(parametreCodeBarres: PartialUpdateParametreCodeBarres): Observable<IParametreCodeBarres> {
    return this.http.patch<IParametreCodeBarres>(
      `${this.resourceUrl}/${encodeURIComponent(this.getParametreCodeBarresIdentifier(parametreCodeBarres))}`,
      parametreCodeBarres,
    );
  }

  find(id: number): Observable<IParametreCodeBarres> {
    return this.http.get<IParametreCodeBarres>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IParametreCodeBarres[]>> {
    const options = createRequestOption(req);
    return this.http.get<IParametreCodeBarres[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getParametreCodeBarresIdentifier(parametreCodeBarres: Pick<IParametreCodeBarres, 'id'>): number {
    return parametreCodeBarres.id;
  }

  compareParametreCodeBarres(o1: Pick<IParametreCodeBarres, 'id'> | null, o2: Pick<IParametreCodeBarres, 'id'> | null): boolean {
    return o1 && o2 ? this.getParametreCodeBarresIdentifier(o1) === this.getParametreCodeBarresIdentifier(o2) : o1 === o2;
  }

  addParametreCodeBarresToCollectionIfMissing<Type extends Pick<IParametreCodeBarres, 'id'>>(
    parametreCodeBarresCollection: Type[],
    ...parametreCodeBarresesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parametreCodeBarreses: Type[] = parametreCodeBarresesToCheck.filter(isPresent);
    if (parametreCodeBarreses.length > 0) {
      const parametreCodeBarresCollectionIdentifiers = parametreCodeBarresCollection.map(parametreCodeBarresItem =>
        this.getParametreCodeBarresIdentifier(parametreCodeBarresItem),
      );
      const parametreCodeBarresesToAdd = parametreCodeBarreses.filter(parametreCodeBarresItem => {
        const parametreCodeBarresIdentifier = this.getParametreCodeBarresIdentifier(parametreCodeBarresItem);
        if (parametreCodeBarresCollectionIdentifiers.includes(parametreCodeBarresIdentifier)) {
          return false;
        }
        parametreCodeBarresCollectionIdentifiers.push(parametreCodeBarresIdentifier);
        return true;
      });
      return [...parametreCodeBarresesToAdd, ...parametreCodeBarresCollection];
    }
    return parametreCodeBarresCollection;
  }
}
