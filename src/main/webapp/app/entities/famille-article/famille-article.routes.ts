import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import FamilleArticleResolve from './route/famille-article-routing-resolve.service';

const familleArticleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/famille-article').then(m => m.FamilleArticle),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/famille-article-detail').then(m => m.FamilleArticleDetail),
    resolve: {
      familleArticle: FamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/famille-article-update').then(m => m.FamilleArticleUpdate),
    resolve: {
      familleArticle: FamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/famille-article-update').then(m => m.FamilleArticleUpdate),
    resolve: {
      familleArticle: FamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default familleArticleRoute;
