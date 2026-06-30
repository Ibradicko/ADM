import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAffectationUtilisateur } from '../affectation-utilisateur.model';

@Component({
  selector: 'jhi-affectation-utilisateur-detail',
  templateUrl: './affectation-utilisateur-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatePipe],
})
export class AffectationUtilisateurDetail {
  readonly affectationUtilisateur = input<IAffectationUtilisateur | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
