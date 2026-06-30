import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { firstValueFrom } from 'rxjs';

import { ICodeBarresProduit } from 'app/entities/code-barres-produit/code-barres-produit.model';
import { CodeBarresProduitService } from 'app/entities/code-barres-produit/service/code-barres-produit.service';
import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';
import { TranslateDirective } from 'app/shared/language';
import { IProduit } from '../produit.model';

@Component({
  selector: 'jhi-produit-code-barres-modal',
  templateUrl: './produit-code-barres-modal.html',
  imports: [FormsModule, TranslateDirective, TranslateModule],
})
export class ProduitCodeBarresModal {
  produit: IProduit | null = null;
  readonly activeModal = inject(NgbActiveModal);
  readonly isSaving = signal(false);
  readonly isLoading = signal(false);
  readonly erreur = signal('');
  readonly codesBarres = signal<ICodeBarresProduit[]>([]);
  readonly typesCodeBarres = Object.values(TypeCodeBarres);

  readonly formulaire = {
    code: '',
    type: TypeCodeBarres.EAN13,
    principal: true,
    actif: true,
    genereParSysteme: false,
  };

  private readonly codeBarresProduitService = inject(CodeBarresProduitService);

  async chargerHistorique(): Promise<void> {
    if (!this.produit?.id) {
      return;
    }

    this.isLoading.set(true);
    this.erreur.set('');

    try {
      const reponse = await firstValueFrom(
        this.codeBarresProduitService.query({
          'produitId.equals': this.produit.id,
          sort: ['dateAffectation,desc'],
        }),
      );
      this.codesBarres.set(reponse.body ?? []);
    } catch {
      this.erreur.set('productCustom.messages.loadBarcodesFailed');
    } finally {
      this.isLoading.set(false);
    }
  }

  async enregistrer(): Promise<void> {
    if (!this.produit?.id || !this.formulaire.code.trim()) {
      this.erreur.set('productCustom.messages.barcodeRequired');
      return;
    }

    this.isSaving.set(true);
    this.erreur.set('');

    try {
      await firstValueFrom(
        this.codeBarresProduitService.create({
          id: null,
          code: this.formulaire.code.trim(),
          type: this.formulaire.type,
          principal: this.formulaire.principal,
          actif: this.formulaire.actif,
          genereParSysteme: this.formulaire.genereParSysteme,
          dateAffectation: dayjs(),
          produit: { id: this.produit.id, designation: this.produit.designation ?? undefined },
        }),
      );
      this.activeModal.close('saved');
    } catch {
      this.erreur.set('productCustom.messages.barcodeCreationFailed');
    } finally {
      this.isSaving.set(false);
    }
  }

  async genererAutomatiquement(): Promise<void> {
    if (!this.produit?.id) {
      return;
    }

    this.isSaving.set(true);
    this.erreur.set('');
    try {
      await firstValueFrom(this.codeBarresProduitService.generate(this.produit.id));
      this.activeModal.close('saved');
    } catch {
      this.erreur.set('productCustom.messages.barcodeGenerationFailed');
    } finally {
      this.isSaving.set(false);
    }
  }
}
