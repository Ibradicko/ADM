import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { DataUtils } from 'app/core/util/data-util.service';
import { IProduit } from '../produit.model';

@Component({
  selector: 'jhi-produit-detail',
  templateUrl: './produit-detail.html',
  imports: [FontAwesomeModule, RouterLink, FormatMediumDatetimePipe, TranslateDirective, TranslateModule],
})
export class ProduitDetail {
  readonly produit = input<IProduit | null>(null);

  protected dataUtils = inject(DataUtils);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
  }

  formatTaux(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur}%` : '--';
  }

  formatValeur(valeur: string | null | undefined): string {
    return valeur?.trim() ? valeur : '--';
  }

  previousState(): void {
    globalThis.history.back();
  }
}
