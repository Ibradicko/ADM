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
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDepotStock } from '../depot-stock.model';
import { DepotStockService } from '../service/depot-stock.service';

import { DepotStockFormGroup, DepotStockFormService } from './depot-stock-form.service';

@Component({
  selector: 'jhi-depot-stock-update',
  templateUrl: './depot-stock-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DepotStockUpdate implements OnInit {
  readonly isSaving = signal(false);
  depotStock: IDepotStock | null = null;

  boutiquesSharedCollection = signal<IBoutique[]>([]);

  protected depotStockService = inject(DepotStockService);
  protected depotStockFormService = inject(DepotStockFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DepotStockFormGroup = this.depotStockFormService.createDepotStockFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ depotStock }) => {
      this.depotStock = depotStock;
      if (depotStock) {
        this.updateForm(depotStock);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const depotStock = this.depotStockFormService.getDepotStock(this.editForm);
    if (depotStock.id === null) {
      this.subscribeToSaveResponse(this.depotStockService.create(depotStock));
    } else {
      this.subscribeToSaveResponse(this.depotStockService.update(depotStock));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDepotStock | null>): void {
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

  protected updateForm(depotStock: IDepotStock): void {
    this.depotStock = depotStock;
    this.depotStockFormService.resetForm(this.editForm, depotStock);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, depotStock.boutique),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.depotStock?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));
  }
}
