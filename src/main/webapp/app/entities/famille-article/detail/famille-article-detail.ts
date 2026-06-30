import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IFamilleArticle } from '../famille-article.model';

@Component({
  selector: 'jhi-famille-article-detail',
  templateUrl: './famille-article-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class FamilleArticleDetail {
  readonly familleArticle = input<IFamilleArticle | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
