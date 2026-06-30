import { Injectable, computed, effect, inject, signal } from '@angular/core';

import { firstValueFrom } from 'rxjs';

import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';

const PERMISSIONS = {
  userManage: 'USER_MANAGE',
  userRead: 'USER_READ',
  userCreate: 'USER_CREATE',
  userUpdate: 'USER_UPDATE',
  userDeactivate: 'USER_DEACTIVATE',
  salesRead: 'SALES_READ',
  salesManage: 'SALES_MANAGE',
  stockRead: 'STOCK_READ',
  stockManage: 'STOCK_MANAGE',
  reportingRead: 'REPORTING_READ',
  reportingExport: 'REPORTING_EXPORT',
  royaltyRead: 'ROYALTY_READ',
  royaltyManage: 'ROYALTY_MANAGE',
  settingsRead: 'SETTINGS_READ',
  settingsManage: 'SETTINGS_MANAGE',
  auditRead: 'AUDIT_READ',
} as const;

const CODES_PROFIL_GESTION = new Set(['MANAGER_ADM']);
const CODES_PROFIL_SUPERVISION = new Set(['MANAGER_ADM']);
const CODES_PROFIL_BOUTIQUE = new Set(['MANAGER_BOUTIQUE']);
const CODES_PROFIL_VENTE = new Set(['VENDEUR']);
const CODES_PROFIL_STANDARD = new Set(['MANAGER_BOUTIQUE', 'VENDEUR']);
const AUTORITE_LOCATAIRE = 'ROLE_LOCATAIRE';

@Injectable({ providedIn: 'root' })
export class UiPermissionService {
  readonly chargement = signal(false);
  readonly permissionsMetier = signal<Set<string>>(new Set());
  readonly codesProfil = signal<Set<string>>(new Set());
  readonly boutiqueIds = signal<number[]>([]);

  readonly estAdmin = computed(() => this.accountService.hasAnyAuthority('ROLE_ADMIN'));
  readonly estLocataire = computed(() => this.accountService.hasAnyAuthority(AUTORITE_LOCATAIRE));
  readonly estProfilAdm = computed(() => this.hasAnyProfile(CODES_PROFIL_GESTION));
  readonly estProfilSupervision = computed(() => this.hasAnyProfile(CODES_PROFIL_SUPERVISION));
  readonly estProfilBoutique = computed(() => !this.estLocataire() && this.hasAnyProfile(CODES_PROFIL_BOUTIQUE));
  readonly estProfilVente = computed(() => this.hasAnyProfile(CODES_PROFIL_VENTE));
  readonly estProfilStandard = computed(() => this.hasAnyProfile(CODES_PROFIL_STANDARD));
  readonly peutLireUtilisateurs = computed(() => this.estAdmin() || this.estProfilAdm() || this.estLocataire() || this.estProfilBoutique());
  readonly peutCreerUtilisateurBoutique = computed(
    () =>
      this.estAdmin() ||
      this.estProfilAdm() ||
      (this.estLocataire() && this.boutiqueIds().length > 0) ||
      (this.estProfilBoutique() && this.boutiqueIds().length > 0),
  );
  readonly peutGererAffectationsBoutique = computed(() => this.estAdmin() || this.estProfilAdm());
  readonly peutGererUtilisateurs = this.peutGererAffectationsBoutique;
  readonly peutLireVentes = computed(() => this.estAdmin() || this.estProfilAdm() || this.estProfilBoutique() || this.estProfilVente());
  readonly peutGererVentes = computed(() => this.estAdmin() || this.estProfilBoutique() || this.estProfilVente());
  readonly peutLireStock = computed(() => this.estAdmin() || this.estProfilBoutique());
  readonly peutGererStock = computed(() => this.estAdmin() || this.estProfilBoutique());
  readonly peutLireReporting = computed(
    () => this.estAdmin() || (this.estLocataire() && this.boutiqueIds().length > 0) || this.estProfilAdm() || this.estProfilBoutique(),
  );
  readonly peutExporterReporting = computed(
    () => this.estAdmin() || (this.estProfilAdm() && this.hasAnyBusinessPermission(PERMISSIONS.reportingExport)),
  );
  readonly peutLireRedevances = computed(
    () =>
      this.estAdmin() ||
      (this.estLocataire() && this.boutiqueIds().length > 0) ||
      this.estProfilBoutique() ||
      ((this.estProfilAdm() || this.estProfilBoutique()) &&
        this.hasAnyBusinessPermission(PERMISSIONS.royaltyManage, PERMISSIONS.royaltyRead)),
  );
  readonly peutGererRedevances = computed(
    () => this.estAdmin() || (this.estProfilAdm() && this.hasAnyBusinessPermission(PERMISSIONS.royaltyManage)),
  );
  readonly peutLireParametres = computed(() => this.estAdmin() || this.estProfilAdm());
  readonly peutGererParametres = computed(() => this.estAdmin() || this.estProfilAdm());
  readonly peutLireAudit = computed(
    () =>
      this.estAdmin() ||
      (this.estProfilAdm() && this.hasAnyBusinessPermission(PERMISSIONS.auditRead, PERMISSIONS.reportingRead, PERMISSIONS.reportingExport)),
  );
  readonly peutCreerBoutiques = computed(() => this.estAdmin());
  readonly peutModifierBoutiques = computed(() => this.estAdmin() || this.estProfilBoutique());
  readonly peutGererBoutiques = this.peutCreerBoutiques;
  readonly peutGererArticlesBoutique = computed(() => this.estProfilBoutique());
  readonly peutGererReferentielArticles = computed(() => this.estAdmin() || this.estProfilAdm());
  readonly peutGererCatalogue = computed(() => this.estAdmin() || this.estProfilAdm() || this.estProfilBoutique());
  readonly peutAdministrerParametresGlobaux = computed(() => this.estAdmin() || this.estProfilSupervision());
  readonly peutVoirArchivesTechniques = computed(() => this.estAdmin() || this.estProfilAdm());

