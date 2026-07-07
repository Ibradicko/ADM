import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { ProfileInfo } from 'app/layouts/profiles/profile-info.model';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';

import Navbar from './navbar';

describe('Navbar Component', () => {
  let comp: Navbar;
  let fixture: ComponentFixture<Navbar>;
  let accountService: AccountService;
  let profileService: ProfileService;
  let permissionsUi: UiPermissionService;
  const account: Account = {
    activated: true,
    authorities: [],
    email: '',
    firstName: 'John',
    langKey: '',
    lastName: 'Doe',
    login: 'john.doe',
    imageUrl: '',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {},
        },
        provideHttpClientTesting(),
        LoginService,
      ],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Navbar);
    comp = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    profileService = TestBed.inject(ProfileService);
    permissionsUi = TestBed.inject(UiPermissionService);
  });

  it('should call profileService.getProfileInfo on init', () => {
    // GIVEN
    vitest.spyOn(profileService, 'getProfileInfo').mockReturnValue(of(new ProfileInfo()));

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(profileService.getProfileInfo).toHaveBeenCalled();
  });

  it('should hold current authenticated user in variable account', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(comp.account()).toBeNull();

    // WHEN
    accountService.authenticate(account);

    // THEN
    expect(comp.account()).toEqual(account);

    // WHEN
    accountService.authenticate(null);

    // THEN
    expect(comp.account()).toBeNull();
  });

  it('should hold current authenticated user in variable account if user is authenticated before page load', () => {
    // GIVEN
    accountService.authenticate(account);

    // WHEN
    comp.ngOnInit();

    // THEN
    expect(comp.account()).toEqual(account);

    // WHEN
    accountService.authenticate(null);

    // THEN
    expect(comp.account()).toBeNull();
  });

  it('should show ADM management menus for manager ADM profile', () => {
    accountService.authenticate({ ...account, login: 'manager_adm', authorities: ['ROLE_USER'] });
    permissionsUi.codesProfil.set(new Set(['MANAGER_ADM']));
    permissionsUi.permissionsMetier.set(
      new Set(['USER_MANAGE', 'SALES_READ', 'REPORTING_EXPORT', 'ROYALTY_MANAGE', 'SETTINGS_MANAGE', 'AUDIT_READ']),
    );

    const routes = comp.menuItemsAffiches().map(item => item.route);

    expect(routes).toContain('/dashboard');
    expect(routes).toContain('/royalties');
    expect(routes).toContain('/boutique');
    expect(routes).toContain('/exploitation-boutique');
    expect(routes).toContain('/locataire');
    expect(routes).toContain('/reporting');
    expect(routes).toContain('/audit-supervision');
    expect(routes).not.toContain('/caisse');
    expect(routes).not.toContain('/vente');
    expect(routes).not.toContain('/produit');
    expect(routes).not.toContain('/depot-stock');
    expect(routes).not.toContain('/stock-produit');
    expect(routes).not.toContain('/settings-center');
    expect(routes).not.toContain('/admin/user-management');
  });

  it('should limit boutique manager menus to boutique operations and users', () => {
    accountService.authenticate({ ...account, login: 'manager_alpha', authorities: ['ROLE_USER'] });
    permissionsUi.codesProfil.set(new Set(['MANAGER_BOUTIQUE']));
    permissionsUi.permissionsMetier.set(
      new Set([
        'USER_READ',
        'USER_CREATE',
        'USER_UPDATE',
        'USER_DEACTIVATE',
        'SALES_MANAGE',
        'STOCK_MANAGE',
        'REPORTING_READ',
        'ROYALTY_READ',
      ]),
    );

    const items = comp.menuItemsAffiches();
    const routes = items.map(item => item.route);

    expect(items.find(item => item.route === '/boutique')?.labelKey).toBe('global.navbar.myBoutique');
    expect(routes).toContain('/settings-center');
    expect(routes).toContain('/caisse');
    expect(routes).toContain('/stock-operations');
    expect(routes).toContain('/produit');
    expect(routes).toContain('/catalogue-identification');
    expect(routes).toContain('/reporting');
    expect(routes).not.toContain('/depot-stock');
    expect(routes).not.toContain('/stock-produit');
    expect(routes).not.toContain('/royalties');
    expect(routes).not.toContain('/vente');
    expect(routes).not.toContain('/rapport-export');
    expect(routes).not.toContain('/locataire');
    expect(routes).not.toContain('/exploitation-boutique');
    expect(routes).not.toContain('/admin/user-management');
  });

  it('should limit vendeur menus to dashboard and caisse', () => {
    accountService.authenticate({ ...account, login: 'vendeur_alpha', authorities: ['ROLE_USER'] });
    permissionsUi.codesProfil.set(new Set(['VENDEUR']));
    permissionsUi.permissionsMetier.set(new Set(['SALES_MANAGE']));

    const routes = comp.menuItemsAffiches().map(item => item.route);

    expect(routes).toContain('/dashboard');
    expect(routes).toContain('/caisse');
    expect(routes).not.toContain('/boutique');
    expect(routes).not.toContain('/produit');
    expect(routes).not.toContain('/stock-operations');
    expect(routes).not.toContain('/royalties');
    expect(routes).not.toContain('/reporting');
    expect(routes).not.toContain('/audit-supervision');
    expect(routes).not.toContain('/settings-center');
    expect(routes).not.toContain('/admin/user-management');
  });

  it('should limit locataire menus to dashboard and mes boutiques', () => {
    accountService.authenticate({ ...account, login: 'locataire_alpha', authorities: ['ROLE_LOCATAIRE'] });

    const items = comp.menuItemsAffiches();
    const routes = items.map(item => item.route);

    expect(routes).toContain('/dashboard');
    expect(routes).toContain('/mes-boutiques');
    expect(routes).toContain('/royalties');
    expect(items.find(item => item.feature === 'users')).toBeUndefined();
    expect(routes).not.toContain('/boutique');
    expect(routes).not.toContain('/locataire');
    expect(routes).not.toContain('/produit');
    expect(routes).not.toContain('/caisse');
    expect(routes).not.toContain('/stock-operations');
    expect(routes).not.toContain('/reporting');
    expect(routes).not.toContain('/audit-supervision');
    expect(routes).not.toContain('/settings-center');
    expect(comp.roleLabel()).toBe('global.roles.locataire');
    expect(routes).not.toContain('/admin/user-management');
  });

  it('should show ADM menus for ROLE_ADMIN without sales and stock operations', () => {
    accountService.authenticate({ ...account, login: 'admin', authorities: ['ROLE_ADMIN'] });

    const routes = comp.menuItemsAffiches().map(item => item.route);

    expect(routes).toContain('/dashboard');
    expect(routes).toContain('/boutique');
    expect(routes).toContain('/exploitation-boutique');
    expect(routes).toContain('/locataire');
    expect(routes).toContain('/produit');
    expect(routes).not.toContain('/caisse');
    expect(routes).not.toContain('/stock-operations');
    expect(routes).toContain('/royalties');
    expect(routes).toContain('/reporting');
    expect(routes).toContain('/audit-supervision');
    expect(routes).toContain('/settings-center');
    expect(routes).toContain('/admin/user-management');
    expect(comp.roleLabel()).toBe('global.roles.admin');
  });
});
