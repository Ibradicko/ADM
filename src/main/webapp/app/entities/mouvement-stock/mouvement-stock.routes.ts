import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MouvementStockResolve from './route/mouvement-stock-routing-resolve.service';

const mouvementStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mouvement-stock').then(m => m.MouvementStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mouvement-stock-detail').then(m => m.MouvementStockDetail),
    resolve: {
      mouvementStock: MouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mouvement-stock-update').then(m => m.MouvementStockUpdate),
    resolve: {
      mouvementStock: MouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mouvement-stock-update').then(m => m.MouvementStockUpdate),
    resolve: {
      mouvementStock: MouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default mouvementStockRoute;
