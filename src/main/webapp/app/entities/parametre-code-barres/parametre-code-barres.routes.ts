import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ParametreCodeBarresResolve from './route/parametre-code-barres-routing-resolve.service';

const parametreCodeBarresRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/parametre-code-barres').then(m => m.ParametreCodeBarres),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/parametre-code-barres-detail').then(m => m.ParametreCodeBarresDetail),
    resolve: {
      parametreCodeBarres: ParametreCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/parametre-code-barres-update').then(m => m.ParametreCodeBarresUpdate),
    resolve: {
      parametreCodeBarres: ParametreCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/parametre-code-barres-update').then(m => m.ParametreCodeBarresUpdate),
    resolve: {
      parametreCodeBarres: ParametreCodeBarresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default parametreCodeBarresRoute;
