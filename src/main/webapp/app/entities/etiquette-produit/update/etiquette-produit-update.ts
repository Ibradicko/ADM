import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILotEtiquettes } from 'app/entities/lot-etiquettes/lot-etiquettes.model';
import { LotEtiquettesService } from 'app/entities/lot-etiquettes/service/lot-etiquettes.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IEtiquetteProduit } from '../etiquette-produit.model';
import { EtiquetteProduitService } from '../service/etiquette-produit.service';

import { EtiquetteProduitFormGroup, EtiquetteProduitFormService } from './etiquette-produit-form.service';

@Component({
  selector: 'jhi-etiquette-produit-update',
  templateUrl: './etiquette-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EtiquetteProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  etiquetteProduit: IEtiquetteProduit | null = null;

  produitsSharedCollection = signal<IProduit[]>([]);
  lotEtiquettesesSharedCollection = signal<ILotEtiquettes[]>([]);

  protected etiquetteProduitService = inject(EtiquetteProduitService);
  protected etiquetteProduitFormService = inject(EtiquetteProduitFormService);
  protected produitService = inject(ProduitService);
  protected lotEtiquettesService = inject(LotEtiquettesService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EtiquetteProduitFormGroup = this.etiquetteProduitFormService.createEtiquetteProduitFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  compareLotEtiquettes = (o1: ILotEtiquettes | null, o2: ILotEtiquettes | null): boolean =>
    this.lotEtiquettesService.compareLotEtiquettes(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ etiquetteProduit }) => {
      this.etiquetteProduit = etiquetteProduit;
      if (etiquetteProduit) {
        this.updateForm(etiquetteProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const etiquetteProduit = this.etiquetteProduitFormService.getEtiquetteProduit(this.editForm);
    if (etiquetteProduit.id === null) {
      this.subscribeToSaveResponse(this.etiquetteProduitService.create(etiquetteProduit));
    } else {
      this.subscribeToSaveResponse(this.etiquetteProduitService.update(etiquetteProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEtiquetteProduit | null>): void {
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

  protected updateForm(etiquetteProduit: IEtiquetteProduit): void {
    this.etiquetteProduit = etiquetteProduit;
    this.etiquetteProduitFormService.resetForm(this.editForm, etiquetteProduit);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, etiquetteProduit.produit),
    );
    this.lotEtiquettesesSharedCollection.update(lotEtiquetteses =>
      this.lotEtiquettesService.addLotEtiquettesToCollectionIfMissing<ILotEtiquettes>(lotEtiquetteses, etiquetteProduit.lot),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.etiquetteProduit?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));

    this.lotEtiquettesService
      .query()
      .pipe(map((res: HttpResponse<ILotEtiquettes[]>) => res.body ?? []))
      .pipe(
        map((lotEtiquetteses: ILotEtiquettes[]) =>
          this.lotEtiquettesService.addLotEtiquettesToCollectionIfMissing<ILotEtiquettes>(lotEtiquetteses, this.etiquetteProduit?.lot),
        ),
      )
      .subscribe((lotEtiquetteses: ILotEtiquettes[]) => this.lotEtiquettesesSharedCollection.set(lotEtiquetteses));
  }
}
