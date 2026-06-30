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
import { IReceptionProduit } from 'app/entities/reception-produit/reception-produit.model';
import { ReceptionProduitService } from 'app/entities/reception-produit/service/reception-produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneReceptionProduit } from '../ligne-reception-produit.model';
import { LigneReceptionProduitService } from '../service/ligne-reception-produit.service';

import { LigneReceptionProduitFormGroup, LigneReceptionProduitFormService } from './ligne-reception-produit-form.service';

@Component({
  selector: 'jhi-ligne-reception-produit-update',
  templateUrl: './ligne-reception-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneReceptionProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneReceptionProduit: ILigneReceptionProduit | null = null;

  receptionProduitsSharedCollection = signal<IReceptionProduit[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected ligneReceptionProduitService = inject(LigneReceptionProduitService);
  protected ligneReceptionProduitFormService = inject(LigneReceptionProduitFormService);
  protected receptionProduitService = inject(ReceptionProduitService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneReceptionProduitFormGroup = this.ligneReceptionProduitFormService.createLigneReceptionProduitFormGroup();

  compareReceptionProduit = (o1: IReceptionProduit | null, o2: IReceptionProduit | null): boolean =>
    this.receptionProduitService.compareReceptionProduit(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneReceptionProduit }) => {
      this.ligneReceptionProduit = ligneReceptionProduit;
      if (ligneReceptionProduit) {
        this.updateForm(ligneReceptionProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneReceptionProduit = this.ligneReceptionProduitFormService.getLigneReceptionProduit(this.editForm);
    if (ligneReceptionProduit.id === null) {
      this.subscribeToSaveResponse(this.ligneReceptionProduitService.create(ligneReceptionProduit));
    } else {
      this.subscribeToSaveResponse(this.ligneReceptionProduitService.update(ligneReceptionProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneReceptionProduit | null>): void {
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

  protected updateForm(ligneReceptionProduit: ILigneReceptionProduit): void {
    this.ligneReceptionProduit = ligneReceptionProduit;
    this.ligneReceptionProduitFormService.resetForm(this.editForm, ligneReceptionProduit);

    this.receptionProduitsSharedCollection.update(receptionProduits =>
      this.receptionProduitService.addReceptionProduitToCollectionIfMissing<IReceptionProduit>(
        receptionProduits,
        ligneReceptionProduit.reception,
      ),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ligneReceptionProduit.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.receptionProduitService
      .query()
      .pipe(map((res: HttpResponse<IReceptionProduit[]>) => res.body ?? []))
      .pipe(
        map((receptionProduits: IReceptionProduit[]) =>
          this.receptionProduitService.addReceptionProduitToCollectionIfMissing<IReceptionProduit>(
            receptionProduits,
            this.ligneReceptionProduit?.reception,
          ),
        ),
      )
      .subscribe((receptionProduits: IReceptionProduit[]) => this.receptionProduitsSharedCollection.set(receptionProduits));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.ligneReceptionProduit?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
