import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IRegleRedevance } from '../regle-redevance.model';

@Component({
  selector: 'jhi-regle-redevance-detail',
  templateUrl: './regle-redevance-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatePipe],
})
export class RegleRedevanceDetail {
  readonly regleRedevance = input<IRegleRedevance | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
