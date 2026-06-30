import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DepotStockResolve from './route/depot-stock-routing-resolve.service';

const depotStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/depot-stock').then(m => m.DepotStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/depot-stock-detail').then(m => m.DepotStockDetail),
    resolve: {
      depotStock: DepotStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/depot-stock-update').then(m => m.DepotStockUpdate),
    resolve: {
      depotStock: DepotStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/depot-stock-update').then(m => m.DepotStockUpdate),
    resolve: {
      depotStock: DepotStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default depotStockRoute;
