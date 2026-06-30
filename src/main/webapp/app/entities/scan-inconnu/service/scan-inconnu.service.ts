import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IScanInconnu, NewScanInconnu } from '../scan-inconnu.model';

export type PartialUpdateScanInconnu = Partial<IScanInconnu> & Pick<IScanInconnu, 'id'>;

type RestOf<T extends IScanInconnu | NewScanInconnu> = Omit<T, 'dateScan'> & {
  dateScan?: string | null;
};

export type RestScanInconnu = RestOf<IScanInconnu>;

export type NewRestScanInconnu = RestOf<NewScanInconnu>;

export type PartialUpdateRestScanInconnu = RestOf<PartialUpdateScanInconnu>;

@Injectable()
export class ScanInconnusService {
  readonly scanInconnusParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly scanInconnusResource = httpResource<RestScanInconnu[]>(() => {
    const params = this.scanInconnusParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of scanInconnu that have been fetched. It is updated when the scanInconnusResource emits a new value.
   * In case of error while fetching the scanInconnus, the signal is set to an empty array.
   */
  readonly scanInconnus = computed(() =>
    (this.scanInconnusResource.hasValue() ? this.scanInconnusResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/scan-inconnus');

  protected convertValueFromServer(restScanInconnu: RestScanInconnu): IScanInconnu {
    return {
      ...restScanInconnu,
      dateScan: restScanInconnu.dateScan ? dayjs(restScanInconnu.dateScan) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ScanInconnuService extends ScanInconnusService {
  protected readonly http = inject(HttpClient);

  create(scanInconnu: NewScanInconnu): Observable<IScanInconnu> {
    const copy = this.convertValueFromClient(scanInconnu);
    return this.http.post<RestScanInconnu>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(scanInconnu: IScanInconnu): Observable<IScanInconnu> {
    const copy = this.convertValueFromClient(scanInconnu);
    return this.http
      .put<RestScanInconnu>(`${this.resourceUrl}/${encodeURIComponent(this.getScanInconnuIdentifier(scanInconnu))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(scanInconnu: PartialUpdateScanInconnu): Observable<IScanInconnu> {
    const copy = this.convertValueFromClient(scanInconnu);
    return this.http
      .patch<RestScanInconnu>(`${this.resourceUrl}/${encodeURIComponent(this.getScanInconnuIdentifier(scanInconnu))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IScanInconnu> {
    return this.http
      .get<RestScanInconnu>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IScanInconnu[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestScanInconnu[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getScanInconnuIdentifier(scanInconnu: Pick<IScanInconnu, 'id'>): number {
    return scanInconnu.id;
  }

  compareScanInconnu(o1: Pick<IScanInconnu, 'id'> | null, o2: Pick<IScanInconnu, 'id'> | null): boolean {
    return o1 && o2 ? this.getScanInconnuIdentifier(o1) === this.getScanInconnuIdentifier(o2) : o1 === o2;
  }

  addScanInconnuToCollectionIfMissing<Type extends Pick<IScanInconnu, 'id'>>(
    scanInconnuCollection: Type[],
    ...scanInconnusToCheck: (Type | null | undefined)[]
  ): Type[] {
    const scanInconnus: Type[] = scanInconnusToCheck.filter(isPresent);
    if (scanInconnus.length > 0) {
      const scanInconnuCollectionIdentifiers = scanInconnuCollection.map(scanInconnuItem => this.getScanInconnuIdentifier(scanInconnuItem));
      const scanInconnusToAdd = scanInconnus.filter(scanInconnuItem => {
        const scanInconnuIdentifier = this.getScanInconnuIdentifier(scanInconnuItem);
        if (scanInconnuCollectionIdentifiers.includes(scanInconnuIdentifier)) {
          return false;
        }
        scanInconnuCollectionIdentifiers.push(scanInconnuIdentifier);
        return true;
      });
      return [...scanInconnusToAdd, ...scanInconnuCollection];
    }
    return scanInconnuCollection;
  }

  protected convertValueFromClient<T extends IScanInconnu | NewScanInconnu | PartialUpdateScanInconnu>(scanInconnu: T): RestOf<T> {
    return {
      ...scanInconnu,
      dateScan: scanInconnu.dateScan?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestScanInconnu): IScanInconnu {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestScanInconnu[]): IScanInconnu[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
