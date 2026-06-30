import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TarifProduitResolve from './route/tarif-produit-routing-resolve.service';

const tarifProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tarif-produit').then(m => m.TarifProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tarif-produit-detail').then(m => m.TarifProduitDetail),
    resolve: {
      tarifProduit: TarifProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tarif-produit-update').then(m => m.TarifProduitUpdate),
    resolve: {
      tarifProduit: TarifProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tarif-produit-update').then(m => m.TarifProduitUpdate),
    resolve: {
      tarifProduit: TarifProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tarifProduitRoute;
