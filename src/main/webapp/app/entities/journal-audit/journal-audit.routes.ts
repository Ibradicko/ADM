import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { BusinessRouteAccessService } from 'app/core/auth/business-route-access.service';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import JournalAuditResolve from './route/journal-audit-routing-resolve.service';

const journalAuditRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/journal-audit').then(m => m.JournalAudit),
    data: {
      defaultSort: `id,${ASC}`,
      feature: 'audit',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/journal-audit-detail').then(m => m.JournalAuditDetail),
    resolve: {
      journalAudit: JournalAuditResolve,
    },
    data: {
      feature: 'audit',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/journal-audit-update').then(m => m.JournalAuditUpdate),
    resolve: {
      journalAudit: JournalAuditResolve,
    },
    data: {
      feature: 'audit',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/journal-audit-update').then(m => m.JournalAuditUpdate),
    resolve: {
      journalAudit: JournalAuditResolve,
    },
    data: {
      feature: 'audit',
    },
    canActivate: [UserRouteAccessService, BusinessRouteAccessService],
  },
];

export default journalAuditRoute;
