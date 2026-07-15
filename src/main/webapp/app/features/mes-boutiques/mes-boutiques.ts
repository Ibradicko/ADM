import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap/collapse';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { TranslateDirective } from 'app/shared/language';
import { AdmDashboardReportingService, DashboardOverviewResponse } from 'app/core/services/adm-dashboard-reporting.service';
import { IAffectationUtilisateur } from 'app/entities/affectation-utilisateur/affectation-utilisateur.model';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { UserManagementService } from 'app/entities/admin/user-management/service/user-management.service';
import { IExploitationBoutique } from 'app/entities/exploitation-boutique/exploitation-boutique.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { IUser } from 'app/entities/user/user.model';

interface BoutiquePanel {
  exploitation: IExploitationBoutique;
  affectations: IAffectationUtilisateur[];
  ouvert: boolean;
  chargementAffectations: boolean;
  formulaireOuvert: boolean;
  ajoutEnCours: boolean;
  nouvelUtilisateur: {
    login: string;
    firstName: string;
    lastName: string;
    email: string;
    profilId: number | null;
  };
  erreurAjout: string | null;
  statsChargement: boolean;
  stats: DashboardOverviewResponse | null;
  statsErreur: string | null;
}

interface MesBoutiquesMetric {
  labelKey: string;
  value: string;
  tone: 'primary' | 'success' | 'warning' | 'neutral';
}

@Component({
  selector: 'jhi-mes-boutiques',
  templateUrl: './mes-boutiques.html',
  styleUrl: './mes-boutiques.scss',
  imports: [FontAwesomeModule, FormsModule, NgbCollapseModule, TranslateModule, TranslateDirective],
})
export default class MesBoutiquesComponent implements OnInit {
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly panneaux = signal<BoutiquePanel[]>([]);
  readonly profilsDisponibles = signal<IProfilMetier[]>([]);

  readonly compte = inject(AccountService).account;
  readonly nomLocataire = computed(() => {
    const c = this.compte();
    return [c?.firstName, c?.lastName].filter(Boolean).join(' ') || c?.login || '';
  });
  readonly boutiquesActives = computed(() => this.panneaux().filter(panneau => panneau.exploitation.statut === 'ACTIF'));
  readonly totalMembres = computed(() => this.panneaux().reduce((total, panneau) => total + panneau.affectations.length, 0));
  readonly caNetCharge = computed(() => this.panneaux().reduce((total, panneau) => total + (panneau.stats?.netSales ?? 0), 0));
  readonly redevanceRestanteChargee = computed(() =>
    this.panneaux().reduce((total, panneau) => total + (panneau.stats?.royaltyOutstandingAmount ?? 0), 0),
  );
  readonly metrics = computed<MesBoutiquesMetric[]>(() => [
    {
      labelKey: 'mesBoutiques.metrics.activeShops',
      value: String(this.boutiquesActives().length),
      tone: 'success',
    },
    {
      labelKey: 'mesBoutiques.metrics.totalShops',
      value: String(this.panneaux().length),
      tone: 'primary',
    },
    {
      labelKey: 'mesBoutiques.metrics.teamMembers',
      value: String(this.totalMembres()),
      tone: 'neutral',
    },
    {
      labelKey: 'mesBoutiques.metrics.loadedNetSales',
      value: this.formatMontant(this.caNetCharge()),
      tone: 'success',
    },
    {
      labelKey: 'mesBoutiques.metrics.loadedRoyalties',
      value: this.formatMontant(this.redevanceRestanteChargee()),
      tone: 'warning',
    },
  ]);

  private readonly translateService = inject(TranslateService);
  private readonly exploitationService = inject(ExploitationBoutiqueService);
  private readonly affectationService = inject(AffectationUtilisateurService);
  private readonly profilService = inject(ProfilMetierService);
  private readonly userManagementService = inject(UserManagementService);
  private readonly dashboardReportingService = inject(AdmDashboardReportingService);

  ngOnInit(): void {
    void Promise.all([this.chargerMesExploitations(), this.chargerProfils()]);
  }

  async chargerMesExploitations(): Promise<void> {
    this.chargement.set(true);
    this.erreur.set(null);
    try {
      const exploitations = await firstValueFrom(this.exploitationService.findMesExploitations());
      this.panneaux.set(
        exploitations.map(exploitation => ({
          exploitation,
          affectations: [],
          ouvert: false,
          chargementAffectations: false,
          formulaireOuvert: false,
          ajoutEnCours: false,
          nouvelUtilisateur: { login: '', firstName: '', lastName: '', email: '', profilId: null },
          erreurAjout: null,
          statsChargement: false,
          stats: null,
          statsErreur: null,
        })),
      );
    } catch {
      this.erreur.set(this.translateService.instant('mesBoutiques.errors.loadFailed'));
    } finally {
      this.chargement.set(false);
    }
  }

  async togglePanneau(panneau: BoutiquePanel): Promise<void> {
    panneau.ouvert = !panneau.ouvert;
    this.panneaux.update(panneaux => [...panneaux]);
    if (panneau.ouvert) {
      await Promise.all([
        panneau.affectations.length ? Promise.resolve() : this.chargerAffectations(panneau),
        this.chargerStatistiques(panneau),
      ]);
    }
  }

