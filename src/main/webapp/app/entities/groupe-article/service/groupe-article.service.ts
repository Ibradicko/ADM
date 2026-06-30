import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IGroupeArticle, NewGroupeArticle } from '../groupe-article.model';

export type PartialUpdateGroupeArticle = Partial<IGroupeArticle> & Pick<IGroupeArticle, 'id'>;

@Injectable()
export class GroupeArticlesService {
  readonly groupeArticlesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly groupeArticlesResource = httpResource<IGroupeArticle[]>(() => {
    const params = this.groupeArticlesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of groupeArticle that have been fetched. It is updated when the groupeArticlesResource emits a new value.
   * In case of error while fetching the groupeArticles, the signal is set to an empty array.
   */
  readonly groupeArticles = computed(() => (this.groupeArticlesResource.hasValue() ? this.groupeArticlesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/groupe-articles');
}

@Injectable({ providedIn: 'root' })
export class GroupeArticleService extends GroupeArticlesService {
  protected readonly http = inject(HttpClient);

  create(groupeArticle: NewGroupeArticle): Observable<IGroupeArticle> {
    return this.http.post<IGroupeArticle>(this.resourceUrl, groupeArticle);
  }

  update(groupeArticle: IGroupeArticle): Observable<IGroupeArticle> {
    return this.http.put<IGroupeArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getGroupeArticleIdentifier(groupeArticle))}`,
      groupeArticle,
    );
  }

  partialUpdate(groupeArticle: PartialUpdateGroupeArticle): Observable<IGroupeArticle> {
    return this.http.patch<IGroupeArticle>(
      `${this.resourceUrl}/${encodeURIComponent(this.getGroupeArticleIdentifier(groupeArticle))}`,
      groupeArticle,
    );
  }

  find(id: number): Observable<IGroupeArticle> {
    return this.http.get<IGroupeArticle>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IGroupeArticle[]>> {
    const options = createRequestOption(req);
    return this.http.get<IGroupeArticle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getGroupeArticleIdentifier(groupeArticle: Pick<IGroupeArticle, 'id'>): number {
    return groupeArticle.id;
  }

  compareGroupeArticle(o1: Pick<IGroupeArticle, 'id'> | null, o2: Pick<IGroupeArticle, 'id'> | null): boolean {
    return o1 && o2 ? this.getGroupeArticleIdentifier(o1) === this.getGroupeArticleIdentifier(o2) : o1 === o2;
  }

  addGroupeArticleToCollectionIfMissing<Type extends Pick<IGroupeArticle, 'id'>>(
    groupeArticleCollection: Type[],
    ...groupeArticlesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const groupeArticles: Type[] = groupeArticlesToCheck.filter(isPresent);
    if (groupeArticles.length > 0) {
      const groupeArticleCollectionIdentifiers = groupeArticleCollection.map(groupeArticleItem =>
        this.getGroupeArticleIdentifier(groupeArticleItem),
      );
      const groupeArticlesToAdd = groupeArticles.filter(groupeArticleItem => {
        const groupeArticleIdentifier = this.getGroupeArticleIdentifier(groupeArticleItem);
        if (groupeArticleCollectionIdentifiers.includes(groupeArticleIdentifier)) {
          return false;
        }
        groupeArticleCollectionIdentifiers.push(groupeArticleIdentifier);
        return true;
      });
      return [...groupeArticlesToAdd, ...groupeArticleCollection];
    }
    return groupeArticleCollection;
  }
}
