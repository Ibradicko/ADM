import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAffectationUtilisateur, NewAffectationUtilisateur } from '../affectation-utilisateur.model';

export type PartialUpdateAffectationUtilisateur = Partial<IAffectationUtilisateur> & Pick<IAffectationUtilisateur, 'id'>;

type RestOf<T extends IAffectationUtilisateur | NewAffectationUtilisateur> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

export type RestAffectationUtilisateur = RestOf<IAffectationUtilisateur>;

export type NewRestAffectationUtilisateur = RestOf<NewAffectationUtilisateur>;

export type PartialUpdateRestAffectationUtilisateur = RestOf<PartialUpdateAffectationUtilisateur>;

@Injectable()
export class AffectationUtilisateursService {
  readonly affectationUtilisateursParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly affectationUtilisateursResource = httpResource<RestAffectationUtilisateur[]>(() => {
    const params = this.affectationUtilisateursParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of affectationUtilisateur that have been fetched. It is updated when the affectationUtilisateursResource emits a new value.
   * In case of error while fetching the affectationUtilisateurs, the signal is set to an empty array.
   */
  readonly affectationUtilisateurs = computed(() =>
    (this.affectationUtilisateursResource.hasValue() ? this.affectationUtilisateursResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/affectation-utilisateurs');

  protected convertValueFromServer(restAffectationUtilisateur: RestAffectationUtilisateur): IAffectationUtilisateur {
    return {
      ...restAffectationUtilisateur,
      dateDebut: restAffectationUtilisateur.dateDebut ? dayjs(restAffectationUtilisateur.dateDebut) : undefined,
      dateFin: restAffectationUtilisateur.dateFin ? dayjs(restAffectationUtilisateur.dateFin) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class AffectationUtilisateurService extends AffectationUtilisateursService {
  protected readonly http = inject(HttpClient);

  create(affectationUtilisateur: NewAffectationUtilisateur): Observable<IAffectationUtilisateur> {
    const copy = this.convertValueFromClient(affectationUtilisateur);
    return this.http.post<RestAffectationUtilisateur>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(affectationUtilisateur: IAffectationUtilisateur): Observable<IAffectationUtilisateur> {
    const copy = this.convertValueFromClient(affectationUtilisateur);
    return this.http
      .put<RestAffectationUtilisateur>(
        `${this.resourceUrl}/${encodeURIComponent(this.getAffectationUtilisateurIdentifier(affectationUtilisateur))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(affectationUtilisateur: PartialUpdateAffectationUtilisateur): Observable<IAffectationUtilisateur> {
    const copy = this.convertValueFromClient(affectationUtilisateur);
    return this.http
      .patch<RestAffectationUtilisateur>(
        `${this.resourceUrl}/${encodeURIComponent(this.getAffectationUtilisateurIdentifier(affectationUtilisateur))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAffectationUtilisateur> {
    return this.http
      .get<RestAffectationUtilisateur>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAffectationUtilisateur[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAffectationUtilisateur[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAffectationUtilisateurIdentifier(affectationUtilisateur: Pick<IAffectationUtilisateur, 'id'>): number {
    return affectationUtilisateur.id;
  }

  compareAffectationUtilisateur(o1: Pick<IAffectationUtilisateur, 'id'> | null, o2: Pick<IAffectationUtilisateur, 'id'> | null): boolean {
    return o1 && o2 ? this.getAffectationUtilisateurIdentifier(o1) === this.getAffectationUtilisateurIdentifier(o2) : o1 === o2;
  }

  addAffectationUtilisateurToCollectionIfMissing<Type extends Pick<IAffectationUtilisateur, 'id'>>(
    affectationUtilisateurCollection: Type[],
    ...affectationUtilisateursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const affectationUtilisateurs: Type[] = affectationUtilisateursToCheck.filter(isPresent);
    if (affectationUtilisateurs.length > 0) {
      const affectationUtilisateurCollectionIdentifiers = affectationUtilisateurCollection.map(affectationUtilisateurItem =>
        this.getAffectationUtilisateurIdentifier(affectationUtilisateurItem),
      );
      const affectationUtilisateursToAdd = affectationUtilisateurs.filter(affectationUtilisateurItem => {
        const affectationUtilisateurIdentifier = this.getAffectationUtilisateurIdentifier(affectationUtilisateurItem);
        if (affectationUtilisateurCollectionIdentifiers.includes(affectationUtilisateurIdentifier)) {
          return false;
        }
        affectationUtilisateurCollectionIdentifiers.push(affectationUtilisateurIdentifier);
        return true;
      });
      return [...affectationUtilisateursToAdd, ...affectationUtilisateurCollection];
    }
    return affectationUtilisateurCollection;
  }

  protected convertValueFromClient<T extends IAffectationUtilisateur | NewAffectationUtilisateur | PartialUpdateAffectationUtilisateur>(
    affectationUtilisateur: T,
  ): RestOf<T> {
    return {
      ...affectationUtilisateur,
      dateDebut: affectationUtilisateur.dateDebut?.format(DATE_FORMAT) ?? null,
      dateFin: affectationUtilisateur.dateFin?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAffectationUtilisateur): IAffectationUtilisateur {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAffectationUtilisateur[]): IAffectationUtilisateur[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
