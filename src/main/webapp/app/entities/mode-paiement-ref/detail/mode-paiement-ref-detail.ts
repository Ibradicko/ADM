import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IModePaiementRef } from '../mode-paiement-ref.model';

@Component({
  selector: 'jhi-mode-paiement-ref-detail',
  templateUrl: './mode-paiement-ref-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ModePaiementRefDetail {
  readonly modePaiementRef = input<IModePaiementRef | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
