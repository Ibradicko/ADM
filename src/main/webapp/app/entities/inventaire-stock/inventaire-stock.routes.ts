import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import InventaireStockResolve from './route/inventaire-stock-routing-resolve.service';

const inventaireStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/inventaire-stock').then(m => m.InventaireStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/inventaire-stock-detail').then(m => m.InventaireStockDetail),
    resolve: {
      inventaireStock: InventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/inventaire-stock-update').then(m => m.InventaireStockUpdate),
    resolve: {
      inventaireStock: InventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/inventaire-stock-update').then(m => m.InventaireStockUpdate),
    resolve: {
      inventaireStock: InventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inventaireStockRoute;
