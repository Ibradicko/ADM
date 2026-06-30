import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AffectationUtilisateurResolve from './route/affectation-utilisateur-routing-resolve.service';

const affectationUtilisateurRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/affectation-utilisateur').then(m => m.AffectationUtilisateur),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/affectation-utilisateur-detail').then(m => m.AffectationUtilisateurDetail),
    resolve: {
      affectationUtilisateur: AffectationUtilisateurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/affectation-utilisateur-update').then(m => m.AffectationUtilisateurUpdate),
    resolve: {
      affectationUtilisateur: AffectationUtilisateurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/affectation-utilisateur-update').then(m => m.AffectationUtilisateurUpdate),
    resolve: {
      affectationUtilisateur: AffectationUtilisateurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default affectationUtilisateurRoute;
