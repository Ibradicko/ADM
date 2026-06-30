import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneTransfertStockResolve from './route/ligne-transfert-stock-routing-resolve.service';

const ligneTransfertStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-transfert-stock').then(m => m.LigneTransfertStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-transfert-stock-detail').then(m => m.LigneTransfertStockDetail),
    resolve: {
      ligneTransfertStock: LigneTransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-transfert-stock-update').then(m => m.LigneTransfertStockUpdate),
    resolve: {
      ligneTransfertStock: LigneTransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-transfert-stock-update').then(m => m.LigneTransfertStockUpdate),
    resolve: {
      ligneTransfertStock: LigneTransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneTransfertStockRoute;
