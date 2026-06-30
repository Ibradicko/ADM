import { inject, isDevMode } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';

import { map, switchMap } from 'rxjs/operators';

import { UiPermissionService } from 'app/core/services/ui-permission.service';

import { AccountService } from './account.service';
import { StateStorageService } from './state-storage.service';

type BusinessFeature =
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

export const BusinessRouteAccessService: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const accountService = inject(AccountService);
  const permissionsUi = inject(UiPermissionService);
  const router = inject(Router);
  const stateStorageService = inject(StateStorageService);

  return accountService.identity().pipe(
    switchMap(async account => {
      if (!account) {
        stateStorageService.storeUrl(state.url);
        router.navigate(['/login']);
        return false;
      }

      await permissionsUi.chargerPermissions(account);
      return account;
    }),
    map(account => {
      if (!account) {
        return false;
      }

      const features = next.pathFromRoot.flatMap(route => normalizeFeatures(route.data['feature'] ?? route.data['features']));
      const isMutationRoute = /\/(?:new|\d+\/edit)(?:\?|$)/.test(state.url);
      const canRead = features.length === 0 || features.some(feature => permissionsUi.peutVoirEcran(feature));
      const canMutate = !isMutationRoute || features.some(feature => permissionsUi.peutModifierFeature(feature, state.url));
      if (canRead && canMutate && permissionsUi.peutAccederRouteTechnique(state.url)) {
        return true;
      }

      if (isDevMode()) {
        console.error('User does not have any of the required business features:', features);
      }
      router.navigate([permissionsUi.routeAccueilAutorisee()]);
      return false;
    }),
  );
};

function normalizeFeatures(value: unknown): BusinessFeature[] {
  if (!value) {
    return [];
  }

  return (Array.isArray(value) ? value : [value]).filter((feature): feature is BusinessFeature => typeof feature === 'string');
}
