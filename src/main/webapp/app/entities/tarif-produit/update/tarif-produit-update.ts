import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TarifProduitService } from '../service/tarif-produit.service';
import { ITarifProduit } from '../tarif-produit.model';

import { TarifProduitFormGroup, TarifProduitFormService } from './tarif-produit-form.service';

@Component({
  selector: 'jhi-tarif-produit-update',
  templateUrl: './tarif-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class TarifProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  tarifProduit: ITarifProduit | null = null;
  typePrixValues = Object.keys(TypePrix);

  produitsSharedCollection = signal<IProduit[]>([]);

  protected tarifProduitService = inject(TarifProduitService);
  protected tarifProduitFormService = inject(TarifProduitFormService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TarifProduitFormGroup = this.tarifProduitFormService.createTarifProduitFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tarifProduit }) => {
      this.tarifProduit = tarifProduit;
      if (tarifProduit) {
        this.updateForm(tarifProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tarifProduit = this.tarifProduitFormService.getTarifProduit(this.editForm);
    if (tarifProduit.id === null) {
      this.subscribeToSaveResponse(this.tarifProduitService.create(tarifProduit));
    } else {
      this.subscribeToSaveResponse(this.tarifProduitService.update(tarifProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITarifProduit | null>): void {
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

  protected updateForm(tarifProduit: ITarifProduit): void {
    this.tarifProduit = tarifProduit;
    this.tarifProduitFormService.resetForm(this.editForm, tarifProduit);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, tarifProduit.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) => this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.tarifProduit?.produit)),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
