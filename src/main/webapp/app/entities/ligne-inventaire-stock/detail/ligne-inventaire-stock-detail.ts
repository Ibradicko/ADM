import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneInventaireStock } from '../ligne-inventaire-stock.model';

@Component({
  selector: 'jhi-ligne-inventaire-stock-detail',
  templateUrl: './ligne-inventaire-stock-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneInventaireStockDetail {
  readonly ligneInventaireStock = input<ILigneInventaireStock | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
