import dayjs from 'dayjs/esm';

import { NgClass } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { TranslateDirective } from 'app/shared/language';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IJournalAudit } from 'app/entities/journal-audit/journal-audit.model';
import { JournalAuditService } from 'app/entities/journal-audit/service/journal-audit.service';
import { IRapportExport } from 'app/entities/rapport-export/rapport-export.model';
import { RapportExportService } from 'app/entities/rapport-export/service/rapport-export.service';

interface MessageAudit {
  type: 'success' | 'danger' | 'info';
  key: string;
}

const ACTIONS_SENSIBLES = new Set([
  'CHANGEMENT_PRIX',
  'CHANGEMENT_TAUX_REDEVANCE',
  'CHANGEMENT_CODE_BARRES',
  'VENTE_ANNULEE',
  'RETOUR_VENTE',
  'MOUVEMENT_STOCK',
  'PARAMETRAGE',
  'EXPORT',
]);

const ACTIONS_SUCCES = new Set(['CONNEXION', 'CREATION', 'VENTE_VALIDEE']);
const ACTIONS_DANGER = new Set(['DESACTIVATION', 'VENTE_ANNULEE']);
const ACTIONS_AVERTISSEMENT = new Set(['DECONNEXION', 'RETOUR_VENTE']);

@Component({
  selector: 'jhi-audit-supervision',
  templateUrl: './audit-supervision.html',
  styleUrl: './audit-supervision.scss',
  imports: [NgClass, FormsModule, RouterLink, TranslateDirective, TranslateModule, FontAwesomeModule],
})
export default class AuditSupervisionComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);

  readonly boutiques = signal<IBoutique[]>([]);
  readonly journaux = signal<IJournalAudit[]>([]);
  readonly exportsRecents = signal<IRapportExport[]>([]);
  readonly chargement = signal(false);
  readonly message = signal<MessageAudit | null>(null);

  readonly boutiqueId = signal<number | null>(null);
  readonly typeAction = signal<string>('');
  readonly dateDebut = signal<string>(dayjs().subtract(7, 'day').format('YYYY-MM-DD'));
  readonly dateFin = signal<string>(dayjs().format('YYYY-MM-DD'));
  readonly recherche = signal('');

  /** Locataire : perimetre restreint aux boutiques de ses utilisateurs (lecture seule). Admin et manager ADM : vue globale. */
  readonly estVueLocataire = computed(() => this.permissionsUi.estLocataire());

  readonly typesActions = [
    'CONNEXION',
    'CREATION',
    'MODIFICATION',
    'DESACTIVATION',
    'CHANGEMENT_PRIX',
    'CHANGEMENT_TAUX_REDEVANCE',
    'CHANGEMENT_CODE_BARRES',
    'VENTE_VALIDEE',
    'VENTE_ANNULEE',
    'RETOUR_VENTE',
    'MOUVEMENT_STOCK',
    'INVENTAIRE',
    'PARAMETRAGE',
    'EXPORT',
  ];

  readonly journauxFiltres = computed(() => {
    const texte = this.recherche().trim().toLowerCase();
    const debut = this.dateDebut();
    const fin = this.dateFin();

    return this.journaux().filter(evenement => {
      if (this.boutiqueId() && evenement.boutique?.id !== this.boutiqueId()) {
        return false;
      }
      if (this.typeAction() && evenement.typeAction !== this.typeAction()) {
        return false;
      }
      if (debut && evenement.dateAction?.isBefore(dayjs(debut), 'day')) {
        return false;
      }
      if (fin && evenement.dateAction?.isAfter(dayjs(fin).endOf('day'))) {
        return false;
      }
      if (!texte) {
        return true;
      }

      const haystack = [
        evenement.typeAction,
        evenement.entiteConcernee,
        evenement.identifiantEntite,
        evenement.description,
        evenement.utilisateur?.login,
        evenement.boutique?.nom,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return haystack.includes(texte);
    });
  });
  readonly evenementsSensibles = computed(() =>
    this.journauxFiltres().filter(evenement => ACTIONS_SENSIBLES.has(evenement.typeAction ?? '')),
  );
  readonly boutiquesImpactees = computed(
    () =>
      new Set(
        this.journauxFiltres()
          .map(item => item.boutique?.id)
          .filter(Boolean),
      ).size,
  );
  readonly resume = computed(() => ({
    totalEvenements: this.journauxFiltres().length,
    totalSensibles: this.evenementsSensibles().length,
    exportsRecents: this.exportsRecents().length,
    boutiquesImpactees: this.boutiquesImpactees(),
  }));

  private readonly boutiqueService = inject(BoutiqueService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);
  private readonly journalAuditService = inject(JournalAuditService);
  private readonly rapportExportService = inject(RapportExportService);

  ngOnInit(): void {
    void this.recharger();
  }

  formatDate(valeur: dayjs.Dayjs | null | undefined): string {
    return valeur ? valeur.format('DD/MM/YYYY HH:mm') : '--';
  }

  classePourAction(typeAction: string | null | undefined): string {
    const action = typeAction ?? '';
    if (ACTIONS_DANGER.has(action)) {
      return 'audit-pill--danger';
    }
    if (ACTIONS_AVERTISSEMENT.has(action)) {
      return 'audit-pill--warning';
    }
    if (ACTIONS_SUCCES.has(action)) {
      return 'audit-pill--success';
    }
    if (ACTIONS_SENSIBLES.has(action)) {
      return 'audit-pill--info';
    }
    return 'audit-pill--neutral';
  }

  estActionSensible(typeAction: string | null | undefined): boolean {
    return ACTIONS_SENSIBLES.has(typeAction ?? '');
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);

    try {
      const exportsRequest = this.permissionsUi.peutExporterReporting()
        ? this.queryOptionnelle(() => firstValueFrom(this.rapportExportService.query({ size: 20, sort: ['dateGeneration,desc'] })))
        : Promise.resolve(null);

      const perimetreLocataire = this.estVueLocataire() ? this.permissionsUi.boutiqueIds() : null;
      const utilisateursLocataire = perimetreLocataire ? await this.chargerUtilisateurIdsLocataire(perimetreLocataire) : null;
      const boutiqueQueryParams = perimetreLocataire
        ? perimetreLocataire.length
          ? { size: 500, sort: ['nom,asc'], 'id.in': perimetreLocataire.join(',') }
          : { size: 500, sort: ['nom,asc'], 'id.equals': -1 }
        : { size: 500, sort: ['nom,asc'] };
      const journalQueryParams = perimetreLocataire
        ? perimetreLocataire.length
          ? {
              size: 500,
              sort: ['dateAction,desc'],
              'boutiqueId.in': perimetreLocataire.join(','),
              'utilisateurId.in': utilisateursLocataire?.length ? utilisateursLocataire.join(',') : '-1',
            }
          : { size: 500, sort: ['dateAction,desc'], 'boutiqueId.equals': -1 }
        : { size: 500, sort: ['dateAction,desc'] };

      const [boutiquesResponse, journauxResponse, exportsResponse] = await Promise.all([
        this.queryOptionnelle(() => firstValueFrom(this.boutiqueService.query(boutiqueQueryParams))),
        firstValueFrom(this.journalAuditService.query(journalQueryParams)),
        exportsRequest,
      ]);

      this.boutiques.set(boutiquesResponse?.body ?? []);
      this.journaux.set(journauxResponse.body ?? []);
      this.exportsRecents.set(exportsResponse?.body ?? []);
    } catch {
      this.message.set({
        type: 'danger',
        key: 'auditSupervision.messages.loadFailed',
      });
    } finally {
      this.chargement.set(false);
    }
  }

  private async queryOptionnelle<T>(requete: () => Promise<T>): Promise<T | null> {
    try {
      return await requete();
    } catch {
      return null;
    }
  }

  private async chargerUtilisateurIdsLocataire(boutiqueIds: number[]): Promise<number[]> {
    if (!boutiqueIds.length) {
      return [];
    }

    const affectationsResponse = await this.queryOptionnelle(() =>
      firstValueFrom(
        this.affectationUtilisateurService.query({
          size: 500,
          sort: ['user.login,asc'],
          'actif.equals': true,
          'boutiqueId.in': boutiqueIds.join(','),
        }),
      ),
    );

    return Array.from(
      new Set((affectationsResponse?.body ?? []).map(affectation => affectation.user?.id).filter((id): id is number => !!id)),
    );
  }
}
