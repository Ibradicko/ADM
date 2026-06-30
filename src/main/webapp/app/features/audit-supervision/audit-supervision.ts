import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { TranslateDirective } from 'app/shared/language';
import { AdmDashboardReportingService, DashboardStockAlertResponse } from 'app/core/services/adm-dashboard-reporting.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IJournalAudit } from 'app/entities/journal-audit/journal-audit.model';
import { JournalAuditService } from 'app/entities/journal-audit/service/journal-audit.service';
import { IRapportExport } from 'app/entities/rapport-export/rapport-export.model';
import { RapportExportService } from 'app/entities/rapport-export/service/rapport-export.service';
import { IScanInconnu } from 'app/entities/scan-inconnu/scan-inconnu.model';
import { ScanInconnuService } from 'app/entities/scan-inconnu/service/scan-inconnu.service';

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

@Component({
  selector: 'jhi-audit-supervision',
  templateUrl: './audit-supervision.html',
  imports: [FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class AuditSupervisionComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);

  readonly boutiques = signal<IBoutique[]>([]);
  readonly journaux = signal<IJournalAudit[]>([]);
  readonly scansInconnus = signal<IScanInconnu[]>([]);
  readonly exportsRecents = signal<IRapportExport[]>([]);
  readonly alertesStock = signal<DashboardStockAlertResponse[]>([]);
  readonly chargement = signal(false);
  readonly message = signal<MessageAudit | null>(null);

  readonly boutiqueId = signal<number | null>(null);
  readonly typeAction = signal<string>('');
  readonly dateDebut = signal<string>(dayjs().subtract(7, 'day').format('YYYY-MM-DD'));
  readonly dateFin = signal<string>(dayjs().format('YYYY-MM-DD'));
  readonly recherche = signal('');
  readonly auditSelectionneId = signal<number | null>(null);

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
      if (debut && evenement.dateAction && evenement.dateAction.isBefore(dayjs(debut), 'day')) {
        return false;
      }
      if (fin && evenement.dateAction && evenement.dateAction.isAfter(dayjs(fin).endOf('day'))) {
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
  readonly auditSelectionne = computed(
    () => this.journaux().find(evenement => evenement.id === this.auditSelectionneId()) ?? this.journauxFiltres()[0] ?? null,
  );
  readonly evenementsSensibles = computed(() =>
    this.journauxFiltres().filter(evenement => ACTIONS_SENSIBLES.has(evenement.typeAction ?? '')),
  );
  readonly scansNonResolus = computed(() =>
    this.scansInconnus().filter(scan => !scan.resolu && (!this.boutiqueId() || scan.boutique?.id === this.boutiqueId())),
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
    scansNonResolus: this.scansNonResolus().length,
    alertesStock: this.alertesStock().length,
    exportsRecents: this.exportsRecents().length,
    boutiquesImpactees: this.boutiquesImpactees(),
  }));

  private readonly boutiqueService = inject(BoutiqueService);
  private readonly journalAuditService = inject(JournalAuditService);
  private readonly scanInconnuService = inject(ScanInconnuService);
  private readonly rapportExportService = inject(RapportExportService);
  private readonly dashboardReportingService = inject(AdmDashboardReportingService);

  ngOnInit(): void {
    void this.recharger();
  }

  formatDate(valeur: dayjs.Dayjs | null | undefined): string {
    return valeur ? valeur.format('DD/MM/YYYY HH:mm') : '--';
  }

  selectionnerAudit(id: number): void {
    this.auditSelectionneId.set(id);
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);

    try {
      const scansRequest = this.permissionsUi.peutLireStock()
        ? this.queryOptionnelle(() => firstValueFrom(this.scanInconnuService.query({ size: 200, sort: ['dateScan,desc'] })))
        : Promise.resolve(null);
      const exportsRequest = this.permissionsUi.peutExporterReporting()
        ? this.queryOptionnelle(() => firstValueFrom(this.rapportExportService.query({ size: 20, sort: ['dateGeneration,desc'] })))
        : Promise.resolve(null);
      const alertesStockRequest = this.permissionsUi.peutLireStock()
        ? this.queryOptionnelle(() =>
            firstValueFrom(
              this.dashboardReportingService.getStockAlerts({
                boutiqueId: this.boutiqueId() ?? undefined,
              }),
            ),
          )
        : Promise.resolve<DashboardStockAlertResponse[] | null>(null);

      const [boutiquesResponse, journauxResponse, scansResponse, exportsResponse, alertesStock] = await Promise.all([
        this.queryOptionnelle(() => firstValueFrom(this.boutiqueService.query({ size: 500, sort: ['nom,asc'] }))),
        firstValueFrom(this.journalAuditService.query({ size: 500, sort: ['dateAction,desc'] })),
        scansRequest,
        exportsRequest,
        alertesStockRequest,
      ]);

      this.boutiques.set(boutiquesResponse?.body ?? []);
      this.journaux.set(journauxResponse.body ?? []);
      this.scansInconnus.set(scansResponse?.body ?? []);
      this.exportsRecents.set(exportsResponse?.body ?? []);
      this.alertesStock.set(alertesStock ?? []);
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
}
