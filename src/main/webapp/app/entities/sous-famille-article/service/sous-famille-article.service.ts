import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISousFamilleArticle, NewSousFamilleArticle } from '../sous-famille-article.model';

export type PartialUpdateSousFamilleArticle = Partial<ISousFamilleArticle> & Pick<ISousFamilleArticle, 'id'>;

@Injectable()
export class SousFamilleArticlesService {
  readonly sousFamilleArticlesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly sousFamilleArticlesResource = httpResource<ISousFamilleArticle[]>(() => {
    const params = this.sousFamilleArticlesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of sousFamilleArticle that have been fetched. It is updated when the sousFamilleArticlesResource emits a new value.
   * In case of error while fetching the sousFamilleArticles, the signal is set to an empty array.
   */
  readonly sousFamilleArticles = computed(() =>
    this.sousFamilleArticlesResource.hasValue() ? this.sousFamilleArticlesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/sous-famille-articles');
}

@Injectable({ providedIn: 'root' })
export class SousFamilleArticleService extends SousFamilleArticlesService {
  protected readonly http = inject(HttpClient);

  create(sousFamilleArticle: NewSousFamilleArticle): Observable<ISousFamilleArticle> {
    return this.http.post<ISousFamilleArticle>(this.resourceUrl, sousFamilleArticle);
  }

  update(sousFamilleArticle: ISousFamilleArticle): Observable<ISousFamilleArticle> {
    return this.http.put<ISousFamilleArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getSousFamilleArticleIdentifier(sousFamilleArticle))}`,
      sousFamilleArticle,
    );
  }

  partialUpdate(sousFamilleArticle: PartialUpdateSousFamilleArticle): Observable<ISousFamilleArticle> {
    return this.http.patch<ISousFamilleArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getSousFamilleArticleIdentifier(sousFamilleArticle))}`,
      sousFamilleArticle,
    );
  }

  find(id: number): Observable<ISousFamilleArticle> {
    return this.http.get<ISousFamilleArticle>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISousFamilleArticle[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISousFamilleArticle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSousFamilleArticleIdentifier(sousFamilleArticle: Pick<ISousFamilleArticle, 'id'>): number {
    return sousFamilleArticle.id;
  }

  compareSousFamilleArticle(o1: Pick<ISousFamilleArticle, 'id'> | null, o2: Pick<ISousFamilleArticle, 'id'> | null): boolean {
    return o1 && o2 ? this.getSousFamilleArticleIdentifier(o1) === this.getSousFamilleArticleIdentifier(o2) : o1 === o2;
  }

  addSousFamilleArticleToCollectionIfMissing<Type extends Pick<ISousFamilleArticle, 'id'>>(
    sousFamilleArticleCollection: Type[],
    ...sousFamilleArticlesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sousFamilleArticles: Type[] = sousFamilleArticlesToCheck.filter(isPresent);
    if (sousFamilleArticles.length > 0) {
      const sousFamilleArticleCollectionIdentifiers = sousFamilleArticleCollection.map(sousFamilleArticleItem =>
        this.getSousFamilleArticleIdentifier(sousFamilleArticleItem),
      );
      const sousFamilleArticlesToAdd = sousFamilleArticles.filter(sousFamilleArticleItem => {
        const sousFamilleArticleIdentifier = this.getSousFamilleArticleIdentifier(sousFamilleArticleItem);
        if (sousFamilleArticleCollectionIdentifiers.includes(sousFamilleArticleIdentifier)) {
          return false;
        }
        sousFamilleArticleCollectionIdentifiers.push(sousFamilleArticleIdentifier);
        return true;
      });
      return [...sousFamilleArticlesToAdd, ...sousFamilleArticleCollection];
    }
    return sousFamilleArticleCollection;
  }
}
