import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IOperationCorrectiveVente } from '../operation-corrective-vente.model';

@Component({
  selector: 'jhi-operation-corrective-vente-detail',
  templateUrl: './operation-corrective-vente-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class OperationCorrectiveVenteDetail {
  readonly operationCorrectiveVente = input<IOperationCorrectiveVente | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
