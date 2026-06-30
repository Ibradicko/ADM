import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneVenteResolve from './route/ligne-vente-routing-resolve.service';

const ligneVenteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-vente').then(m => m.LigneVente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-vente-detail').then(m => m.LigneVenteDetail),
    resolve: {
      ligneVente: LigneVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-vente-update').then(m => m.LigneVenteUpdate),
    resolve: {
      ligneVente: LigneVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-vente-update').then(m => m.LigneVenteUpdate),
    resolve: {
      ligneVente: LigneVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneVenteRoute;
