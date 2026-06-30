import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { BusinessRouteAccessService } from 'app/core/auth/business-route-access.service';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BoutiqueResolve from './route/boutique-routing-resolve.service';

const boutiqueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/boutique').then(m => m.Boutique),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/boutique-detail').then(m => m.BoutiqueDetail),
    resolve: {
      boutique: BoutiqueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/boutique-update').then(m => m.BoutiqueUpdate),
    data: {
      feature: 'boutiqueManagement',
    },
    resolve: {
      boutique: BoutiqueResolve,
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/boutique-update').then(m => m.BoutiqueUpdate),
    data: {
      feature: 'boutiqueManagement',
    },
    resolve: {
      boutique: BoutiqueResolve,
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
];

export default boutiqueRoute;
