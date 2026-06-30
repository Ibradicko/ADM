import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneMouvementStock } from '../ligne-mouvement-stock.model';

@Component({
  selector: 'jhi-ligne-mouvement-stock-detail',
  templateUrl: './ligne-mouvement-stock-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneMouvementStockDetail {
  readonly ligneMouvementStock = input<ILigneMouvementStock | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
