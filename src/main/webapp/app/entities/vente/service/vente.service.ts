import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneVente } from 'app/entities/ligne-vente/ligne-vente.model';
import { IPaiementVente } from 'app/entities/paiement-vente/paiement-vente.model';
import { ITicketCaisse } from 'app/entities/ticket-caisse/ticket-caisse.model';
import { IVente, NewVente } from '../vente.model';

export type PartialUpdateVente = Partial<IVente> & Pick<IVente, 'id'>;

type RestOf<T extends IVente | NewVente> = Omit<T, 'dateHeure'> & {
  dateHeure?: string | null;
};

export type RestVente = RestOf<IVente>;

export type NewRestVente = RestOf<NewVente>;

export type PartialUpdateRestVente = RestOf<PartialUpdateVente>;

export interface CaisseVenteLignePayload {
  produitId: number;
  quantite: number;
  remise?: number | null;
  codeBarresScanne?: string | null;
}

export interface CaisseVentePaiementPayload {
  modePaiementId: number;
  montant: number;
  referencePaiement?: string | null;
}

export interface CaisseVentePayload {
  boutiqueId: number;
  locataireId: number;
  referencePassager?: string | null;
  referenceCarteEmbarquement?: string | null;
  commentaire?: string | null;
  lignes: CaisseVenteLignePayload[];
  paiements: CaisseVentePaiementPayload[];
}

type RestCaisseVenteResult = Omit<CaisseVenteResult, 'vente' | 'ticket' | 'paiements'> & {
  vente: RestVente;
  ticket: Omit<ITicketCaisse, 'dateEmission'> & { dateEmission?: string | null };
  paiements: (Omit<IPaiementVente, 'datePaiement'> & { datePaiement?: string | null })[];
};

export interface CaisseVenteResult {
  vente: IVente;
  ticket: ITicketCaisse;
  lignes: ILigneVente[];
  paiements: IPaiementVente[];
}

export interface CaissePosteBoutique {
  id: number;
  nom?: string | null;
}

export interface CaissePosteLocataire {
  id: number;
  nom?: string | null;
}

export interface CaissePosteArticle {
  produitId: number;
  codeInterne?: string | null;
  designation?: string | null;
  description?: string | null;
  prixVente?: number | null;
  groupeArticleId?: number | null;
  groupeArticleLibelle?: string | null;
  stockDisponible?: number | null;
}

export interface CaissePosteModePaiement {
  id: number;
  code?: string | null;
  libelle?: string | null;
  actif?: boolean | null;
}

export interface CaissePosteContexte {
  boutique: CaissePosteBoutique;
  boutiquesAccessibles: CaissePosteBoutique[];
  locataire: CaissePosteLocataire | null;
  articles: CaissePosteArticle[];
  modesPaiement: CaissePosteModePaiement[];
}

@Injectable()
export class VentesService {
  readonly ventesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly ventesResource = httpResource<RestVente[]>(() => {
    const params = this.ventesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of vente that have been fetched. It is updated when the ventesResource emits a new value.
   * In case of error while fetching the ventes, the signal is set to an empty array.
   */
  readonly ventes = computed(() =>
    (this.ventesResource.hasValue() ? this.ventesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ventes');

  protected convertValueFromServer(restVente: RestVente): IVente {
    return {
      ...restVente,
      dateHeure: restVente.dateHeure ? dayjs(restVente.dateHeure) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class VenteService extends VentesService {
  protected readonly http = inject(HttpClient);

  create(vente: NewVente): Observable<IVente> {
    const copy = this.convertValueFromClient(vente);
    return this.http.post<RestVente>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(vente: IVente): Observable<IVente> {
    const copy = this.convertValueFromClient(vente);
    return this.http
      .put<RestVente>(`${this.resourceUrl}/${encodeURIComponent(this.getVenteIdentifier(vente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(vente: PartialUpdateVente): Observable<IVente> {
    const copy = this.convertValueFromClient(vente);
    return this.http
      .patch<RestVente>(`${this.resourceUrl}/${encodeURIComponent(this.getVenteIdentifier(vente))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  checkout(payload: CaisseVentePayload): Observable<CaisseVenteResult> {
    return this.http
      .post<RestCaisseVenteResult>(`${this.resourceUrl}/checkout`, payload)
      .pipe(map(res => this.convertCheckoutResponseFromServer(res)));
  }

  getContextePoste(boutiqueId?: number | null): Observable<CaissePosteContexte> {
    const params: Record<string, number> = boutiqueId ? { boutiqueId } : {};
    return this.http.get<CaissePosteContexte>(`${this.resourceUrl}/poste-caisse`, { params });
  }

  find(id: number): Observable<IVente> {
    return this.http.get<RestVente>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IVente[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestVente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getVenteIdentifier(vente: Pick<IVente, 'id'>): number {
    return vente.id;
  }

  compareVente(o1: Pick<IVente, 'id'> | null, o2: Pick<IVente, 'id'> | null): boolean {
    return o1 && o2 ? this.getVenteIdentifier(o1) === this.getVenteIdentifier(o2) : o1 === o2;
  }

  addVenteToCollectionIfMissing<Type extends Pick<IVente, 'id'>>(
    venteCollection: Type[],
    ...ventesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ventes: Type[] = ventesToCheck.filter(isPresent);
    if (ventes.length > 0) {
      const venteCollectionIdentifiers = venteCollection.map(venteItem => this.getVenteIdentifier(venteItem));
      const ventesToAdd = ventes.filter(venteItem => {
        const venteIdentifier = this.getVenteIdentifier(venteItem);
        if (venteCollectionIdentifiers.includes(venteIdentifier)) {
          return false;
        }
        venteCollectionIdentifiers.push(venteIdentifier);
        return true;
      });
      return [...ventesToAdd, ...venteCollection];
    }
    return venteCollection;
  }

  protected convertValueFromClient<T extends IVente | NewVente | PartialUpdateVente>(vente: T): RestOf<T> {
    return {
      ...vente,
      dateHeure: vente.dateHeure?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestVente): IVente {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestVente[]): IVente[] {
    return res.map(item => this.convertValueFromServer(item));
  }

  private convertCheckoutResponseFromServer(res: RestCaisseVenteResult): CaisseVenteResult {
    return {
      ...res,
      vente: this.convertValueFromServer(res.vente),
      ticket: {
        ...res.ticket,
        dateEmission: res.ticket.dateEmission ? dayjs(res.ticket.dateEmission) : undefined,
      },
      paiements: res.paiements.map(paiement => ({
        ...paiement,
        datePaiement: paiement.datePaiement ? dayjs(paiement.datePaiement) : undefined,
      })),
    };
  }
}
