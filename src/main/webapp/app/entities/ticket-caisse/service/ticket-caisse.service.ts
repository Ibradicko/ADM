import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITicketCaisse, NewTicketCaisse } from '../ticket-caisse.model';

export type PartialUpdateTicketCaisse = Partial<ITicketCaisse> & Pick<ITicketCaisse, 'id'>;

type RestOf<T extends ITicketCaisse | NewTicketCaisse> = Omit<T, 'dateEmission'> & {
  dateEmission?: string | null;
};

export type RestTicketCaisse = RestOf<ITicketCaisse>;

export type NewRestTicketCaisse = RestOf<NewTicketCaisse>;

export type PartialUpdateRestTicketCaisse = RestOf<PartialUpdateTicketCaisse>;

@Injectable()
export class TicketCaissesService {
  readonly ticketCaissesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ticketCaissesResource = httpResource<RestTicketCaisse[]>(() => {
    const params = this.ticketCaissesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ticketCaisse that have been fetched. It is updated when the ticketCaissesResource emits a new value.
   * In case of error while fetching the ticketCaisses, the signal is set to an empty array.
   */
  readonly ticketCaisses = computed(() =>
    (this.ticketCaissesResource.hasValue() ? this.ticketCaissesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ticket-caisses');

  protected convertValueFromServer(restTicketCaisse: RestTicketCaisse): ITicketCaisse {
    return {
      ...restTicketCaisse,
      dateEmission: restTicketCaisse.dateEmission ? dayjs(restTicketCaisse.dateEmission) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TicketCaisseService extends TicketCaissesService {
  protected readonly http = inject(HttpClient);

  create(ticketCaisse: NewTicketCaisse): Observable<ITicketCaisse> {
    const copy = this.convertValueFromClient(ticketCaisse);
    return this.http.post<RestTicketCaisse>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ticketCaisse: ITicketCaisse): Observable<ITicketCaisse> {
    const copy = this.convertValueFromClient(ticketCaisse);
    return this.http
      .put<RestTicketCaisse>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketCaisseIdentifier(ticketCaisse))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ticketCaisse: PartialUpdateTicketCaisse): Observable<ITicketCaisse> {
    const copy = this.convertValueFromClient(ticketCaisse);
    return this.http
      .patch<RestTicketCaisse>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketCaisseIdentifier(ticketCaisse))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITicketCaisse> {
    return this.http
      .get<RestTicketCaisse>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITicketCaisse[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTicketCaisse[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTicketCaisseIdentifier(ticketCaisse: Pick<ITicketCaisse, 'id'>): number {
    return ticketCaisse.id;
  }

  compareTicketCaisse(o1: Pick<ITicketCaisse, 'id'> | null, o2: Pick<ITicketCaisse, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketCaisseIdentifier(o1) === this.getTicketCaisseIdentifier(o2) : o1 === o2;
  }

  addTicketCaisseToCollectionIfMissing<Type extends Pick<ITicketCaisse, 'id'>>(
    ticketCaisseCollection: Type[],
    ...ticketCaissesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ticketCaisses: Type[] = ticketCaissesToCheck.filter(isPresent);
    if (ticketCaisses.length > 0) {
      const ticketCaisseCollectionIdentifiers = ticketCaisseCollection.map(ticketCaisseItem =>
        this.getTicketCaisseIdentifier(ticketCaisseItem),
      );
      const ticketCaissesToAdd = ticketCaisses.filter(ticketCaisseItem => {
        const ticketCaisseIdentifier = this.getTicketCaisseIdentifier(ticketCaisseItem);
        if (ticketCaisseCollectionIdentifiers.includes(ticketCaisseIdentifier)) {
          return false;
        }
        ticketCaisseCollectionIdentifiers.push(ticketCaisseIdentifier);
        return true;
      });
      return [...ticketCaissesToAdd, ...ticketCaisseCollection];
    }
    return ticketCaisseCollection;
  }

  protected convertValueFromClient<T extends ITicketCaisse | NewTicketCaisse | PartialUpdateTicketCaisse>(ticketCaisse: T): RestOf<T> {
    return {
      ...ticketCaisse,
      dateEmission: ticketCaisse.dateEmission?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTicketCaisse): ITicketCaisse {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTicketCaisse[]): ITicketCaisse[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
