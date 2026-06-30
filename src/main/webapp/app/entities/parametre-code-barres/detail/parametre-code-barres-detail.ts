import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IParametreCodeBarres } from '../parametre-code-barres.model';

@Component({
  selector: 'jhi-parametre-code-barres-detail',
  templateUrl: './parametre-code-barres-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ParametreCodeBarresDetail {
  readonly parametreCodeBarres = input<IParametreCodeBarres | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
