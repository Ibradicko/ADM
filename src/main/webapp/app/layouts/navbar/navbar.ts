import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Event, NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { LangChangeEvent, TranslateModule, TranslateService } from '@ngx-translate/core';
import { environment } from 'environments/environment';
import { filter } from 'rxjs';

import { LANGUAGES } from 'app/config/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';
import { FindLanguageFromKeyPipe, TranslateDirective } from 'app/shared/language';

interface NavItem {
  id: string;
  labelKey: string;
  route: string;
  icon: string;
  exact?: boolean;
  adminOnly?: boolean;
  technicalOnly?: boolean;
  queryParams?: Record<string, string>;
  feature?:
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
    | 'boutiqueManagement';
}

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownToggle,
    FindLanguageFromKeyPipe,
    TranslateDirective,
    TranslateModule,
  ],
})
export default class Navbar implements OnInit {
  readonly account = inject(AccountService).account;
  readonly inProduction = signal(true);
  readonly isNavbarCollapsed = signal(true);
  readonly openAPIEnabled = signal(false);
  readonly languages = LANGUAGES;
  readonly currentLanguage = signal('fr');
  readonly currentUrl = signal('');
  readonly version: string;
  readonly permissionsUi = inject(UiPermissionService);
  readonly menuItems: readonly NavItem[] = [
    {
      id: 'dashboard',
      labelKey: 'global.navbar.dashboard',
      route: '/dashboard',
      icon: 'tachometer-alt',
      exact: true,
      feature: 'dashboard',
    },
    { id: 'mesBoutiques', labelKey: 'global.navbar.mesBoutiques', route: '/mes-boutiques', icon: 'store', feature: 'mes-boutiques' },
    { id: 'boutiques', labelKey: 'global.navbar.boutiques', route: '/boutique', icon: 'th-list', feature: 'boutiques' },
    {
      id: 'contracts',
      labelKey: 'global.navbar.contracts',
      route: '/exploitation-boutique',
      icon: 'file-contract',
      feature: 'exploitations',
    },
    { id: 'locataires', labelKey: 'global.navbar.locataires', route: '/locataire', icon: 'user', feature: 'locataires' },
    {
      id: 'groupesArticles',
      labelKey: 'global.navbar.productGroups',
      route: '/groupe-article',
      icon: 'tags',
      feature: 'settings',
    },
    { id: 'catalogue', labelKey: 'global.navbar.catalogue', route: '/produit', icon: 'list', feature: 'produits' },
    {
      id: 'catalogueIdentification',
      labelKey: 'global.navbar.catalogueIdentification',
      route: '/catalogue-identification',
      icon: 'barcode',
      feature: 'produits',
    },
    { id: 'caisse', labelKey: 'global.navbar.caisse', route: '/caisse', icon: 'cash-register', feature: 'caisse' },
    { id: 'stocks', labelKey: 'global.navbar.stocks', route: '/stock-operations', icon: 'database', feature: 'stock' },
    { id: 'redevances', labelKey: 'global.navbar.redevances', route: '/royalties', icon: 'database', feature: 'redevances' },
    { id: 'reporting', labelKey: 'global.navbar.reporting', route: '/reporting', icon: 'book', feature: 'reporting' },
    { id: 'audit', labelKey: 'global.navbar.audit', route: '/audit-supervision', icon: 'tasks', feature: 'audit' },
    {
      id: 'users',
      labelKey: 'global.navbar.users',
      route: '/settings-center',
      icon: 'users',
      feature: 'users',
      queryParams: { onglet: 'affectations' },
    },
    { id: 'settings', labelKey: 'global.navbar.settings', route: '/settings-center', icon: 'cogs', feature: 'settings' },
    { id: 'adminUsers', labelKey: 'global.navbar.adminUsers', route: '/admin/user-management', icon: 'users', adminOnly: true },
  ];
  readonly menuIdsParRole = computed(() => {
    const profils = this.permissionsUi.codesProfil();

    if (this.account()?.authorities.includes('ROLE_ADMIN')) {
      return new Set([
        'dashboard',
        'boutiques',
        'contracts',
        'locataires',
        'groupesArticles',
        'caisse',
        'stocks',
        'redevances',
        'reporting',
        'audit',
        'users',
        'settings',
        'adminUsers',
      ]);
    }

    if (profils.has('MANAGER_ADM')) {
      return new Set([
        'dashboard',
        'boutiques',
        'contracts',
        'locataires',
        'groupesArticles',
        'redevances',
        'reporting',
        'audit',
        'users',
        'settings',
      ]);
    }

    if (this.account()?.authorities.includes('ROLE_LOCATAIRE')) {
      return new Set(['dashboard', 'mesBoutiques', 'redevances', 'reporting']);
    }

    if (profils.has('MANAGER_BOUTIQUE')) {
      return new Set([
        'dashboard',
        'boutiques',
        'catalogue',
        'catalogueIdentification',
        'caisse',
        'stocks',
        'redevances',
        'reporting',
        'users',
      ]);
    }

    if (profils.has('VENDEUR')) {
      return new Set(['dashboard', 'caisse']);
    }

    return new Set(['dashboard']);
  });
  readonly menuItemsVisibles = computed(() =>
    this.menuItems.filter(item => {
      if (!this.menuIdsParRole().has(item.id)) {
        return false;
      }
      if (item.adminOnly && !this.account()?.authorities.includes('ROLE_ADMIN')) {
        return false;
      }
      if (item.technicalOnly && !this.permissionsUi.peutVoirArchivesTechniques()) {
        return false;
      }
      return item.feature ? this.permissionsUi.peutVoirEcran(item.feature) : true;
    }),
  );
  readonly menuItemsAffiches = computed(() =>
    this.menuItemsVisibles().map(item => ({
      ...item,
      labelKey:
        item.route === '/boutique' &&
        this.permissionsUi.estProfilBoutique() &&
        !this.permissionsUi.estProfilAdm() &&
        !this.permissionsUi.estAdmin() &&
        !this.permissionsUi.peutGererBoutiques()
          ? 'global.navbar.myBoutique'
          : item.id === 'catalogue' &&
              this.permissionsUi.estProfilBoutique() &&
              !this.permissionsUi.estProfilAdm() &&
              !this.permissionsUi.estAdmin()
            ? 'global.navbar.produits'
            : item.id === 'users' &&
                this.permissionsUi.estProfilBoutique() &&
                !this.permissionsUi.estProfilAdm() &&
                !this.permissionsUi.estAdmin()
              ? 'global.navbar.sellerManagement'
              : item.labelKey,
    })),
  );
  readonly activeSectionLabelKey = computed(
    () =>
      this.menuItemsAffiches()
        .filter(item => this.currentUrl().startsWith(item.route))
        .sort((left, right) => right.route.length - left.route.length)[0]?.labelKey ?? 'global.navbar.dashboard',
  );
  readonly displayName = computed(() => {
    const account = this.account();
    if (!account) {
      return 'Compte';
    }

    const fullName = [account.firstName, account.lastName].filter(Boolean).join(' ').trim();
    return fullName || account.login;
  });
  readonly roleLabel = computed(() => {
    if (this.account()?.authorities.includes('ROLE_ADMIN')) {
      return 'global.roles.admin';
    }

    if (this.account()?.authorities.includes('ROLE_LOCATAIRE')) {
      return 'global.roles.locataire';
    }

    const profils = this.permissionsUi.codesProfil();
    if (profils.has('MANAGER_ADM')) {
      return 'global.roles.managerAdm';
    }
    if (profils.has('MANAGER_BOUTIQUE')) {
      return 'global.roles.managerBoutique';
    }
    if (profils.has('VENDEUR')) {
      return 'global.roles.vendeur';
    }
    return 'global.roles.user';
  });
  readonly initials = computed(() => {
    const account = this.account();
    if (!account) {
      return 'AD';
    }

    const source = [account.firstName, account.lastName].filter(Boolean).join(' ').trim() || account.login;
    return source
      .split(/\s+/)
      .slice(0, 2)
      .map(part => part.charAt(0).toUpperCase())
      .join('');
  });

