import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IScanInconnu } from '../scan-inconnu.model';
import { ScanInconnuService } from '../service/scan-inconnu.service';

import { ScanInconnuFormGroup, ScanInconnuFormService } from './scan-inconnu-form.service';

@Component({
  selector: 'jhi-scan-inconnu-update',
  templateUrl: './scan-inconnu-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ScanInconnuUpdate implements OnInit {
  readonly isSaving = signal(false);
  scanInconnu: IScanInconnu | null = null;

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected scanInconnuService = inject(ScanInconnuService);
  protected scanInconnuFormService = inject(ScanInconnuFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ScanInconnuFormGroup = this.scanInconnuFormService.createScanInconnuFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scanInconnu }) => {
      this.scanInconnu = scanInconnu;
      if (scanInconnu) {
        this.updateForm(scanInconnu);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const scanInconnu = this.scanInconnuFormService.getScanInconnu(this.editForm);
    if (scanInconnu.id === null) {
      this.subscribeToSaveResponse(this.scanInconnuService.create(scanInconnu));
    } else {
      this.subscribeToSaveResponse(this.scanInconnuService.update(scanInconnu));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IScanInconnu | null>): void {
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

  protected updateForm(scanInconnu: IScanInconnu): void {
    this.scanInconnu = scanInconnu;
    this.scanInconnuFormService.resetForm(this.editForm, scanInconnu);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, scanInconnu.boutique),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, scanInconnu.produitAffecte),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.scanInconnu?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.scanInconnu?.produitAffecte),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
