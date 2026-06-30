import { test as base, expect, Page } from '@playwright/test';

import { loginAsLocataire, trackConsoleErrors } from '../fixtures/auth';

/**
 * Source of truth for these expectations: navbar.ts:113-160 (menuIdsParRole for
 * ROLE_LOCATAIRE) cross-checked against UiPermissionService.peutVoirEcran (locataire
 * has estLocataire=true and at least one boutique via loc_alpha -> Locataire LOC-ALPHA
 * -> ExploitationBoutique 903201 ACTIF on ADM-BTK-01).
 */
const ALLOWED_ROUTES = ['/dashboard', '/mes-boutiques', '/royalties', '/reporting'];

/**
 * Each forbidden route below denies access through a single, unambiguous feature
 * check in UiPermissionService (peutGererVentes/peutLireStock/peutLireAudit all
 * false for a pure locataire), so there is no risk of an OR-combined feature
 * (like settings-center's ['settings','users']) accidentally allowing access.
 */
const FORBIDDEN_ROUTES = ['/caisse', '/stock-operations', '/audit-supervision'];

const test = base.extend<{ authedPage: Page }>({
  // eslint-disable-next-line no-empty-pattern
  authedPage: async ({ browser }, use) => {
    const context = await browser.newContext();
    const page = await context.newPage();
    await loginAsLocataire(page);
    // UiPermissionService.rechargerPermissions (boutiqueIds in particular) resolves
    // asynchronously after landing on /dashboard: the sidebar first renders with only
    // the synchronous-permission items (dashboard, mes-boutiques) and reactively adds
    // redevances/reporting once boutiqueIds populates. Wait for the full, stable set
    // before each test proceeds, not just "the sidebar exists".
    await expect(page.locator('.adm-nav__link')).toHaveCount(ALLOWED_ROUTES.length);
    await use(page);
    await context.close();
  },
});

/**
 * Navigates through the sidebar link rather than page.goto(), i.e. an Angular
 * client-side route change instead of a full document reload. Reloading mid-session
 * forces UiPermissionService to re-fetch permissions (including the locataire's
 * boutiqueIds, via an extra findMesExploitations() call) from scratch, and the
 * BusinessRouteAccessService guard for the *new* route can run before that refetch
 * settles, bouncing back to /dashboard. A real user clicking the sidebar never hits
 * this because the already-loaded permission state is reused.
 */
async function navigateViaMenu(page: Page, route: string): Promise<void> {
  await page.locator(`.adm-nav__link[href="${route}"]`).click();
  await page.waitForURL(new RegExp(route.replace('/', '\\/')), { timeout: 10_000 });
}

test.describe('Role: locataire (loc_alpha)', () => {
  test('le menu affiche exactement les routes prevues par BusinessRouteAccessService', async ({ authedPage: page }) => {
    const hrefs = await page.locator('.adm-nav__link').evaluateAll(links => links.map(link => link.getAttribute('href')));

    expect(new Set(hrefs)).toEqual(new Set(ALLOWED_ROUTES));
  });

  for (const route of FORBIDDEN_ROUTES) {
    test(`acces direct a ${route} est bloque et redirige vers /dashboard`, async ({ authedPage: page }) => {
      await page.goto(route);
      await page.waitForURL(/\/dashboard/, { timeout: 10_000 });

      expect(new URL(page.url()).pathname).toBe('/dashboard');
    });
  }

  test('dashboard (tenant) rend sans erreur console avec des KPI non vides', async ({ authedPage: page }) => {
    const errors = trackConsoleErrors(page);

    await page.goto('/dashboard');
    await expect(page.locator('h1')).toBeVisible();
    await expect(page.locator('.adm-stat-card')).toHaveCount(4);

    expect(errors).toEqual([]);
  });

  test('mes-boutiques rend sans erreur console avec la boutique du locataire', async ({ authedPage: page }) => {
    const errors = trackConsoleErrors(page);

    await page.goto('/mes-boutiques');
    await expect(page.locator('.mes-boutiques__empty')).toHaveCount(0);
    await expect(page.locator('.mes-boutiques__card')).toHaveCount(1);
    await expect(page.locator('.mes-boutiques__boutique-nom')).toHaveText('Boutique Test Alpha');

    expect(errors).toEqual([]);
  });

  test('royalties rend sans erreur console avec le calcul de redevance scope a ses boutiques', async ({ authedPage: page }) => {
    const errors = trackConsoleErrors(page);

    await navigateViaMenu(page, '/royalties');
    await expect(page.getByText('CALC-ALPHA-2026-06').first()).toBeVisible();
    await expect(page.getByText('royalties.empty.noCalculation')).toHaveCount(0);

    expect(errors).toEqual([]);
  });

  test('reporting rend sans erreur console avec la liste de boutiques scopee au locataire', async ({ authedPage: page }) => {
    const errors = trackConsoleErrors(page);

    await navigateViaMenu(page, '/reporting');
    // reporting.html selects in order: typeRapport (0), format (1), boutiqueId (2).
    const boutiqueSelect = page.locator('select.adm-select').nth(2);
    await expect(boutiqueSelect.locator('option', { hasText: 'Boutique Test Alpha' })).toHaveCount(1);
    await expect(boutiqueSelect.locator('option')).toHaveCount(2); // "Toutes les boutiques" + Alpha only

    expect(errors).toEqual([]);
  });
});
