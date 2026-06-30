import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ParametreGlobalResolve from './route/parametre-global-routing-resolve.service';

const parametreGlobalRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/parametre-global').then(m => m.ParametreGlobal),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/parametre-global-detail').then(m => m.ParametreGlobalDetail),
    resolve: {
      parametreGlobal: ParametreGlobalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/parametre-global-update').then(m => m.ParametreGlobalUpdate),
    resolve: {
      parametreGlobal: ParametreGlobalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/parametre-global-update').then(m => m.ParametreGlobalUpdate),
    resolve: {
      parametreGlobal: ParametreGlobalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default parametreGlobalRoute;
