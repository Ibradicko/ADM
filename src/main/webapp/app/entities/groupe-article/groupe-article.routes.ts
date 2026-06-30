import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import GroupeArticleResolve from './route/groupe-article-routing-resolve.service';

const groupeArticleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/groupe-article').then(m => m.GroupeArticle),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/groupe-article-detail').then(m => m.GroupeArticleDetail),
    resolve: {
      groupeArticle: GroupeArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/groupe-article-update').then(m => m.GroupeArticleUpdate),
    resolve: {
      groupeArticle: GroupeArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/groupe-article-update').then(m => m.GroupeArticleUpdate),
    resolve: {
      groupeArticle: GroupeArticleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default groupeArticleRoute;
