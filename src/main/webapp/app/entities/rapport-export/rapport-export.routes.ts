import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RapportExportResolve from './route/rapport-export-routing-resolve.service';

const rapportExportRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/rapport-export').then(m => m.RapportExport),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/rapport-export-detail').then(m => m.RapportExportDetail),
    resolve: {
      rapportExport: RapportExportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/rapport-export-update').then(m => m.RapportExportUpdate),
    resolve: {
      rapportExport: RapportExportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/rapport-export-update').then(m => m.RapportExportUpdate),
    resolve: {
      rapportExport: RapportExportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rapportExportRoute;
