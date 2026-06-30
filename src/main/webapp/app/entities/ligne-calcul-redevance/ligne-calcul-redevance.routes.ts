import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneCalculRedevanceResolve from './route/ligne-calcul-redevance-routing-resolve.service';

const ligneCalculRedevanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-calcul-redevance').then(m => m.LigneCalculRedevance),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-calcul-redevance-detail').then(m => m.LigneCalculRedevanceDetail),
    resolve: {
      ligneCalculRedevance: LigneCalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-calcul-redevance-update').then(m => m.LigneCalculRedevanceUpdate),
    resolve: {
      ligneCalculRedevance: LigneCalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-calcul-redevance-update').then(m => m.LigneCalculRedevanceUpdate),
    resolve: {
      ligneCalculRedevance: LigneCalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneCalculRedevanceRoute;
