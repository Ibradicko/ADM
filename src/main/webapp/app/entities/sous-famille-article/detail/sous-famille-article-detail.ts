import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ISousFamilleArticle } from '../sous-famille-article.model';

@Component({
  selector: 'jhi-sous-famille-article-detail',
  templateUrl: './sous-famille-article-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class SousFamilleArticleDetail {
  readonly sousFamilleArticle = input<ISousFamilleArticle | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
