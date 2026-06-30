import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import UniteMesureResolve from './route/unite-mesure-routing-resolve.service';

const uniteMesureRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/unite-mesure').then(m => m.UniteMesure),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/unite-mesure-detail').then(m => m.UniteMesureDetail),
    resolve: {
      uniteMesure: UniteMesureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/unite-mesure-update').then(m => m.UniteMesureUpdate),
    resolve: {
      uniteMesure: UniteMesureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/unite-mesure-update').then(m => m.UniteMesureUpdate),
    resolve: {
      uniteMesure: UniteMesureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default uniteMesureRoute;
