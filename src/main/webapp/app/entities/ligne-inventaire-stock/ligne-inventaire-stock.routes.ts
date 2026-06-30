import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneInventaireStockResolve from './route/ligne-inventaire-stock-routing-resolve.service';

const ligneInventaireStockRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-inventaire-stock').then(m => m.LigneInventaireStock),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-inventaire-stock-detail').then(m => m.LigneInventaireStockDetail),
    resolve: {
      ligneInventaireStock: LigneInventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-inventaire-stock-update').then(m => m.LigneInventaireStockUpdate),
    resolve: {
      ligneInventaireStock: LigneInventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-inventaire-stock-update').then(m => m.LigneInventaireStockUpdate),
    resolve: {
      ligneInventaireStock: LigneInventaireStockResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneInventaireStockRoute;
