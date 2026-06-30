import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneCalculRedevance } from '../ligne-calcul-redevance.model';
import { LigneCalculRedevanceService } from '../service/ligne-calcul-redevance.service';

import { LigneCalculRedevanceFormGroup, LigneCalculRedevanceFormService } from './ligne-calcul-redevance-form.service';

@Component({
  selector: 'jhi-ligne-calcul-redevance-update',
  templateUrl: './ligne-calcul-redevance-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneCalculRedevanceUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneCalculRedevance: ILigneCalculRedevance | null = null;

  calculRedevancesSharedCollection = signal<ICalculRedevance[]>([]);
  ventesSharedCollection = signal<IVente[]>([]);

  protected ligneCalculRedevanceService = inject(LigneCalculRedevanceService);
  protected ligneCalculRedevanceFormService = inject(LigneCalculRedevanceFormService);
  protected calculRedevanceService = inject(CalculRedevanceService);
  protected venteService = inject(VenteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneCalculRedevanceFormGroup = this.ligneCalculRedevanceFormService.createLigneCalculRedevanceFormGroup();

  compareCalculRedevance = (o1: ICalculRedevance | null, o2: ICalculRedevance | null): boolean =>
    this.calculRedevanceService.compareCalculRedevance(o1, o2);

  compareVente = (o1: IVente | null, o2: IVente | null): boolean => this.venteService.compareVente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneCalculRedevance }) => {
      this.ligneCalculRedevance = ligneCalculRedevance;
      if (ligneCalculRedevance) {
        this.updateForm(ligneCalculRedevance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneCalculRedevance = this.ligneCalculRedevanceFormService.getLigneCalculRedevance(this.editForm);
    if (ligneCalculRedevance.id === null) {
      this.subscribeToSaveResponse(this.ligneCalculRedevanceService.create(ligneCalculRedevance));
    } else {
      this.subscribeToSaveResponse(this.ligneCalculRedevanceService.update(ligneCalculRedevance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneCalculRedevance | null>): void {
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

  protected updateForm(ligneCalculRedevance: ILigneCalculRedevance): void {
    this.ligneCalculRedevance = ligneCalculRedevance;
    this.ligneCalculRedevanceFormService.resetForm(this.editForm, ligneCalculRedevance);

    this.calculRedevancesSharedCollection.update(calculRedevances =>
      this.calculRedevanceService.addCalculRedevanceToCollectionIfMissing<ICalculRedevance>(calculRedevances, ligneCalculRedevance.calcul),
    );
    this.ventesSharedCollection.update(ventes =>
      this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, ligneCalculRedevance.vente),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.calculRedevanceService
      .query()
      .pipe(map((res: HttpResponse<ICalculRedevance[]>) => res.body ?? []))
      .pipe(
        map((calculRedevances: ICalculRedevance[]) =>
          this.calculRedevanceService.addCalculRedevanceToCollectionIfMissing<ICalculRedevance>(
            calculRedevances,
            this.ligneCalculRedevance?.calcul,
          ),
        ),
      )
      .subscribe((calculRedevances: ICalculRedevance[]) => this.calculRedevancesSharedCollection.set(calculRedevances));

    this.venteService
      .query()
      .pipe(map((res: HttpResponse<IVente[]>) => res.body ?? []))
      .pipe(map((ventes: IVente[]) => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, this.ligneCalculRedevance?.vente)))
      .subscribe((ventes: IVente[]) => this.ventesSharedCollection.set(ventes));
  }
}
