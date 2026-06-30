import dayjs from 'dayjs/esm';

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { AccountService } from 'app/core/auth/account.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { IAffectationUtilisateur, NewAffectationUtilisateur } from 'app/entities/affectation-utilisateur/affectation-utilisateur.model';
import { AffectationUtilisateurService } from 'app/entities/affectation-utilisateur/service/affectation-utilisateur.service';
import { IBoutique, NewBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IUserManagement } from 'app/entities/admin/user-management/user-management.model';
import { UserManagementService } from 'app/entities/admin/user-management/service/user-management.service';
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypeBoutique } from 'app/entities/enumerations/type-boutique.model';
import { IModePaiementRef, NewModePaiementRef } from 'app/entities/mode-paiement-ref/mode-paiement-ref.model';
import { ModePaiementRefService } from 'app/entities/mode-paiement-ref/service/mode-paiement-ref.service';
import { IParametreCodeBarres, NewParametreCodeBarres } from 'app/entities/parametre-code-barres/parametre-code-barres.model';
import { ParametreCodeBarresService } from 'app/entities/parametre-code-barres/service/parametre-code-barres.service';
import { IParametreGlobal, NewParametreGlobal } from 'app/entities/parametre-global/parametre-global.model';
import { ParametreGlobalService } from 'app/entities/parametre-global/service/parametre-global.service';
import { IPermissionMetier } from 'app/entities/permission-metier/permission-metier.model';
import { PermissionMetierService } from 'app/entities/permission-metier/service/permission-metier.service';
import { IProfilMetier, NewProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { TranslateDirective } from 'app/shared/language';

interface MessageParametres {
  type: 'success' | 'danger' | 'info';
  key: string;
  params?: Record<string, string>;
}

interface GroupePermissions {
  module: string;
  permissions: IPermissionMetier[];
}

interface ParametreCatalogue {
  code: string;
  labelKey: string;
  descriptionKey: string;
  valeurDefaut: string;
  type: 'text' | 'number' | 'boolean';
}

const PARAMETRES_GLOBAUX_CONTROLES: ParametreCatalogue[] = [
  {
    code: 'DEVISE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultCurrency.label',
    descriptionKey: 'settingsCenter.parameters.defaultCurrency.description',
    valeurDefaut: 'XOF',
    type: 'text',
  },
  {
    code: 'TAUX_REDEVANCE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultRoyaltyRate.label',
    descriptionKey: 'settingsCenter.parameters.defaultRoyaltyRate.description',
    valeurDefaut: '10',
    type: 'number',
  },
  {
    code: 'STOCK_ALERTE_DEFAUT',
    labelKey: 'settingsCenter.parameters.defaultStockAlert.label',
    descriptionKey: 'settingsCenter.parameters.defaultStockAlert.description',
    valeurDefaut: '5',
    type: 'number',
  },
  {
    code: 'TICKET_PREFIXE',
    labelKey: 'settingsCenter.parameters.ticketPrefix.label',
    descriptionKey: 'settingsCenter.parameters.ticketPrefix.description',
    valeurDefaut: 'ADM',
    type: 'text',
  },
];

const PARAMETRES_BOUTIQUE_CONTROLES: ParametreCatalogue[] = [
  {
    code: 'REF_PASSAGER_OBLIGATOIRE',
    labelKey: 'settingsCenter.parameters.passengerReferenceRequired.label',
    descriptionKey: 'settingsCenter.parameters.passengerReferenceRequired.description',
    valeurDefaut: 'false',
    type: 'boolean',
  },
  {
    code: 'CARTE_EMBARQUEMENT_OBLIGATOIRE',
    labelKey: 'settingsCenter.parameters.boardingPassRequired.label',
    descriptionKey: 'settingsCenter.parameters.boardingPassRequired.description',
    valeurDefaut: 'false',
    type: 'boolean',
  },
  {
    code: 'TAUX_REDEVANCE_BOUTIQUE',
    labelKey: 'settingsCenter.parameters.shopRoyaltyRate.label',
    descriptionKey: 'settingsCenter.parameters.shopRoyaltyRate.description',
    valeurDefaut: '10',
    type: 'number',
  },
  {
    code: 'STOCK_ALERTE_BOUTIQUE',
    labelKey: 'settingsCenter.parameters.shopStockAlert.label',
    descriptionKey: 'settingsCenter.parameters.shopStockAlert.description',
    valeurDefaut: '5',
    type: 'number',
  },
];

@Component({
  selector: 'jhi-settings-center',
  templateUrl: './settings-center.html',
  imports: [FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class SettingsCenterComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);
  private readonly translateService = inject(TranslateService);
  readonly compte = inject(AccountService).account;

  readonly parametresGlobaux = signal<IParametreGlobal[]>([]);
  readonly modesPaiement = signal<IModePaiementRef[]>([]);
  readonly parametresCodeBarres = signal<IParametreCodeBarres[]>([]);
  readonly profils = signal<IProfilMetier[]>([]);
  readonly permissions = signal<IPermissionMetier[]>([]);
  readonly affectations = signal<IAffectationUtilisateur[]>([]);
  readonly boutiques = signal<IBoutique[]>([]);
  readonly utilisateurs = signal<IUserManagement[]>([]);
  readonly chargement = signal(false);
  readonly enregistrement = signal(false);
  readonly message = signal<MessageParametres | null>(null);
  readonly recherche = signal('');
  readonly onglet = signal<'global' | 'paiement' | 'barcode' | 'profils' | 'affectations' | 'referentiels'>('global');
  readonly porteeParametres = signal<'general' | 'boutique'>('general');
  readonly boutiqueParametresId = signal<number | null>(null);

  readonly valeursGlobales = signal<Record<string, string>>({});
  readonly actifsGlobaux = signal<Record<string, boolean>>({});
  readonly valeursBoutique = signal<Record<string, string>>({});
  readonly actifsBoutique = signal<Record<string, boolean>>({});

  readonly nouveauLibellePaiement = signal('');
  readonly nouveauPaiementActif = signal(true);

  readonly nouveauFormatBarcode = signal<'EAN13' | 'EAN8' | 'CODE128' | 'QR_CODE' | 'INTERNE'>('CODE128');
  readonly nouveauPrefixeBarcode = signal('ADM');
  readonly nouvelleLongueurBarcode = signal<number | null>(13);
  readonly nouveauBarcodeActif = signal(true);

  readonly nouveauLibelleProfil = signal('');
  readonly nouvelleDescriptionProfil = signal('');
  readonly nouveauProfilStatut = signal<'ACTIF' | 'INACTIF' | 'SUSPENDU'>('ACTIF');
  readonly permissionIdsProfil = signal<number[]>([]);

  readonly affectationUserId = signal<number | null>(null);
  readonly affectationBoutiqueId = signal<number | null>(null);
  readonly affectationProfilId = signal<number | null>(null);
  readonly rechercheBoutiqueParametres = signal('');
  readonly rechercheNouvelUtilisateurBoutique = signal('');
  readonly rechercheAffectationUtilisateur = signal('');
  readonly rechercheAffectationBoutique = signal('');
  readonly rechercheAffectationProfil = signal('');
  readonly affectationDateDebut = signal(dayjs().format(DATE_FORMAT));
  readonly affectationDateFin = signal('');
  readonly affectationActive = signal(true);

  readonly nouvelleBoutiqueNom = signal('');
  readonly nouvelleBoutiqueType = signal<keyof typeof TypeBoutique>('COMMERCE');
  readonly nouvelleBoutiqueEmplacement = signal('');
  readonly nouvelleBoutiqueTelephone = signal('');

  readonly nouvelUtilisateurPrenom = signal('');
  readonly nouvelUtilisateurNom = signal('');
  readonly nouvelUtilisateurLogin = signal('');
  readonly nouvelUtilisateurEmail = signal('');
  readonly nouvelUtilisateurBoutiqueId = signal<number | null>(null);
  readonly nouvelUtilisateurProfilId = signal<number | null>(null);

  readonly formatsBarcode = ['EAN13', 'EAN8', 'CODE128', 'QR_CODE', 'INTERNE'] as const;
  readonly typesBoutique = Object.values(TypeBoutique);

  readonly parametresGlobauxControles = PARAMETRES_GLOBAUX_CONTROLES;
  readonly parametresBoutiqueControles = PARAMETRES_BOUTIQUE_CONTROLES;
  readonly codePaiementAuto = computed(() => this.genererCode(this.nouveauLibellePaiement(), 'PAY'));
  readonly prefixeBarcodeAuto = computed(() => this.genererCode(this.nouveauPrefixeBarcode(), 'ADM').slice(0, 20));
  readonly codeProfilAuto = computed(() => this.genererCode(this.nouveauLibelleProfil(), 'PROFIL'));

  readonly parametresGlobauxFiltres = computed(() => {
    const texte = this.recherche().trim().toLowerCase();
    return this.parametresGlobaux().filter(parametre => {
      if (!texte) {
        return true;
      }
      return [parametre.code, parametre.valeur, parametre.description].filter(Boolean).join(' ').toLowerCase().includes(texte);
    });
  });
  readonly profilsActifs = computed(() => this.profils().filter(profil => profil.statut === 'ACTIF'));
  readonly estGestionAdm = computed(() => this.permissionsUi.estAdmin() || this.permissionsUi.codesProfil().has('MANAGER_ADM'));
  readonly profilsUtilisateursBoutique = computed(() => {
    if (this.permissionsUi.estAdmin()) {
      return this.profilsActifs();
    }
    if (this.permissionsUi.codesProfil().has('MANAGER_ADM')) {
      return this.profilsActifs().filter(profil => (profil.code ?? '').toUpperCase() !== 'ADMINISTRATEUR');
    }

    const codesAutorises = this.permissionsUi.estLocataire() ? ['MANAGER_BOUTIQUE', 'VENDEUR'] : ['VENDEUR'];
    return this.profilsActifs().filter(profil => codesAutorises.includes((profil.code ?? '').toUpperCase()));
  });
  readonly peutCreerBoutique = computed(() => this.permissionsUi.estAdmin() || this.permissionsUi.codesProfil().has('MANAGER_ADM'));
  readonly peutCreerUtilisateurBoutique = this.permissionsUi.peutCreerUtilisateurBoutique;
  readonly peutGererAffectationsBoutique = this.permissionsUi.peutGererAffectationsBoutique;
  readonly peutAdministrerGlobal = this.permissionsUi.peutAdministrerParametresGlobaux;
  readonly permissionsTriees = computed(() =>
    [...this.permissions()].sort((a, b) => `${a.module ?? ''}-${a.code ?? ''}`.localeCompare(`${b.module ?? ''}-${b.code ?? ''}`)),
  );
  readonly permissionsParModule = computed<GroupePermissions[]>(() => {
    const groupes = new Map<string, IPermissionMetier[]>();
    for (const permission of this.permissionsTriees()) {
      const module = permission.module ?? 'AUTRE';
      groupes.set(module, [...(groupes.get(module) ?? []), permission]);
    }
    return Array.from(groupes.entries()).map(([module, permissions]) => ({ module, permissions }));
  });
  readonly affectationsVisibles = computed(() =>
    this.affectations().filter(affectation => {
      const codeProfil = (affectation.profil?.code ?? '').toUpperCase();
      if (this.permissionsUi.estProfilBoutique()) {
        return codeProfil === 'VENDEUR';
      }
      if (this.permissionsUi.estLocataire()) {
        return ['MANAGER_BOUTIQUE', 'VENDEUR'].includes(codeProfil);
      }
      return true;
    }),
  );
  readonly affectationsActives = computed(() => this.affectationsVisibles().filter(affectation => affectation.actif));
  readonly parametresActifs = computed(() => this.parametresGlobaux().filter(parametre => parametre.actif).length);
  readonly boutiquesParametrables = computed(() => {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.codesProfil().has('MANAGER_ADM')) {
      return this.boutiques();
    }

    const idsAccessibles = new Set(this.permissionsUi.boutiqueIds());
    return this.boutiques().filter(boutique => idsAccessibles.has(boutique.id));
  });
  readonly boutiqueSelectionnee = computed(() => this.boutiques().find(boutique => boutique.id === this.boutiqueParametresId()) ?? null);
  readonly boutiquesParametresFiltrees = computed(() =>
    this.filtrerBoutiques(this.boutiquesParametrables(), this.rechercheBoutiqueParametres()),
  );
  readonly boutiquesNouvelUtilisateurFiltrees = computed(() =>
    this.filtrerBoutiques(this.boutiquesParametrables(), this.rechercheNouvelUtilisateurBoutique()),
  );
  readonly boutiquesAffectationFiltrees = computed(() =>
    this.filtrerBoutiques(this.boutiquesParametrables(), this.rechercheAffectationBoutique()),
  );
  readonly utilisateursAffectationFiltres = computed(() =>
    this.filtrerUtilisateurs(this.utilisateurs(), this.rechercheAffectationUtilisateur()),
  );
  readonly profilsAffectationDisponibles = computed(() => this.profilsUtilisateursBoutique());
  readonly profilsAffectationFiltres = computed(() =>
    this.filtrerProfils(this.profilsAffectationDisponibles(), this.rechercheAffectationProfil()),
  );

  private readonly accountService = inject(AccountService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly parametreGlobalService = inject(ParametreGlobalService);
  private readonly modePaiementRefService = inject(ModePaiementRefService);
  private readonly parametreCodeBarresService = inject(ParametreCodeBarresService);
  private readonly profilMetierService = inject(ProfilMetierService);
  private readonly permissionMetierService = inject(PermissionMetierService);
  private readonly affectationUtilisateurService = inject(AffectationUtilisateurService);
  private readonly boutiqueService = inject(BoutiqueService);
  private readonly userManagementService = inject(UserManagementService);

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe(params => {
      this.appliquerOngletDepuisUrl(params.get('onglet'));
      void this.recharger();
    });
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);

    try {
      const compte = this.accountService.account();
      const affectationParams = this.permissionsUi.peutLireUtilisateurs()
        ? {
            ...this.paramsBoutiquesAccessibles(),
            size: 500,
            sort: ['actif,desc', 'dateDebut,desc'],
          }
        : compte?.id
          ? { 'userId.equals': compte.id, size: 100, sort: ['dateDebut,desc'] }
          : { size: 0 };

      const [
        parametresResponse,
        modesResponse,
        barcodeResponse,
        profilsResponse,
        permissionsResponse,
        affectationsResponse,
        boutiquesResponse,
        utilisateursResponse,
      ] = await Promise.all([
        this.peutAdministrerGlobal()
          ? firstValueFrom(this.parametreGlobalService.query({ size: 300, sort: ['code,asc'] }))
          : Promise.resolve({ body: [] }),
        this.peutAdministrerGlobal()
          ? firstValueFrom(this.modePaiementRefService.query({ size: 200, sort: ['libelle,asc'] }))
          : Promise.resolve({ body: [] }),
        this.peutAdministrerGlobal()
          ? firstValueFrom(this.parametreCodeBarresService.query({ size: 50, sort: ['id,desc'] }))
          : Promise.resolve({ body: [] }),
        firstValueFrom(this.profilMetierService.query({ size: 200, sort: ['code,asc'] })),
        this.peutAdministrerGlobal()
          ? firstValueFrom(this.permissionMetierService.query({ size: 500, sort: ['module,asc', 'code,asc'] }))
          : Promise.resolve({ body: [] }),
        firstValueFrom(this.affectationUtilisateurService.query(affectationParams)),
        firstValueFrom(this.boutiqueService.query({ ...this.paramsIdsBoutiquesAccessibles(), size: 500, sort: ['nom,asc'] })),
        this.peutGererAffectationsBoutique()
          ? firstValueFrom(this.userManagementService.query({ page: 0, size: 500, sort: ['login,asc'] }))
          : Promise.resolve({ body: [] }),
      ]);

      this.parametresGlobaux.set(parametresResponse.body ?? []);
      this.modesPaiement.set(modesResponse.body ?? []);
      this.parametresCodeBarres.set(barcodeResponse.body ?? []);
      this.profils.set(profilsResponse.body ?? []);
      this.permissions.set(permissionsResponse.body ?? []);
      this.affectations.set(affectationsResponse.body ?? []);
      this.boutiques.set(boutiquesResponse.body ?? []);
      this.utilisateurs.set(utilisateursResponse.body ?? []);
      this.synchroniserValeursParametres();
      if (!this.boutiqueParametresId() && this.boutiquesParametrables().length) {
        this.boutiqueParametresId.set(this.boutiquesParametrables()[0].id);
        this.synchroniserValeursParametresBoutique();
      }
      if (!this.nouvelUtilisateurBoutiqueId() && this.boutiquesParametrables().length) {
        this.nouvelUtilisateurBoutiqueId.set(this.boutiquesParametrables()[0].id);
      }
      if (!this.nouvelUtilisateurProfilId() && this.profilsUtilisateursBoutique().length) {
        this.nouvelUtilisateurProfilId.set(this.profilsUtilisateursBoutique()[0].id);
      }
      if (
        this.nouvelUtilisateurProfilId() &&
        !this.profilsUtilisateursBoutique().some(profil => profil.id === this.nouvelUtilisateurProfilId())
      ) {
        this.nouvelUtilisateurProfilId.set(this.profilsUtilisateursBoutique()[0]?.id ?? null);
      }
    } catch {
      this.message.set({
        type: 'danger',
        key: 'settingsCenter.messages.loadFailed',
      });
    } finally {
      this.chargement.set(false);
    }
  }

  async enregistrerParametreGlobal(definition: ParametreCatalogue): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }
    const valeur = this.valeursGlobales()[definition.code]?.trim();
    if (!valeur) {
      this.message.set({
        type: 'danger',
        key: 'settingsCenter.messages.parameterValueRequired',
        params: { parameter: this.translateService.instant(definition.labelKey) },
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      const existant = this.parametresGlobaux().find(parametre => parametre.code === definition.code);
      const actif = this.actifsGlobaux()[definition.code] ?? true;
      if (existant) {
        await firstValueFrom(
          this.parametreGlobalService.partialUpdate({
            id: existant.id,
            code: existant.code,
            valeur,
            actif,
            description: this.translateService.instant(definition.descriptionKey),
          }),
        );
      } else {
        await firstValueFrom(
          this.parametreGlobalService.create({
            id: null,
            code: definition.code,
            valeur,
            description: this.translateService.instant(definition.descriptionKey),
            actif,
          }),
        );
      }
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.globalParameterUpdated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.globalParameterUpdateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async enregistrerParametreBoutique(definition: ParametreCatalogue): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }
    const boutique = this.boutiqueSelectionnee();
    if (!boutique) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.selectShop' });
      return;
    }

    const code = this.codeParametreBoutique(boutique.id, definition.code);
    const valeur = this.valeursBoutique()[definition.code]?.trim();
    if (!valeur) {
      this.message.set({
        type: 'danger',
        key: 'settingsCenter.messages.parameterValueRequired',
        params: { parameter: this.translateService.instant(definition.labelKey) },
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      const existant = this.parametresGlobaux().find(parametre => parametre.code === code);
      const actif = this.actifsBoutique()[definition.code] ?? true;
      const description = `${boutique.nom ?? this.translateService.instant('settingsCenter.common.shopWithId', { id: boutique.id })} - ${this.translateService.instant(definition.descriptionKey)}`;
      if (existant) {
        await firstValueFrom(
          this.parametreGlobalService.partialUpdate({
            id: existant.id,
            code: existant.code,
            valeur,
            actif,
            description,
          }),
        );
      } else {
        await firstValueFrom(
          this.parametreGlobalService.create({
            id: null,
            code,
            valeur,
            description,
            actif,
          }),
        );
      }
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.shopParameterUpdated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.shopParameterUpdateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerModePaiement(): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }
    if (!this.nouveauLibellePaiement().trim()) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.paymentLabelRequired' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const payload: NewModePaiementRef = {
        id: null,
        code: this.codePaiementAuto(),
        libelle: this.nouveauLibellePaiement().trim(),
        actif: this.nouveauPaiementActif(),
      };
      await firstValueFrom(this.modePaiementRefService.create(payload));
      this.nouveauLibellePaiement.set('');
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.paymentModeAdded' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.paymentModeCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerParametreBarcode(): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }
    if (!this.nouvelleLongueurBarcode()) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.barcodeLengthRequired' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const payload: NewParametreCodeBarres = {
        id: null,
        formatParDefaut: this.nouveauFormatBarcode(),
        prefixe: this.prefixeBarcodeAuto(),
        longueur: this.nouvelleLongueurBarcode(),
        actif: this.nouveauBarcodeActif(),
      };
      await firstValueFrom(this.parametreCodeBarresService.create(payload));
      this.nouveauPrefixeBarcode.set('ADM');
      this.nouvelleLongueurBarcode.set(13);
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.barcodeRuleAdded' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.barcodeRuleCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerProfil(): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }
    if (!this.nouveauLibelleProfil().trim()) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.profileLabelRequired' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const permissionIds = new Set(this.permissionIdsProfil());
      const payload: NewProfilMetier = {
        id: null,
        code: this.codeProfilAuto(),
        libelle: this.nouveauLibelleProfil().trim(),
        description: this.nouvelleDescriptionProfil().trim() || null,
        statut: this.nouveauProfilStatut(),
        permissionses: this.permissions()
          .filter(permission => permissionIds.has(permission.id))
          .map(permission => ({ id: permission.id, code: permission.code })),
      };
      await firstValueFrom(this.profilMetierService.create(payload));
      this.nouveauLibelleProfil.set('');
      this.nouvelleDescriptionProfil.set('');
      this.permissionIdsProfil.set([]);
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.profileCreated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.profileCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerBoutique(): Promise<void> {
    if (!this.peutCreerBoutique()) {
      return;
    }

    if (!this.nouvelleBoutiqueNom().trim()) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.shopRequired' });
      return;
    }
    if (!this.telephoneMaliValide(this.nouvelleBoutiqueTelephone())) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.maliPhoneInvalid' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const boutiquePayload: NewBoutique = {
        id: null,
        code: this.genererCode(this.nouvelleBoutiqueNom(), 'BTQ'),
        nom: this.nouvelleBoutiqueNom().trim(),
        type: this.nouvelleBoutiqueType(),
        emplacement: this.nouvelleBoutiqueEmplacement().trim() || null,
        telephone: this.nouvelleBoutiqueTelephone().trim() || null,
        statut: StatutGeneral.ACTIF,
        dateCreation: dayjs(),
      };
      await firstValueFrom(this.boutiqueService.create(boutiquePayload));
      this.reinitialiserFormulaireBoutique();
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.shopCreated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.shopCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerUtilisateurBoutique(): Promise<void> {
    if (!this.permissionsUi.peutCreerUtilisateurBoutique()) {
      return;
    }

    const boutique = this.boutiquesParametrables().find(item => item.id === this.nouvelUtilisateurBoutiqueId());
    const profil = this.profilsUtilisateursBoutique().find(item => item.id === this.nouvelUtilisateurProfilId());
    if (!boutique || !profil || !this.nouvelUtilisateurLogin().trim() || !this.nouvelUtilisateurEmail().trim()) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.shopUserRequired' });
      return;
    }
    if (!this.emailValide(this.nouvelUtilisateurEmail())) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.userEmailInvalid' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const utilisateur = await this.creerCompteUtilisateur({
        login: this.nouvelUtilisateurLogin(),
        email: this.nouvelUtilisateurEmail(),
        firstName: this.nouvelUtilisateurPrenom(),
        lastName: this.nouvelUtilisateurNom(),
        boutiqueId: boutique.id,
        profilId: profil.id,
      });
      if (this.estGestionAdm()) {
        await this.creerAffectationUtilisateur(utilisateur, boutique, profil);
      }
      this.reinitialiserFormulaireUtilisateurBoutique();
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.shopUserCreated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.shopUserCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerAffectation(): Promise<void> {
    if (!this.permissionsUi.peutGererAffectationsBoutique()) {
      return;
    }

    const utilisateur = this.utilisateurs().find(user => user.id === this.affectationUserId());
    const boutique = this.boutiquesParametrables().find(item => item.id === this.affectationBoutiqueId());
    const profil = this.profilsAffectationDisponibles().find(item => item.id === this.affectationProfilId());

    if (!utilisateur?.id || !utilisateur.login || !boutique || !profil) {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.assignmentRequired' });
      return;
    }

    this.enregistrement.set(true);
    try {
      const payload: NewAffectationUtilisateur = {
        id: null,
        dateDebut: dayjs(this.affectationDateDebut(), DATE_FORMAT),
        dateFin: this.affectationDateFin().trim() ? dayjs(this.affectationDateFin(), DATE_FORMAT) : null,
        actif: this.affectationActive(),
        user: { id: utilisateur.id, login: utilisateur.login },
        boutique: { id: boutique.id, nom: boutique.nom },
        profil: { id: profil.id, code: profil.code },
      };
      await firstValueFrom(this.affectationUtilisateurService.create(payload));
      this.affectationUserId.set(null);
      this.affectationBoutiqueId.set(null);
      this.affectationProfilId.set(null);
      this.affectationDateFin.set('');
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.assignmentCreated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.assignmentCreateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async basculerActif(
    type: 'global' | 'paiement' | 'barcode',
    element: IParametreGlobal | IModePaiementRef | IParametreCodeBarres,
  ): Promise<void> {
    if (!this.peutAdministrerGlobal() || !this.permissionsUi.peutGererParametres()) {
      return;
    }

    this.enregistrement.set(true);
    try {
      if (type === 'global') {
        const parametre = element as IParametreGlobal;
        await firstValueFrom(
          this.parametreGlobalService.partialUpdate({ id: parametre.id, code: parametre.code, actif: !parametre.actif }),
        );
      } else if (type === 'paiement') {
        await firstValueFrom(this.modePaiementRefService.partialUpdate({ id: element.id, actif: !element.actif }));
      } else {
        await firstValueFrom(this.parametreCodeBarresService.partialUpdate({ id: element.id, actif: !element.actif }));
      }

      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.activeStatusUpdated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.activeStatusUpdateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async basculerAffectation(affectation: IAffectationUtilisateur): Promise<void> {
    if (!this.permissionsUi.peutGererAffectationsBoutique()) {
      return;
    }

    this.enregistrement.set(true);
    try {
      await firstValueFrom(this.affectationUtilisateurService.partialUpdate({ id: affectation.id, actif: !affectation.actif }));
      await this.recharger();
      this.message.set({ type: 'success', key: 'settingsCenter.messages.assignmentUpdated' });
    } catch {
      this.message.set({ type: 'danger', key: 'settingsCenter.messages.assignmentUpdateFailed' });
    } finally {
      this.enregistrement.set(false);
    }
  }

  permissionCochee(permissionId: number): boolean {
    return this.permissionIdsProfil().includes(permissionId);
  }

  libellePermission(permission: Pick<IPermissionMetier, 'id' | 'code'>): string {
    return this.permissions().find(item => item.id === permission.id)?.libelle ?? permission.code ?? 'Permission';
  }

  libelleProfil(code: string | null | undefined): string {
    return this.profils().find(profil => profil.code === code)?.libelle ?? code ?? '--';
  }

  basculerPermission(permissionId: number, coche: boolean): void {
    const ids = new Set(this.permissionIdsProfil());
    if (coche) {
      ids.add(permissionId);
    } else {
      ids.delete(permissionId);
    }
    this.permissionIdsProfil.set(Array.from(ids).sort((a, b) => a - b));
  }

  formatDate(valeur: dayjs.Dayjs | null | undefined): string {
    return valeur ? valeur.format('DD/MM/YYYY') : '--';
  }

  definirValeurGlobale(code: string, valeur: string): void {
    this.valeursGlobales.update(valeurs => ({ ...valeurs, [code]: valeur }));
  }

  definirActifGlobal(code: string, actif: boolean): void {
    this.actifsGlobaux.update(valeurs => ({ ...valeurs, [code]: actif }));
  }

  definirValeurBoutique(code: string, valeur: string): void {
    this.valeursBoutique.update(valeurs => ({ ...valeurs, [code]: valeur }));
  }

  definirActifBoutique(code: string, actif: boolean): void {
    this.actifsBoutique.update(valeurs => ({ ...valeurs, [code]: actif }));
  }

  changerBoutiqueParametres(boutiqueId: number | null): void {
    this.boutiqueParametresId.set(boutiqueId);
    this.synchroniserValeursParametresBoutique();
  }

  selectionnerBoutiqueParametres(boutiqueId: number | null, selecteur?: HTMLDetailsElement): void {
    this.changerBoutiqueParametres(boutiqueId);
    this.rechercheBoutiqueParametres.set('');
    this.fermerSelecteur(selecteur);
  }

  selectionnerNouvelUtilisateurBoutique(boutiqueId: number | null, selecteur?: HTMLDetailsElement): void {
    this.nouvelUtilisateurBoutiqueId.set(boutiqueId);
    this.rechercheNouvelUtilisateurBoutique.set('');
    this.fermerSelecteur(selecteur);
  }

  selectionnerAffectationUtilisateur(userId: number | null, selecteur?: HTMLDetailsElement): void {
    this.affectationUserId.set(userId);
    this.rechercheAffectationUtilisateur.set('');
    this.fermerSelecteur(selecteur);
  }

  selectionnerAffectationBoutique(boutiqueId: number | null, selecteur?: HTMLDetailsElement): void {
    this.affectationBoutiqueId.set(boutiqueId);
    this.rechercheAffectationBoutique.set('');
    this.fermerSelecteur(selecteur);
  }

  selectionnerAffectationProfil(profilId: number | null, selecteur?: HTMLDetailsElement): void {
    this.affectationProfilId.set(profilId);
    this.rechercheAffectationProfil.set('');
    this.fermerSelecteur(selecteur);
  }

  libelleBoutique(boutiqueId: number | null): string {
    const boutique = this.boutiques().find(item => item.id === boutiqueId);
    return boutique ? this.formatBoutique(boutique) : this.translateService.instant('settingsCenter.common.select');
  }

  libelleUtilisateur(userId: number | null): string {
    const utilisateur = this.utilisateurs().find(item => item.id === userId);
    return utilisateur ? this.formatUtilisateur(utilisateur) : this.translateService.instant('settingsCenter.common.select');
  }

  libelleProfilParId(profilId: number | null): string {
    const profil = this.profils().find(item => item.id === profilId);
    return profil ? this.formatProfil(profil) : this.translateService.instant('settingsCenter.common.select');
  }

  formatBoutique(boutique: IBoutique): string {
    return (
      [boutique.code, boutique.nom].filter(Boolean).join(' - ') ||
      this.translateService.instant('settingsCenter.common.shopWithId', { id: boutique.id })
    );
  }

  formatUtilisateur(user: IUserManagement): string {
    const nom = [user.firstName, user.lastName].filter(Boolean).join(' ').trim();
    return [user.login, nom].filter(Boolean).join(' - ');
  }

  formatProfil(profil: IProfilMetier): string {
    return [profil.code, profil.libelle].filter(Boolean).join(' - ');
  }

  codeParametreBoutique(boutiqueId: number, code: string): string {
    return `BOUTIQUE_${boutiqueId}_${code}`.slice(0, 80);
  }

  private synchroniserValeursParametres(): void {
    const prochainsGlobaux: Record<string, string> = {};
    const prochainsActifsGlobaux: Record<string, boolean> = {};
    for (const definition of PARAMETRES_GLOBAUX_CONTROLES) {
      const existant = this.parametresGlobaux().find(parametre => parametre.code === definition.code);
      prochainsGlobaux[definition.code] = existant?.valeur ?? definition.valeurDefaut;
      prochainsActifsGlobaux[definition.code] = existant?.actif ?? true;
    }
    this.valeursGlobales.set(prochainsGlobaux);
    this.actifsGlobaux.set(prochainsActifsGlobaux);
    this.synchroniserValeursParametresBoutique();
  }

  private appliquerOngletDepuisUrl(onglet: string | null): void {
    if (
      this.peutAdministrerGlobal() &&
      (onglet === 'global' || onglet === 'paiement' || onglet === 'barcode' || onglet === 'profils' || onglet === 'referentiels')
    ) {
      this.onglet.set(onglet);
    } else if (onglet === 'affectations' || !this.peutAdministrerGlobal()) {
      this.onglet.set('affectations');
    } else {
      this.onglet.set('global');
    }
  }

  private paramsBoutiquesAccessibles(): Record<string, string | number> {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm()) {
      return {};
    }

    const ids = this.permissionsUi.boutiqueIds();
    return ids.length ? { 'boutiqueId.in': ids.join(',') } : { 'boutiqueId.equals': -1 };
  }

  private paramsIdsBoutiquesAccessibles(): Record<string, string | number> {
    if (this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm()) {
      return {};
    }

    const ids = this.permissionsUi.boutiqueIds();
    return ids.length ? { 'id.in': ids.join(',') } : { 'id.equals': -1 };
  }

  private synchroniserValeursParametresBoutique(): void {
    const boutiqueId = this.boutiqueParametresId();
    const prochainesValeurs: Record<string, string> = {};
    const prochainsActifs: Record<string, boolean> = {};
    for (const definition of PARAMETRES_BOUTIQUE_CONTROLES) {
      const code = boutiqueId ? this.codeParametreBoutique(boutiqueId, definition.code) : '';
      const existant = code ? this.parametresGlobaux().find(parametre => parametre.code === code) : undefined;
      prochainesValeurs[definition.code] = existant?.valeur ?? definition.valeurDefaut;
      prochainsActifs[definition.code] = existant?.actif ?? true;
    }
    this.valeursBoutique.set(prochainesValeurs);
    this.actifsBoutique.set(prochainsActifs);
  }

  private genererCode(source: string, prefixe: string): string {
    const base = source
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toUpperCase()
      .replace(/[^A-Z0-9]+/g, '_')
      .replace(/^_+|_+$/g, '')
      .slice(0, 54);
    return `${prefixe}_${base || dayjs().format('YYYYMMDDHHmmss')}`.slice(0, 80);
  }

  private filtrerBoutiques(boutiques: IBoutique[], recherche: string): IBoutique[] {
    const texte = this.normaliserRecherche(recherche);
    if (!texte) {
      return boutiques;
    }
    return boutiques.filter(boutique =>
      this.normaliserRecherche([boutique.code, boutique.nom, boutique.emplacement, boutique.type].filter(Boolean).join(' ')).includes(
        texte,
      ),
    );
  }

  private filtrerUtilisateurs(utilisateurs: IUserManagement[], recherche: string): IUserManagement[] {
    const texte = this.normaliserRecherche(recherche);
    if (!texte) {
      return utilisateurs;
    }
    return utilisateurs.filter(user =>
      this.normaliserRecherche([user.login, user.firstName, user.lastName, user.email].filter(Boolean).join(' ')).includes(texte),
    );
  }

  private filtrerProfils(profils: IProfilMetier[], recherche: string): IProfilMetier[] {
    const texte = this.normaliserRecherche(recherche);
    if (!texte) {
      return profils;
    }
    return profils.filter(profil =>
      this.normaliserRecherche([profil.code, profil.libelle, profil.description].filter(Boolean).join(' ')).includes(texte),
    );
  }

  private normaliserRecherche(valeur: string): string {
    return valeur
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }

  private fermerSelecteur(selecteur?: HTMLDetailsElement): void {
    if (selecteur) {
      selecteur.open = false;
    }
  }

  telephoneMaliValide(valeur: string): boolean {
    const telephone = valeur.trim();
    return !telephone || /^\d{8}$/.test(telephone);
  }

  emailValide(valeur: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valeur.trim());
  }

  private async creerCompteUtilisateur(source: {
    login: string;
    email: string;
    firstName: string;
    lastName: string;
    boutiqueId: number;
    profilId: number;
  }): Promise<IUserManagement> {
    const login = source.login.trim().toLowerCase();
    const payload: IUserManagement = {
      login,
      email: source.email.trim().toLowerCase(),
      firstName: source.firstName.trim() || null,
      lastName: source.lastName.trim() || null,
      activated: true,
      langKey: 'fr',
      authorities: ['ROLE_USER'],
    };
    if (!this.estGestionAdm()) {
      return firstValueFrom(this.userManagementService.createForBoutique(payload, source.boutiqueId, source.profilId));
    }
    return firstValueFrom(this.userManagementService.create(payload));
  }

  private async creerAffectationUtilisateur(user: IUserManagement, boutique: IBoutique, profil: IProfilMetier): Promise<void> {
    if (!user.id) {
      throw new Error('Utilisateur cree sans identifiant.');
    }
    await firstValueFrom(
      this.affectationUtilisateurService.create({
        id: null,
        dateDebut: dayjs(this.affectationDateDebut() || dayjs().format(DATE_FORMAT), DATE_FORMAT),
        dateFin: null,
        actif: true,
        user: { id: user.id, login: user.login },
        boutique: { id: boutique.id, nom: boutique.nom },
        profil: { id: profil.id, code: profil.code },
      }),
    );
  }

  private reinitialiserFormulaireBoutique(): void {
    this.nouvelleBoutiqueNom.set('');
    this.nouvelleBoutiqueEmplacement.set('');
    this.nouvelleBoutiqueTelephone.set('');
  }

  private reinitialiserFormulaireUtilisateurBoutique(): void {
    this.nouvelUtilisateurPrenom.set('');
    this.nouvelUtilisateurNom.set('');
    this.nouvelUtilisateurLogin.set('');
    this.nouvelUtilisateurEmail.set('');
  }
}
