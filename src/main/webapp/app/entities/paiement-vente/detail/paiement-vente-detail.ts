import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IPaiementVente } from '../paiement-vente.model';

@Component({
  selector: 'jhi-paiement-vente-detail',
  templateUrl: './paiement-vente-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class PaiementVenteDetail {
  readonly paiementVente = input<IPaiementVente | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
