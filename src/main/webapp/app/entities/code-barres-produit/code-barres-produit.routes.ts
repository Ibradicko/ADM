import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CodeBarresProduitResolve from './route/code-barres-produit-routing-resolve.service';

const codeBarresProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/code-barres-produit').then(m => m.CodeBarresProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/code-barres-produit-detail').then(m => m.CodeBarresProduitDetail),
    resolve: {
      codeBarresProduit: CodeBarresProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/code-barres-produit-update').then(m => m.CodeBarresProduitUpdate),
    resolve: {
      codeBarresProduit: CodeBarresProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/code-barres-produit-update').then(m => m.CodeBarresProduitUpdate),
    resolve: {
      codeBarresProduit: CodeBarresProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default codeBarresProduitRoute;
