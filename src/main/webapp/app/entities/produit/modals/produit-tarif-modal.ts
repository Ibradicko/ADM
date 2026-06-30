import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { firstValueFrom } from 'rxjs';

import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { ITarifProduit } from 'app/entities/tarif-produit/tarif-produit.model';
import { TarifProduitService } from 'app/entities/tarif-produit/service/tarif-produit.service';
import { TranslateDirective } from 'app/shared/language';
import { IProduit } from '../produit.model';

@Component({
  selector: 'jhi-produit-tarif-modal',
  templateUrl: './produit-tarif-modal.html',
  imports: [FormsModule, TranslateDirective, TranslateModule],
})
export class ProduitTarifModal {
  produit: IProduit | null = null;
  readonly activeModal = inject(NgbActiveModal);
  readonly isSaving = signal(false);
  readonly isLoading = signal(false);
  readonly erreur = signal('');
  readonly tarifs = signal<ITarifProduit[]>([]);
  readonly typesPrix = Object.values(TypePrix);

  readonly formulaire = {
    montant: 0,
    typePrix: TypePrix.STANDARD,
    dateDebut: dayjs().format('YYYY-MM-DD'),
    dateFin: '',
    actif: true,
  };

  private readonly tarifProduitService = inject(TarifProduitService);

  async chargerHistorique(): Promise<void> {
    if (!this.produit?.id) {
      return;
    }

    this.isLoading.set(true);
    this.erreur.set('');

    try {
      const reponse = await firstValueFrom(
        this.tarifProduitService.query({
          'produitId.equals': this.produit.id,
          sort: ['dateDebut,desc'],
        }),
      );
      this.tarifs.set(reponse.body ?? []);
    } catch {
      this.erreur.set('productCustom.messages.loadPricesFailed');
    } finally {
      this.isLoading.set(false);
    }
  }

  async enregistrer(): Promise<void> {
    if (!this.produit?.id || this.formulaire.montant <= 0 || !this.formulaire.dateDebut) {
      this.erreur.set('productCustom.messages.priceAndStartDateRequired');
      return;
    }

    this.isSaving.set(true);
    this.erreur.set('');

    try {
      await firstValueFrom(
        this.tarifProduitService.create({
          id: null,
          montant: this.formulaire.montant,
          typePrix: this.formulaire.typePrix,
          dateDebut: dayjs(this.formulaire.dateDebut),
          dateFin: this.formulaire.dateFin ? dayjs(this.formulaire.dateFin) : null,
          actif: this.formulaire.actif,
          produit: { id: this.produit.id, designation: this.produit.designation ?? undefined },
        }),
      );
      this.activeModal.close('saved');
    } catch {
      this.erreur.set('productCustom.messages.priceCreationFailed');
    } finally {
      this.isSaving.set(false);
    }
  }
}
