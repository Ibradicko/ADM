import { inject, isDevMode } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';

import { map } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { UiPermissionService } from 'app/core/services/ui-permission.service';

import { StateStorageService } from './state-storage.service';

export const UserRouteAccessService: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const accountService = inject(AccountService);
  const permissionsUi = inject(UiPermissionService);
  const router = inject(Router);
  const stateStorageService = inject(StateStorageService);
  return accountService.identity().pipe(
    map(account => {
      if (account) {
        // Forcer le changement de mot de passe si mustChangePassword est vrai
        if (account.mustChangePassword && !state.url.startsWith('/account/password')) {
          router.navigate(['/account/password']);
          return false;
        }

        const { authorities } = next.data;

        if (!authorities || authorities.length === 0 || accountService.hasAnyAuthority(authorities)) {
          return true;
        }

        if (isDevMode()) {
          console.error('User does not have any of the required authorities:', authorities);
        }
        router.navigate([permissionsUi.routeAccueilAutorisee()]);
        return false;
      }

      stateStorageService.storeUrl(state.url);
      router.navigate(['/login']);
      return false;
    }),
  );
};