  private readonly accountService = inject(AccountService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);
  private readonly profilMetierService = inject(ProfilMetierService);
  private readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);

  private versionChargement = 0;

  constructor() {
    effect(() => {
      const compte = this.accountService.account();
      void this.rechargerPermissions(compte);
    });
  }

  async chargerPermissions(compte: Account | null): Promise<void> {
    await this.rechargerPermissions(compte);
  }

  peutVoirEcran(
    cle:
      | 'dashboard'
      | 'boutiques'
      | 'exploitations'
      | 'locataires'
      | 'mes-boutiques'
      | 'produits'
      | 'caisse'
      | 'stock'
      | 'redevances'
      | 'reporting'
      | 'audit'
      | 'users'
      | 'settings'
      | 'boutiqueManagement',
  ): boolean {
    switch (cle) {
      case 'dashboard':
        return true;
      case 'boutiques':
        return this.estAdmin() || this.estProfilAdm() || this.estProfilBoutique();
      case 'exploitations':
        return this.estAdmin() || this.estProfilAdm();
      case 'locataires':
        return this.estAdmin() || this.estProfilSupervision();
      case 'mes-boutiques':
        return this.estLocataire();
      case 'boutiqueManagement':
        return this.peutModifierBoutiques();
      case 'produits':
        return this.peutGererCatalogue();
      case 'caisse':
        return this.peutGererVentes();
      case 'stock':
        return this.peutLireStock() || this.peutGererStock();
      case 'redevances':
        return this.peutLireRedevances() || this.peutGererRedevances();
      case 'reporting':
        return this.peutLireReporting() || this.peutExporterReporting();
      case 'audit':
        return this.peutLireAudit();
      case 'users':
        return this.peutLireUtilisateurs() || this.peutCreerUtilisateurBoutique();
      case 'settings':
        return this.peutLireParametres();
      default:
        return false;
    }
  }

  peutModifierFeature(feature: string, url = ''): boolean {
    if (url.includes('/journal-audit') || url.includes('/historique-code-barres')) {
      return false;
    }
    if (url.includes('/rapport-export')) {
      return this.peutExporterReporting();
    }
    if (url.includes('/parametre-code-barres') || url.includes('/mode-paiement-ref') || url.includes('/parametre-global')) {
      return this.peutAdministrerParametresGlobaux() && this.peutGererParametres();
    }
    if (
      url.includes('/groupe-article') ||
      url.includes('/famille-article') ||
      url.includes('/sous-famille-article') ||
      url.includes('/regle-redevance')
    ) {
      return this.peutGererReferentielArticles();
    }
    if (
      url.includes('/produit') ||
      url.includes('/tarif-produit') ||
      url.includes('/code-barres-produit') ||
      url.includes('/etiquette-produit')
    ) {
      return this.peutGererArticlesBoutique();
    }

    switch (feature) {
      case 'boutiques':
        return url.endsWith('/new') ? this.peutCreerBoutiques() : this.peutModifierBoutiques();
      case 'exploitations':
        return this.peutAdministrerParametresGlobaux();
      case 'locataires':
        return this.estAdmin() || this.estProfilAdm();
      case 'produits':
        return this.peutGererArticlesBoutique();
      case 'caisse':
        return this.peutGererVentes();
      case 'stock':
        return this.peutGererStock();
      case 'redevances':
        return this.peutGererRedevances();
      case 'reporting':
        return this.peutExporterReporting();
      case 'users':
        return this.peutGererAffectationsBoutique() || this.peutCreerUtilisateurBoutique();
      case 'settings':
        return this.peutAdministrerParametresGlobaux() && this.peutGererParametres();
      default:
        return false;
    }
  }

  peutAccederRouteTechnique(url: string): boolean {
    const routeTechnique = [
      '/vente',
      '/ligne-vente',
      '/paiement-vente',
      '/operation-corrective-vente',
      '/ticket-caisse',
      '/rapport-export',
    ].some(prefix => url === prefix || url.startsWith(`${prefix}/`) || url.startsWith(`${prefix}?`));

    return !routeTechnique || this.peutVoirArchivesTechniques();
  }

  routeAccueilAutorisee(): string {
    const routesParPriorite: Array<[string, Parameters<UiPermissionService['peutVoirEcran']>[0]]> = [
      ['/dashboard', 'dashboard'],
      ['/mes-boutiques', 'mes-boutiques'],
      ['/caisse', 'caisse'],
      ['/stock-operations', 'stock'],
      ['/reporting', 'reporting'],
      ['/catalogue-identification', 'produits'],
      ['/royalties', 'redevances'],
      ['/audit-supervision', 'audit'],
      ['/settings-center', 'users'],
      ['/settings-center', 'settings'],
    ];

    return routesParPriorite.find(([, feature]) => this.peutVoirEcran(feature))?.[0] ?? '/account/settings';
  }

  private hasAnyProfile(codes: Set<string>): boolean {
    const profils = this.codesProfil();
    return Array.from(profils).some(code => codes.has(code));
  }

  private hasAnyBusinessPermission(...codes: string[]): boolean {
    const profils = this.codesProfil();
    const permissions = this.permissionsMetier();
    return Array.from(profils).some(code => CODES_PROFIL_GESTION.has(code)) || codes.some(code => permissions.has(code));
  }

  private async rechargerPermissions(compte: Account | null): Promise<void> {
    const version = ++this.versionChargement;

    if (!compte?.id) {
      this.permissionsMetier.set(new Set());
      this.codesProfil.set(new Set());
      this.boutiqueIds.set([]);
      return;
    }

    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.permissionsMetier.set(new Set(Object.values(PERMISSIONS)));
      this.codesProfil.set(new Set(['ADMINISTRATEUR']));
      this.boutiqueIds.set([]);
      return;
    }

    this.chargement.set(true);

    try {
      const affectationsResponse = await firstValueFrom(
        this.affectationUtilisateurService.query({
          'userId.equals': compte.id,
          'actif.equals': true,
          size: 500,
          sort: ['dateDebut,desc'],
        }),
      );

      if (version !== this.versionChargement) {
        return;
      }

      const affectations = affectationsResponse.body ?? [];
      const profilIds = Array.from(new Set(affectations.map(affectation => affectation.profil?.id).filter((id): id is number => !!id)));
      const boutiqueIds = new Set(affectations.map(affectation => affectation.boutique?.id).filter((id): id is number => !!id));

      // Un locataire doit toujours voir ses boutiques sous contrat actif, meme si l'affectation
      // MANAGER_BOUTIQUE automatique n'a pas (encore) ete creee pour son propre compte.
      if (compte.authorities?.includes(AUTORITE_LOCATAIRE)) {
        try {
          const exploitations = await firstValueFrom(this.exploitationBoutiqueService.findMesExploitations());
          if (version === this.versionChargement) {
            exploitations
              .filter(exploitation => exploitation.statut === 'ACTIF')
              .forEach(exploitation => {
                if (exploitation.boutique?.id) {
                  boutiqueIds.add(exploitation.boutique.id);
                }
              });
          }
        } catch {
          // pas de contrat accessible : on garde uniquement les affectations explicites
        }
      }

      if (version !== this.versionChargement) {
        return;
      }

      this.boutiqueIds.set(Array.from(boutiqueIds));

      if (!profilIds.length) {
        this.permissionsMetier.set(new Set());
        this.codesProfil.set(new Set());
        return;
      }

      const profilsResponse = await firstValueFrom(
        this.profilMetierService.query({
          'id.in': profilIds.join(','),
          size: profilIds.length,
          sort: ['code,asc'],
        }),
      );

      if (version !== this.versionChargement) {
        return;
      }

      const profils = profilsResponse.body ?? [];
      const codesProfil = new Set(profils.map(profil => (profil.code ?? '').toUpperCase()).filter(Boolean));
      const permissions = new Set(
        profils
          .flatMap(profil => profil.permissionses ?? [])
          .map(permission => (permission.code ?? '').toUpperCase())
          .filter(Boolean),
      );

      this.codesProfil.set(codesProfil);
      this.permissionsMetier.set(permissions);
    } catch {
      if (version === this.versionChargement) {
        this.permissionsMetier.set(new Set());
        this.codesProfil.set(new Set());
        this.boutiqueIds.set([]);
      }
    } finally {
      if (version === this.versionChargement) {
        this.chargement.set(false);
      }
    }
  }
}
