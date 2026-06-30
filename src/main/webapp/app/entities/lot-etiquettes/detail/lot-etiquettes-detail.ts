import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ILotEtiquettes } from '../lot-etiquettes.model';

@Component({
  selector: 'jhi-lot-etiquettes-detail',
  templateUrl: './lot-etiquettes-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class LotEtiquettesDetail {
  readonly lotEtiquettes = input<ILotEtiquettes | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
