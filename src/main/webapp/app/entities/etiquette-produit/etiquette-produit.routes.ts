import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EtiquetteProduitResolve from './route/etiquette-produit-routing-resolve.service';

const etiquetteProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/etiquette-produit').then(m => m.EtiquetteProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/etiquette-produit-detail').then(m => m.EtiquetteProduitDetail),
    resolve: {
      etiquetteProduit: EtiquetteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/etiquette-produit-update').then(m => m.EtiquetteProduitUpdate),
    resolve: {
      etiquetteProduit: EtiquetteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/etiquette-produit-update').then(m => m.EtiquetteProduitUpdate),
    resolve: {
      etiquetteProduit: EtiquetteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default etiquetteProduitRoute;
