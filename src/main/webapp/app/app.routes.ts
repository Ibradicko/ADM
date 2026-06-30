import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BusinessRouteAccessService } from 'app/core/auth/business-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'dashboard',
    data: {
      feature: 'dashboard',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./home/home'),
    title: 'home.title',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'entities',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./entities/entities-overview'),
    title: 'Entites JHipster',
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'login.title',
  },
  {
    path: 'mes-boutiques',
    data: {
      feature: 'mes-boutiques',
      authorities: ['ROLE_LOCATAIRE'],
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/mes-boutiques/mes-boutiques'),
    title: 'Mes boutiques',
  },
  {
    path: 'caisse',
    data: {
      feature: 'caisse',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/caisse/caisse'),
    title: 'caisse.title',
  },
  {
    path: 'stock-operations',
    data: {
      feature: 'stock',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/stock-operations/stock-operations'),
    title: 'stockOperations.title',
  },
  {
    path: 'reporting',
    data: {
      feature: 'reporting',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/reporting/reporting'),
    title: 'reporting.title',
  },
  {
    path: 'catalogue-identification',
    data: {
      feature: 'produits',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/catalogue-identification/catalogue-identification'),
    title: 'catalogueIdentification.title',
  },
  {
    path: 'royalties',
    data: {
      feature: 'redevances',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/royalties/royalties'),
    title: 'royalties.title',
  },
  {
    path: 'audit-supervision',
    data: {
      feature: 'audit',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/audit-supervision/audit-supervision'),
    title: 'auditSupervision.title',
  },
  {
    path: 'settings-center',
    data: {
      features: ['settings', 'users'],
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
    loadComponent: () => import('./features/settings-center/settings-center'),
    title: 'settingsCenter.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
