import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneMouvementStockResolve from './route/ligne-mouvement-stock-routing-resolve.service';

const ligneMouvementStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-mouvement-stock').then(m => m.LigneMouvementStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-mouvement-stock-detail').then(m => m.LigneMouvementStockDetail),
    resolve: {
      ligneMouvementStock: LigneMouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-mouvement-stock-update').then(m => m.LigneMouvementStockUpdate),
    resolve: {
      ligneMouvementStock: LigneMouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-mouvement-stock-update').then(m => m.LigneMouvementStockUpdate),
    resolve: {
      ligneMouvementStock: LigneMouvementStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneMouvementStockRoute;
