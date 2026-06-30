import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRapportExport, NewRapportExport } from '../rapport-export.model';

export type PartialUpdateRapportExport = Partial<IRapportExport> & Pick<IRapportExport, 'id'>;

type RestOf<T extends IRapportExport | NewRapportExport> = Omit<T, 'periodeDebut' | 'periodeFin' | 'dateGeneration'> & {
  periodeDebut?: string | null;
  periodeFin?: string | null;
  dateGeneration?: string | null;
};

export type RestRapportExport = RestOf<IRapportExport>;

export type NewRestRapportExport = RestOf<NewRapportExport>;

export type PartialUpdateRestRapportExport = RestOf<PartialUpdateRapportExport>;

@Injectable()
export class RapportExportsService {
  readonly rapportExportsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rapportExportsResource = httpResource<RestRapportExport[]>(() => {
    const params = this.rapportExportsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rapportExport that have been fetched. It is updated when the rapportExportsResource emits a new value.
   * In case of error while fetching the rapportExports, the signal is set to an empty array.
   */
  readonly rapportExports = computed(() =>
    (this.rapportExportsResource.hasValue() ? this.rapportExportsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rapport-exports');

  protected convertValueFromServer(restRapportExport: RestRapportExport): IRapportExport {
    return {
      ...restRapportExport,
      periodeDebut: restRapportExport.periodeDebut ? dayjs(restRapportExport.periodeDebut) : undefined,
      periodeFin: restRapportExport.periodeFin ? dayjs(restRapportExport.periodeFin) : undefined,
      dateGeneration: restRapportExport.dateGeneration ? dayjs(restRapportExport.dateGeneration) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RapportExportService extends RapportExportsService {
  protected readonly http = inject(HttpClient);

  create(rapportExport: NewRapportExport): Observable<IRapportExport> {
    const copy = this.convertValueFromClient(rapportExport);
    return this.http.post<RestRapportExport>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(rapportExport: IRapportExport): Observable<IRapportExport> {
    const copy = this.convertValueFromClient(rapportExport);
    return this.http
      .put<RestRapportExport>(`${this.resourceUrl}/${encodeURIComponent(this.getRapportExportIdentifier(rapportExport))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(rapportExport: PartialUpdateRapportExport): Observable<IRapportExport> {
    const copy = this.convertValueFromClient(rapportExport);
    return this.http
      .patch<RestRapportExport>(`${this.resourceUrl}/${encodeURIComponent(this.getRapportExportIdentifier(rapportExport))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRapportExport> {
    return this.http
      .get<RestRapportExport>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRapportExport[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRapportExport[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRapportExportIdentifier(rapportExport: Pick<IRapportExport, 'id'>): number {
    return rapportExport.id;
  }

  compareRapportExport(o1: Pick<IRapportExport, 'id'> | null, o2: Pick<IRapportExport, 'id'> | null): boolean {
    return o1 && o2 ? this.getRapportExportIdentifier(o1) === this.getRapportExportIdentifier(o2) : o1 === o2;
  }

  addRapportExportToCollectionIfMissing<Type extends Pick<IRapportExport, 'id'>>(
    rapportExportCollection: Type[],
    ...rapportExportsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rapportExports: Type[] = rapportExportsToCheck.filter(isPresent);
    if (rapportExports.length > 0) {
      const rapportExportCollectionIdentifiers = rapportExportCollection.map(rapportExportItem =>
        this.getRapportExportIdentifier(rapportExportItem),
      );
      const rapportExportsToAdd = rapportExports.filter(rapportExportItem => {
        const rapportExportIdentifier = this.getRapportExportIdentifier(rapportExportItem);
        if (rapportExportCollectionIdentifiers.includes(rapportExportIdentifier)) {
          return false;
        }
        rapportExportCollectionIdentifiers.push(rapportExportIdentifier);
        return true;
      });
      return [...rapportExportsToAdd, ...rapportExportCollection];
    }
    return rapportExportCollection;
  }

  protected convertValueFromClient<T extends IRapportExport | NewRapportExport | PartialUpdateRapportExport>(rapportExport: T): RestOf<T> {
    return {
      ...rapportExport,
      periodeDebut: rapportExport.periodeDebut?.format(DATE_FORMAT) ?? null,
      periodeFin: rapportExport.periodeFin?.format(DATE_FORMAT) ?? null,
      dateGeneration: rapportExport.dateGeneration?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRapportExport): IRapportExport {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRapportExport[]): IRapportExport[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
