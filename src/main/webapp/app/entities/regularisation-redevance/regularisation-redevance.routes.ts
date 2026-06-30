import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RegularisationRedevanceResolve from './route/regularisation-redevance-routing-resolve.service';

const regularisationRedevanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/regularisation-redevance').then(m => m.RegularisationRedevance),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/regularisation-redevance-detail').then(m => m.RegularisationRedevanceDetail),
    resolve: {
      regularisationRedevance: RegularisationRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/regularisation-redevance-update').then(m => m.RegularisationRedevanceUpdate),
    resolve: {
      regularisationRedevance: RegularisationRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/regularisation-redevance-update').then(m => m.RegularisationRedevanceUpdate),
    resolve: {
      regularisationRedevance: RegularisationRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default regularisationRedevanceRoute;
