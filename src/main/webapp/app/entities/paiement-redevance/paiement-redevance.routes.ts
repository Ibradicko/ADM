import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PaiementRedevanceResolve from './route/paiement-redevance-routing-resolve.service';

const paiementRedevanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/paiement-redevance').then(m => m.PaiementRedevance),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/paiement-redevance-detail').then(m => m.PaiementRedevanceDetail),
    resolve: {
      paiementRedevance: PaiementRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/paiement-redevance-update').then(m => m.PaiementRedevanceUpdate),
    resolve: {
      paiementRedevance: PaiementRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/paiement-redevance-update').then(m => m.PaiementRedevanceUpdate),
    resolve: {
      paiementRedevance: PaiementRedevanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default paiementRedevanceRoute;
