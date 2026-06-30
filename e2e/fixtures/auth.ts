import { Page, expect } from '@playwright/test';

export const ACCOUNTS = {
  admin: { login: 'admin', password: 'admin' },
  managerAdm: { login: 'manager_adm', password: 'admin' },
  managerBoutique: { login: 'manager_alpha', password: 'admin' },
  vendeur: { login: 'vendeur_alpha', password: 'admin' },
  // loc_alpha is seeded with must_change_password=true (see
  // 20260621090000_fix_locataire_seed_must_change_password.xml): the first
  // login is forced through /account/password before reaching the app.
  locataire: { login: 'loc_alpha', password: 'admin', forcedNewPassword: 'AdmE2E#2026' },
} as const;

export type AccountKey = keyof typeof ACCOUNTS;

async function fillLoginForm(page: Page, login: string, password: string): Promise<void> {
  await page.goto('/login');
  await page.locator('[data-cy="username"]').fill(login);
  await page.locator('[data-cy="password"]').fill(password);
  await page.locator('[data-cy="submit"]').click();
}

/**
 * Logs in via the real login form (no JWT/storage injection) and waits until
 * the app has settled on /dashboard.
 */
export async function login(page: Page, account: { login: string; password: string }): Promise<void> {
  await fillLoginForm(page, account.login, account.password);
  await page.waitForURL(/\/dashboard/, { timeout: 15_000 });
}

type LoginOutcome = 'password-change' | 'dashboard' | 'error';

/**
 * Submits the login form and waits for whichever happens first: redirect to
 * the forced password-change page, redirect to the dashboard, or the login
 * error message becoming visible. Using Promise.race (rather than checking
 * the error locator synchronously right after the click) avoids a race
 * against the async /api/authenticate call, which would otherwise make the
 * error check run before the response even arrives.
 */
async function attemptLogin(page: Page, loginName: string, pwd: string): Promise<LoginOutcome> {
  await fillLoginForm(page, loginName, pwd);
  return Promise.race<LoginOutcome>([
    page.waitForURL(/\/account\/password/, { timeout: 10_000 }).then(() => 'password-change'),
    page.waitForURL(/\/dashboard/, { timeout: 10_000 }).then(() => 'dashboard'),
    page
      .locator('[data-cy="loginError"]')
      .waitFor({ state: 'visible', timeout: 10_000 })
      .then(() => 'error'),
  ]);
}

/**
 * Logs in as the seeded locataire test account, handling the mandatory
 * first-login password change in the UI exactly as a real user would.
 * Idempotent across repeated suite runs: if the seed password is rejected
 * (a previous run already changed it), retries with the post-change one.
 */
export async function loginAsLocataire(page: Page): Promise<void> {
  const { login: loginName, password, forcedNewPassword } = ACCOUNTS.locataire;

  let outcome = await attemptLogin(page, loginName, password);
  let currentPassword = password;

  if (outcome === 'error') {
    currentPassword = forcedNewPassword;
    outcome = await attemptLogin(page, loginName, forcedNewPassword);
  }

  if (outcome === 'error') {
    throw new Error(`loginAsLocataire: authentication failed with both the seed and post-change passwords for ${loginName}`);
  }

  if (outcome === 'password-change') {
    await page.locator('[data-cy="currentPassword"]').fill(currentPassword);
    await page.locator('[data-cy="newPassword"]').fill(forcedNewPassword);
    await page.locator('[data-cy="confirmPassword"]').fill(forcedNewPassword);
    await page.locator('[data-cy="submit"]').click();
    await expect(page.locator('.alert-success')).toBeVisible({ timeout: 10_000 });
    await page.waitForURL(/\/dashboard/, { timeout: 15_000 });
  }
}

export async function logout(page: Page): Promise<void> {
  await page.locator('[data-cy="accountMenu"]').click();
  await page.locator('[data-cy="logout"]').click();
  await page.waitForURL(/\/login/, { timeout: 10_000 });
}

/** Fails the test if the page logs any console error or uncaught exception. */
export function trackConsoleErrors(page: Page): string[] {
  const errors: string[] = [];
  page.on('console', message => {
    if (message.type() === 'error') {
      errors.push(message.text());
    }
  });
  page.on('pageerror', error => {
    errors.push(error.message);
  });
  return errors;
}
