import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ScanInconnuResolve from './route/scan-inconnu-routing-resolve.service';

const scanInconnuRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/scan-inconnu').then(m => m.ScanInconnu),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/scan-inconnu-detail').then(m => m.ScanInconnuDetail),
    resolve: {
      scanInconnu: ScanInconnuResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/scan-inconnu-update').then(m => m.ScanInconnuUpdate),
    resolve: {
      scanInconnu: ScanInconnuResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/scan-inconnu-update').then(m => m.ScanInconnuUpdate),
    resolve: {
      scanInconnu: ScanInconnuResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default scanInconnuRoute;
