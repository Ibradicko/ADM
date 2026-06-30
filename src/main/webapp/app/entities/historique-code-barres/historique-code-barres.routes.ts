import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import HistoriqueCodeBarresResolve from './route/historique-code-barres-routing-resolve.service';

const historiqueCodeBarresRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/historique-code-barres').then(m => m.HistoriqueCodeBarres),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/historique-code-barres-detail').then(m => m.HistoriqueCodeBarresDetail),
    resolve: {
      historiqueCodeBarres: HistoriqueCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/historique-code-barres-update').then(m => m.HistoriqueCodeBarresUpdate),
    resolve: {
      historiqueCodeBarres: HistoriqueCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/historique-code-barres-update').then(m => m.HistoriqueCodeBarresUpdate),
    resolve: {
      historiqueCodeBarres: HistoriqueCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default historiqueCodeBarresRoute;
