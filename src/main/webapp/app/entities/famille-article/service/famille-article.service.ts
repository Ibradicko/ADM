import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFamilleArticle, NewFamilleArticle } from '../famille-article.model';

export type PartialUpdateFamilleArticle = Partial<IFamilleArticle> & Pick<IFamilleArticle, 'id'>;

@Injectable()
export class FamilleArticlesService {
  readonly familleArticlesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly familleArticlesResource = httpResource<IFamilleArticle[]>(() => {
    const params = this.familleArticlesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of familleArticle that have been fetched. It is updated when the familleArticlesResource emits a new value.
   * In case of error while fetching the familleArticles, the signal is set to an empty array.
   */
  readonly familleArticles = computed(() => (this.familleArticlesResource.hasValue() ? this.familleArticlesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/famille-articles');
}

@Injectable({ providedIn: 'root' })
export class FamilleArticleService extends FamilleArticlesService {
  protected readonly http = inject(HttpClient);

  create(familleArticle: NewFamilleArticle): Observable<IFamilleArticle> {
    return this.http.post<IFamilleArticle>(this.resourceUrl, familleArticle);
  }

  update(familleArticle: IFamilleArticle): Observable<IFamilleArticle> {
    return this.http.put<IFamilleArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getFamilleArticleIdentifier(familleArticle))}`,
      familleArticle,
    );
  }

  partialUpdate(familleArticle: PartialUpdateFamilleArticle): Observable<IFamilleArticle> {
    return this.http.patch<IFamilleArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getFamilleArticleIdentifier(familleArticle))}`,
      familleArticle,
    );
  }

  find(id: number): Observable<IFamilleArticle> {
    return this.http.get<IFamilleArticle>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IFamilleArticle[]>> {
    const options = createRequestOption(req);
    return this.http.get<IFamilleArticle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFamilleArticleIdentifier(familleArticle: Pick<IFamilleArticle, 'id'>): number {
    return familleArticle.id;
  }

  compareFamilleArticle(o1: Pick<IFamilleArticle, 'id'> | null, o2: Pick<IFamilleArticle, 'id'> | null): boolean {
    return o1 && o2 ? this.getFamilleArticleIdentifier(o1) === this.getFamilleArticleIdentifier(o2) : o1 === o2;
  }

  addFamilleArticleToCollectionIfMissing<Type extends Pick<IFamilleArticle, 'id'>>(
    familleArticleCollection: Type[],
    ...familleArticlesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const familleArticles: Type[] = familleArticlesToCheck.filter(isPresent);
    if (familleArticles.length > 0) {
      const familleArticleCollectionIdentifiers = familleArticleCollection.map(familleArticleItem =>
        this.getFamilleArticleIdentifier(familleArticleItem),
      );
      const familleArticlesToAdd = familleArticles.filter(familleArticleItem => {
        const familleArticleIdentifier = this.getFamilleArticleIdentifier(familleArticleItem);
        if (familleArticleCollectionIdentifiers.includes(familleArticleIdentifier)) {
          return false;
        }
        familleArticleCollectionIdentifiers.push(familleArticleIdentifier);
        return true;
      });
      return [...familleArticlesToAdd, ...familleArticleCollection];
    }
    return familleArticleCollection;
  }
}