  private readonly loginService = inject(LoginService);
  private readonly profileService = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly translateService = inject(TranslateService);

  constructor() {
    const { VERSION } = environment;
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    } else {
      this.version = '';
    }
  }

  ngOnInit(): void {
    this.currentUrl.set(this.router.url);
    this.currentLanguage.set((this.translateService.getCurrentLang() || this.stateStorageService.getLocale()) ?? 'fr');
    this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
      this.currentLanguage.set(event.lang);
    });

    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction.set(profileInfo.inProduction ?? true);
      this.openAPIEnabled.set(profileInfo.openAPIEnabled ?? false);
    });

    this.router.events.pipe(filter((event: Event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe(event => {
      this.currentUrl.set(event.urlAfterRedirects);
      this.collapseNavbar();
    });
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  onNavLinkClick(item: NavItem, event: MouseEvent): void {
    this.collapseNavbar();

    const [currentPath] = this.currentUrl().split('?');
    const isCurrentRoute = item.exact ? currentPath === item.route : currentPath.startsWith(item.route);
    if (!isCurrentRoute) {
      return;
    }

    // Clicking the already-active menu entry is otherwise a no-op navigation (same URL): force a reload of
    // its data by navigating with a throwaway query param, without touching the visible URL or history.
    event.preventDefault();
    this.router.navigate([item.route], {
      queryParams: { ...item.queryParams, _r: Date.now() },
      skipLocationChange: true,
    });
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed.update(isCollapsed => !isCollapsed);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}
