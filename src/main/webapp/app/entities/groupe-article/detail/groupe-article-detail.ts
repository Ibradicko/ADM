import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IGroupeArticle } from '../groupe-article.model';

@Component({
  selector: 'jhi-groupe-article-detail',
  templateUrl: './groupe-article-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class GroupeArticleDetail {
  readonly groupeArticle = input<IGroupeArticle | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
