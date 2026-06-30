import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneVente } from '../ligne-vente.model';
import { LigneVenteService } from '../service/ligne-vente.service';

import { LigneVenteFormGroup, LigneVenteFormService } from './ligne-vente-form.service';

@Component({
  selector: 'jhi-ligne-vente-update',
  templateUrl: './ligne-vente-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneVenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneVente: ILigneVente | null = null;

  ventesSharedCollection = signal<IVente[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected ligneVenteService = inject(LigneVenteService);
  protected ligneVenteFormService = inject(LigneVenteFormService);
  protected venteService = inject(VenteService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneVenteFormGroup = this.ligneVenteFormService.createLigneVenteFormGroup();

  compareVente = (o1: IVente | null, o2: IVente | null): boolean => this.venteService.compareVente(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneVente }) => {
      this.ligneVente = ligneVente;
      if (ligneVente) {
        this.updateForm(ligneVente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneVente = this.ligneVenteFormService.getLigneVente(this.editForm);
    if (ligneVente.id === null) {
      this.subscribeToSaveResponse(this.ligneVenteService.create(ligneVente));
    } else {
      this.subscribeToSaveResponse(this.ligneVenteService.update(ligneVente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneVente | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(ligneVente: ILigneVente): void {
    this.ligneVente = ligneVente;
    this.ligneVenteFormService.resetForm(this.editForm, ligneVente);

    this.ventesSharedCollection.update(ventes => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, ligneVente.vente));
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ligneVente.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.venteService
      .query()
      .pipe(map((res: HttpResponse<IVente[]>) => res.body ?? []))
      .pipe(map((ventes: IVente[]) => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, this.ligneVente?.vente)))
      .subscribe((ventes: IVente[]) => this.ventesSharedCollection.set(ventes));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) => this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.ligneVente?.produit)),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
