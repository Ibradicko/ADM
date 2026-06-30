import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICodeBarresProduit } from '../code-barres-produit.model';

@Component({
  selector: 'jhi-code-barres-produit-detail',
  templateUrl: './code-barres-produit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class CodeBarresProduitDetail {
  readonly codeBarresProduit = input<ICodeBarresProduit | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