  async chargerAffectations(panneau: BoutiquePanel): Promise<void> {
    panneau.chargementAffectations = true;
    this.panneaux.update(panneaux => [...panneaux]);
    try {
      const boutiqueId = panneau.exploitation.boutique?.id;
      if (!boutiqueId) {
        return;
      }
      const resp = await firstValueFrom(
        this.affectationService.query({ 'boutiqueId.equals': boutiqueId, 'actif.equals': true, size: 100 }),
      );
      panneau.affectations = (resp.body ?? []).filter(affectation => this.affectationVisiblePourLocataire(affectation));
    } catch {
      panneau.affectations = [];
    } finally {
      panneau.chargementAffectations = false;
      this.panneaux.update(panneaux => [...panneaux]);
    }
  }

  async chargerStatistiques(panneau: BoutiquePanel): Promise<void> {
    const boutiqueId = panneau.exploitation.boutique?.id;
    if (!boutiqueId) {
      return;
    }
    panneau.statsChargement = true;
    panneau.statsErreur = null;
    this.panneaux.update(panneaux => [...panneaux]);
    try {
      panneau.stats = await firstValueFrom(this.dashboardReportingService.getOverview({ boutiqueId }));
    } catch {
      panneau.stats = null;
      panneau.statsErreur = this.translateService.instant('mesBoutiques.errors.statsFailed');
    } finally {
      panneau.statsChargement = false;
      this.panneaux.update(panneaux => [...panneaux]);
    }
  }

  ouvrirFormulaire(panneau: BoutiquePanel): void {
    panneau.formulaireOuvert = true;
    panneau.nouvelUtilisateur = { login: '', firstName: '', lastName: '', email: '', profilId: null };
    panneau.erreurAjout = null;
    this.panneaux.update(panneaux => [...panneaux]);
  }

  fermerFormulaire(panneau: BoutiquePanel): void {
    panneau.formulaireOuvert = false;
    panneau.erreurAjout = null;
    this.panneaux.update(panneaux => [...panneaux]);
  }

  async ajouterResponsable(panneau: BoutiquePanel): Promise<void> {
    await this.creerNouvelUtilisateur(panneau);
  }

  async desactiverAffectation(panneau: BoutiquePanel, affectation: IAffectationUtilisateur): Promise<void> {
    if (this.estAffectationCompteCourant(affectation)) {
      return;
    }

    const login = affectation.user?.login ?? '-';
    if (!confirm(this.translateService.instant('mesBoutiques.confirmRemove', { login }))) {
      return;
    }
    try {
      await firstValueFrom(this.affectationService.delete(affectation.id));
      await this.chargerAffectations(panneau);
    } catch {
      /* no-op: the table stays unchanged when the API refuses the action. */
    }
  }

  nomUtilisateur(user: IUser | null | undefined): string {
    return [user?.firstName, user?.lastName].filter(Boolean).join(' ') || '-';
  }

  estAffectationCompteCourant(affectation: IAffectationUtilisateur): boolean {
    return !!this.compte()?.id && affectation.user?.id === this.compte()?.id;
  }

  profilAffiche(affectation: IAffectationUtilisateur): string {
    return this.estAffectationCompteCourant(affectation) ? 'LOCATAIRE' : (affectation.profil?.code ?? '-');
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '0 F CFA';
  }

  statutClasse(statut: string | null | undefined): string {
    if (statut === 'ACTIF') {
      return 'adm-pill adm-pill--success';
    }
    if (statut === 'SUSPENDU') {
      return 'adm-pill adm-pill--danger';
    }
    return 'adm-pill adm-pill--warning';
  }

  private async creerNouvelUtilisateur(panneau: BoutiquePanel): Promise<void> {
    const { login, firstName, lastName, email, profilId } = panneau.nouvelUtilisateur;
    const boutiqueId = panneau.exploitation.boutique?.id;
    if (!login.trim() || !email.trim() || !profilId || !boutiqueId) {
      panneau.erreurAjout = this.translateService.instant('mesBoutiques.errors.missingRequiredFields');
      this.panneaux.update(panneaux => [...panneaux]);
      return;
    }

    panneau.ajoutEnCours = true;
    panneau.erreurAjout = null;
    this.panneaux.update(panneaux => [...panneaux]);

    try {
      await firstValueFrom(
        this.userManagementService.createForBoutique(
          {
            login: login.trim(),
            firstName: firstName.trim() || undefined,
            lastName: lastName.trim() || undefined,
            email: email.trim(),
            langKey: 'fr',
            activated: true,
          },
          boutiqueId,
          profilId,
        ),
      );
      panneau.formulaireOuvert = false;
      await this.chargerAffectations(panneau);
    } catch (err: any) {
      const msg = err?.error?.detail ?? err?.error?.title ?? this.translateService.instant('mesBoutiques.errors.createFailed');
      panneau.erreurAjout = msg;
    } finally {
      panneau.ajoutEnCours = false;
      this.panneaux.update(panneaux => [...panneaux]);
    }
  }

  private async chargerProfils(): Promise<void> {
    try {
      const resp = await firstValueFrom(this.profilService.query({ size: 50 }));
      const all = resp.body ?? [];
      this.profilsDisponibles.set(all.filter(p => ['MANAGER_BOUTIQUE', 'VENDEUR'].includes((p.code ?? '').toUpperCase())));
    } catch {
      this.profilsDisponibles.set([]);
    }
  }

  private affectationVisiblePourLocataire(affectation: IAffectationUtilisateur): boolean {
    const codeProfil = (affectation.profil?.code ?? '').toUpperCase();
    return ['MANAGER_BOUTIQUE', 'VENDEUR'].includes(codeProfil);
  }
}
