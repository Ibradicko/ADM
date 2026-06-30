import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { firstValueFrom } from 'rxjs';

import { EtiquetteProduitService } from 'app/entities/etiquette-produit/service/etiquette-produit.service';
import { IEtiquetteProduit } from 'app/entities/etiquette-produit/etiquette-produit.model';
import { LotEtiquettesService } from 'app/entities/lot-etiquettes/service/lot-etiquettes.service';
import { TranslateDirective } from 'app/shared/language';
import { IProduit } from '../produit.model';

@Component({
  selector: 'jhi-produit-etiquettes-modal',
  templateUrl: './produit-etiquettes-modal.html',
  imports: [FormsModule, TranslateDirective, TranslateModule],
})
export class ProduitEtiquettesModal {
  produit: IProduit | null = null;
  readonly activeModal = inject(NgbActiveModal);
  readonly isSaving = signal(false);
  readonly isLoading = signal(false);
  readonly erreur = signal('');
  readonly etiquettes = signal<IEtiquetteProduit[]>([]);

  readonly formulaire = {
    reference: '',
    formatImpression: 'A6',
    quantite: 1,
  };

  private readonly lotEtiquettesService = inject(LotEtiquettesService);
  private readonly etiquetteProduitService = inject(EtiquetteProduitService);

  async chargerHistorique(): Promise<void> {
    if (!this.produit?.id) {
      return;
    }

    this.isLoading.set(true);
    this.erreur.set('');

    try {
      const reponse = await firstValueFrom(
        this.etiquetteProduitService.query({
          'produitId.equals': this.produit.id,
          sort: ['id,desc'],
        }),
      );
      this.etiquettes.set(reponse.body ?? []);
    } catch {
      this.erreur.set('productCustom.messages.loadLabelsFailed');
    } finally {
      this.isLoading.set(false);
    }
  }

  async enregistrer(): Promise<void> {
    if (!this.produit?.id || this.formulaire.quantite < 1) {
      this.erreur.set('productCustom.messages.labelQuantityPositive');
      return;
    }

    this.isSaving.set(true);
    this.erreur.set('');

    try {
      const lot = await firstValueFrom(
        this.lotEtiquettesService.create({
          id: null,
          reference: this.formulaire.reference.trim() || `LOT-${this.produit.id}-${Date.now()}`,
          dateGeneration: dayjs(),
          formatImpression: this.formulaire.formatImpression.trim() || 'A6',
          nombreEtiquettes: this.formulaire.quantite,
        }),
      );

      await firstValueFrom(
        this.etiquetteProduitService.create({
          id: null,
          quantite: this.formulaire.quantite,
          imprimee: false,
          dateImpression: null,
          produit: { id: this.produit.id, designation: this.produit.designation ?? undefined },
          lot: { id: lot.id, reference: lot.reference ?? undefined },
        }),
      );

      this.activeModal.close('saved');
    } catch {
      this.erreur.set('productCustom.messages.labelBatchFailed');
    } finally {
      this.isSaving.set(false);
    }
  }
}
