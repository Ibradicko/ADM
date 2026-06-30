import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PermissionMetierResolve from './route/permission-metier-routing-resolve.service';

const permissionMetierRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/permission-metier').then(m => m.PermissionMetier),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/permission-metier-detail').then(m => m.PermissionMetierDetail),
    resolve: {
      permissionMetier: PermissionMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/permission-metier-update').then(m => m.PermissionMetierUpdate),
    resolve: {
      permissionMetier: PermissionMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/permission-metier-update').then(m => m.PermissionMetierUpdate),
    resolve: {
      permissionMetier: PermissionMetierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default permissionMetierRoute;
