import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneVente } from '../ligne-vente.model';

@Component({
  selector: 'jhi-ligne-vente-detail',
  templateUrl: './ligne-vente-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneVenteDetail {
  readonly ligneVente = input<ILigneVente | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
