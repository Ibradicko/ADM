import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import VenteResolve from './route/vente-routing-resolve.service';

const venteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/vente').then(m => m.Vente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/vente-detail').then(m => m.VenteDetail),
    resolve: {
      vente: VenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/vente-update').then(m => m.VenteUpdate),
    resolve: {
      vente: VenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/vente-update').then(m => m.VenteUpdate),
    resolve: {
      vente: VenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default venteRoute;
