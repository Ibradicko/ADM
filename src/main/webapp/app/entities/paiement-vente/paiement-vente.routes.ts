import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PaiementVenteResolve from './route/paiement-vente-routing-resolve.service';

const paiementVenteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/paiement-vente').then(m => m.PaiementVente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/paiement-vente-detail').then(m => m.PaiementVenteDetail),
    resolve: {
      paiementVente: PaiementVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/paiement-vente-update').then(m => m.PaiementVenteUpdate),
    resolve: {
      paiementVente: PaiementVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/paiement-vente-update').then(m => m.PaiementVenteUpdate),
    resolve: {
      paiementVente: PaiementVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default paiementVenteRoute;
