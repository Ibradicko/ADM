import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import StockProduitResolve from './route/stock-produit-routing-resolve.service';

const stockProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/stock-produit').then(m => m.StockProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/stock-produit-detail').then(m => m.StockProduitDetail),
    resolve: {
      stockProduit: StockProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/stock-produit-update').then(m => m.StockProduitUpdate),
    resolve: {
      stockProduit: StockProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/stock-produit-update').then(m => m.StockProduitUpdate),
    resolve: {
      stockProduit: StockProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default stockProduitRoute;
