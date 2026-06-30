import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import OperationCorrectiveVenteResolve from './route/operation-corrective-vente-routing-resolve.service';

const operationCorrectiveVenteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/operation-corrective-vente').then(m => m.OperationCorrectiveVente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/operation-corrective-vente-detail').then(m => m.OperationCorrectiveVenteDetail),
    resolve: {
      operationCorrectiveVente: OperationCorrectiveVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/operation-corrective-vente-update').then(m => m.OperationCorrectiveVenteUpdate),
    resolve: {
      operationCorrectiveVente: OperationCorrectiveVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/operation-corrective-vente-update').then(m => m.OperationCorrectiveVenteUpdate),
    resolve: {
      operationCorrectiveVente: OperationCorrectiveVenteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default operationCorrectiveVenteRoute;
