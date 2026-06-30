import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ReceptionProduitResolve from './route/reception-produit-routing-resolve.service';

const receptionProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reception-produit').then(m => m.ReceptionProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/reception-produit-detail').then(m => m.ReceptionProduitDetail),
    resolve: {
      receptionProduit: ReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reception-produit-update').then(m => m.ReceptionProduitUpdate),
    resolve: {
      receptionProduit: ReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/reception-produit-update').then(m => m.ReceptionProduitUpdate),
    resolve: {
      receptionProduit: ReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default receptionProduitRoute;
