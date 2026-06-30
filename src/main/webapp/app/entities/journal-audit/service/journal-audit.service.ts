import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IJournalAudit, NewJournalAudit } from '../journal-audit.model';

export type PartialUpdateJournalAudit = Partial<IJournalAudit> & Pick<IJournalAudit, 'id'>;

type RestOf<T extends IJournalAudit | NewJournalAudit> = Omit<T, 'dateAction'> & {
  dateAction?: string | null;
};

export type RestJournalAudit = RestOf<IJournalAudit>;

export type NewRestJournalAudit = RestOf<NewJournalAudit>;

export type PartialUpdateRestJournalAudit = RestOf<PartialUpdateJournalAudit>;

@Injectable()
export class JournalAuditsService {
  readonly journalAuditsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly journalAuditsResource = httpResource<RestJournalAudit[]>(() => {
    const params = this.journalAuditsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of journalAudit that have been fetched. It is updated when the journalAuditsResource emits a new value.
   * In case of error while fetching the journalAudits, the signal is set to an empty array.
   */
  readonly journalAudits = computed(() =>
    (this.journalAuditsResource.hasValue() ? this.journalAuditsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/journal-audits');

  protected convertValueFromServer(restJournalAudit: RestJournalAudit): IJournalAudit {
    return {
      ...restJournalAudit,
      dateAction: restJournalAudit.dateAction ? dayjs(restJournalAudit.dateAction) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class JournalAuditService extends JournalAuditsService {
  protected readonly http = inject(HttpClient);

  create(journalAudit: NewJournalAudit): Observable<IJournalAudit> {
    const copy = this.convertValueFromClient(journalAudit);
    return this.http.post<RestJournalAudit>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(journalAudit: IJournalAudit): Observable<IJournalAudit> {
    const copy = this.convertValueFromClient(journalAudit);
    return this.http
      .put<RestJournalAudit>(`${this.resourceUrl}/${encodeURIComponent(this.getJournalAuditIdentifier(journalAudit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(journalAudit: PartialUpdateJournalAudit): Observable<IJournalAudit> {
    const copy = this.convertValueFromClient(journalAudit);
    return this.http
      .patch<RestJournalAudit>(`${this.resourceUrl}/${encodeURIComponent(this.getJournalAuditIdentifier(journalAudit))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IJournalAudit> {
    return this.http
      .get<RestJournalAudit>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IJournalAudit[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestJournalAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getJournalAuditIdentifier(journalAudit: Pick<IJournalAudit, 'id'>): number {
    return journalAudit.id;
  }

  compareJournalAudit(o1: Pick<IJournalAudit, 'id'> | null, o2: Pick<IJournalAudit, 'id'> | null): boolean {
    return o1 && o2 ? this.getJournalAuditIdentifier(o1) === this.getJournalAuditIdentifier(o2) : o1 === o2;
  }

  addJournalAuditToCollectionIfMissing<Type extends Pick<IJournalAudit, 'id'>>(
    journalAuditCollection: Type[],
    ...journalAuditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const journalAudits: Type[] = journalAuditsToCheck.filter(isPresent);
    if (journalAudits.length > 0) {
      const journalAuditCollectionIdentifiers = journalAuditCollection.map(journalAuditItem =>
        this.getJournalAuditIdentifier(journalAuditItem),
      );
      const journalAuditsToAdd = journalAudits.filter(journalAuditItem => {
        const journalAuditIdentifier = this.getJournalAuditIdentifier(journalAuditItem);
        if (journalAuditCollectionIdentifiers.includes(journalAuditIdentifier)) {
          return false;
        }
        journalAuditCollectionIdentifiers.push(journalAuditIdentifier);
        return true;
      });
      return [...journalAuditsToAdd, ...journalAuditCollection];
    }
    return journalAuditCollection;
  }

  protected convertValueFromClient<T extends IJournalAudit | NewJournalAudit | PartialUpdateJournalAudit>(journalAudit: T): RestOf<T> {
    return {
      ...journalAudit,
      dateAction: journalAudit.dateAction?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestJournalAudit): IJournalAudit {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestJournalAudit[]): IJournalAudit[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
