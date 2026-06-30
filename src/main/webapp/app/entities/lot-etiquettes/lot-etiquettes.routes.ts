import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LotEtiquettesResolve from './route/lot-etiquettes-routing-resolve.service';

const lotEtiquettesRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/lot-etiquettes').then(m => m.LotEtiquettes),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/lot-etiquettes-detail').then(m => m.LotEtiquettesDetail),
    resolve: {
      lotEtiquettes: LotEtiquettesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/lot-etiquettes-update').then(m => m.LotEtiquettesUpdate),
    resolve: {
      lotEtiquettes: LotEtiquettesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/lot-etiquettes-update').then(m => m.LotEtiquettesUpdate),
    resolve: {
      lotEtiquettes: LotEtiquettesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default lotEtiquettesRoute;
