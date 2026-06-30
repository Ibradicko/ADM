import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneTransfertStock } from '../ligne-transfert-stock.model';

@Component({
  selector: 'jhi-ligne-transfert-stock-detail',
  templateUrl: './ligne-transfert-stock-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneTransfertStockDetail {
  readonly ligneTransfertStock = input<ILigneTransfertStock | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
