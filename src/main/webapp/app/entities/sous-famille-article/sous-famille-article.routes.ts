import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import SousFamilleArticleResolve from './route/sous-famille-article-routing-resolve.service';

const sousFamilleArticleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sous-famille-article').then(m => m.SousFamilleArticle),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sous-famille-article-detail').then(m => m.SousFamilleArticleDetail),
    resolve: {
      sousFamilleArticle: SousFamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sous-famille-article-update').then(m => m.SousFamilleArticleUpdate),
    resolve: {
      sousFamilleArticle: SousFamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sous-famille-article-update').then(m => m.SousFamilleArticleUpdate),
    resolve: {
      sousFamilleArticle: SousFamilleArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default sousFamilleArticleRoute;
