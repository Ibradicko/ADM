import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneReceptionProduitResolve from './route/ligne-reception-produit-routing-resolve.service';

const ligneReceptionProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-reception-produit').then(m => m.LigneReceptionProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-reception-produit-detail').then(m => m.LigneReceptionProduitDetail),
    resolve: {
      ligneReceptionProduit: LigneReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-reception-produit-update').then(m => m.LigneReceptionProduitUpdate),
    resolve: {
      ligneReceptionProduit: LigneReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-reception-produit-update').then(m => m.LigneReceptionProduitUpdate),
    resolve: {
      ligneReceptionProduit: LigneReceptionProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneReceptionProduitRoute;
