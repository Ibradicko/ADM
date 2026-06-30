import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TransfertStockResolve from './route/transfert-stock-routing-resolve.service';

const transfertStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/transfert-stock').then(m => m.TransfertStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/transfert-stock-detail').then(m => m.TransfertStockDetail),
    resolve: {
      transfertStock: TransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/transfert-stock-update').then(m => m.TransfertStockUpdate),
    resolve: {
      transfertStock: TransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/transfert-stock-update').then(m => m.TransfertStockUpdate),
    resolve: {
      transfertStock: TransfertStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default transfertStockRoute;
