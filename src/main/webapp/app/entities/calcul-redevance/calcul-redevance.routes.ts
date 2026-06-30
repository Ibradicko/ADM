import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CalculRedevanceResolve from './route/calcul-redevance-routing-resolve.service';

const calculRedevanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/calcul-redevance').then(m => m.CalculRedevance),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/calcul-redevance-detail').then(m => m.CalculRedevanceDetail),
    resolve: {
      calculRedevance: CalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/calcul-redevance-update').then(m => m.CalculRedevanceUpdate),
    resolve: {
      calculRedevance: CalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/calcul-redevance-update').then(m => m.CalculRedevanceUpdate),
    resolve: {
      calculRedevance: CalculRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default calculRedevanceRoute;
