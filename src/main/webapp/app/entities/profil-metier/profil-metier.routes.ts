import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfilMetierResolve from './route/profil-metier-routing-resolve.service';

const profilMetierRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/profil-metier').then(m => m.ProfilMetier),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/profil-metier-detail').then(m => m.ProfilMetierDetail),
    resolve: {
      profilMetier: ProfilMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/profil-metier-update').then(m => m.ProfilMetierUpdate),
    resolve: {
      profilMetier: ProfilMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/profil-metier-update').then(m => m.ProfilMetierUpdate),
    resolve: {
      profilMetier: ProfilMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default profilMetierRoute;
