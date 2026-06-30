import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TicketCaisseResolve from './route/ticket-caisse-routing-resolve.service';

const ticketCaisseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ticket-caisse').then(m => m.TicketCaisse),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ticket-caisse-detail').then(m => m.TicketCaisseDetail),
    resolve: {
      ticketCaisse: TicketCaisseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ticket-caisse-update').then(m => m.TicketCaisseUpdate),
    resolve: {
      ticketCaisse: TicketCaisseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ticket-caisse-update').then(m => m.TicketCaisseUpdate),
    resolve: {
      ticketCaisse: TicketCaisseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ticketCaisseRoute;
