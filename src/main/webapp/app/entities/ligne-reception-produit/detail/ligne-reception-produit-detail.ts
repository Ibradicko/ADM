import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneReceptionProduit } from '../ligne-reception-produit.model';

@Component({
  selector: 'jhi-ligne-reception-produit-detail',
  templateUrl: './ligne-reception-produit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneReceptionProduitDetail {
  readonly ligneReceptionProduit = input<ILigneReceptionProduit | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
