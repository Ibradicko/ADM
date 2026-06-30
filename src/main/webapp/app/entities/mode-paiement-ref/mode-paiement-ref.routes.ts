import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ModePaiementRefResolve from './route/mode-paiement-ref-routing-resolve.service';

const modePaiementRefRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mode-paiement-ref').then(m => m.ModePaiementRef),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mode-paiement-ref-detail').then(m => m.ModePaiementRefDetail),
    resolve: {
      modePaiementRef: ModePaiementRefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mode-paiement-ref-update').then(m => m.ModePaiementRefUpdate),
    resolve: {
      modePaiementRef: ModePaiementRefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mode-paiement-ref-update').then(m => m.ModePaiementRefUpdate),
    resolve: {
      modePaiementRef: ModePaiementRefResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default modePaiementRefRoute;
