import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';

import Password from './password';
import { PasswordService } from './password.service';

describe('Password', () => {
  let comp: Password;
  let fixture: ComponentFixture<Password>;
  let service: PasswordService;
  let accountService: AccountService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: AccountService,
          useValue: {
            isAuthenticated: vitest.fn(),
            account: signal({
              activated: true,
              authorities: ['ROLE_LOCATAIRE'],
              email: 'locataire@adm.local',
              firstName: null,
              langKey: 'fr',
              lastName: 'Locataire',
              login: 'locataire',
              imageUrl: null,
              mustChangePassword: true,
            }),
            identity: vitest.fn(() =>
              of({
                activated: true,
                authorities: ['ROLE_LOCATAIRE'],
                email: 'locataire@adm.local',
                firstName: null,
                langKey: 'fr',
                lastName: 'Locataire',
                login: 'locataire',
                imageUrl: null,
                mustChangePassword: false,
              }),
            ),
          },
        },
        {
          provide: Router,
          useValue: {
            navigate: vitest.fn(),
          },
        },
      ],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Password);
    comp = fixture.componentInstance;
    service = TestBed.inject(PasswordService);
    accountService = TestBed.inject(AccountService);
    router = TestBed.inject(Router);
  });

  it('should show error if passwords do not match', () => {
    // GIVEN
    comp.passwordForm.patchValue({
      currentPassword: 'Adm@2026',
      newPassword: 'password1',
      confirmPassword: 'password2',
    });
    // WHEN
    comp.changePassword();
    // THEN
    expect(comp.doNotMatch()).toBe(true);
    expect(comp.error()).toBe(false);
    expect(comp.success()).toBe(false);
  });

  it('should mark all fields as touched and not call service if form is incomplete', () => {
    // GIVEN
    const saveSpy = vitest.spyOn(service, 'save');

    // WHEN
    comp.changePassword();

    // THEN
    expect(comp.passwordForm.touched).toBe(true);
    expect(saveSpy).not.toHaveBeenCalled();
    expect(comp.error()).toBe(false);
    expect(comp.success()).toBe(false);
  });

  it('should call Auth.changePassword when passwords match', () => {
    // GIVEN
    const passwordValues = {
      currentPassword: 'oldPassword',
      newPassword: 'myPassword',
    };

    vitest.spyOn(service, 'save').mockReturnValue(of(new HttpResponse({ body: true })));

    comp.passwordForm.patchValue({
      currentPassword: passwordValues.currentPassword,
      newPassword: passwordValues.newPassword,
      confirmPassword: passwordValues.newPassword,
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(service.save).toHaveBeenCalledWith(passwordValues.newPassword, passwordValues.currentPassword);
  });

  it('should set success to true upon success', () => {
    // GIVEN
    vitest.spyOn(service, 'save').mockReturnValue(of(new HttpResponse({ body: true })));
    comp.passwordForm.patchValue({
      currentPassword: 'Adm@2026',
      newPassword: 'myPassword',
      confirmPassword: 'myPassword',
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(comp.doNotMatch()).toBe(false);
    expect(comp.error()).toBe(false);
    expect(comp.success()).toBe(true);
    expect(accountService.identity).toHaveBeenCalledWith(true);
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should notify of error if change password fails', () => {
    // GIVEN
    vitest.spyOn(service, 'save').mockReturnValue(throwError(Error));
    comp.passwordForm.patchValue({
      currentPassword: 'Adm@2026',
      newPassword: 'myPassword',
      confirmPassword: 'myPassword',
    });

    // WHEN
    comp.changePassword();

    // THEN
    expect(comp.doNotMatch()).toBe(false);
    expect(comp.success()).toBe(false);
    expect(comp.error()).toBe(true);
  });
});
