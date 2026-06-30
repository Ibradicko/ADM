import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RegleRedevanceResolve from './route/regle-redevance-routing-resolve.service';

const regleRedevanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/regle-redevance').then(m => m.RegleRedevance),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/regle-redevance-detail').then(m => m.RegleRedevanceDetail),
    resolve: {
      regleRedevance: RegleRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/regle-redevance-update').then(m => m.RegleRedevanceUpdate),
    resolve: {
      regleRedevance: RegleRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/regle-redevance-update').then(m => m.RegleRedevanceUpdate),
    resolve: {
      regleRedevance: RegleRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default regleRedevanceRoute;
