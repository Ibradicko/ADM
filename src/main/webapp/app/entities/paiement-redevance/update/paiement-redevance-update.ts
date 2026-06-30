import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPaiementRedevance } from '../paiement-redevance.model';
import { PaiementRedevanceService } from '../service/paiement-redevance.service';

import { PaiementRedevanceFormGroup, PaiementRedevanceFormService } from './paiement-redevance-form.service';

@Component({
  selector: 'jhi-paiement-redevance-update',
  templateUrl: './paiement-redevance-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class PaiementRedevanceUpdate implements OnInit {
  readonly isSaving = signal(false);
  paiementRedevance: IPaiementRedevance | null = null;

  calculRedevancesSharedCollection = signal<ICalculRedevance[]>([]);

  protected paiementRedevanceService = inject(PaiementRedevanceService);
  protected paiementRedevanceFormService = inject(PaiementRedevanceFormService);
  protected calculRedevanceService = inject(CalculRedevanceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaiementRedevanceFormGroup = this.paiementRedevanceFormService.createPaiementRedevanceFormGroup();

  compareCalculRedevance = (o1: ICalculRedevance | null, o2: ICalculRedevance | null): boolean =>
    this.calculRedevanceService.compareCalculRedevance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paiementRedevance }) => {
      this.paiementRedevance = paiementRedevance;
      if (paiementRedevance) {
        this.updateForm(paiementRedevance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const paiementRedevance = this.paiementRedevanceFormService.getPaiementRedevance(this.editForm);
    if (paiementRedevance.id === null) {
      this.subscribeToSaveResponse(this.paiementRedevanceService.create(paiementRedevance));
    } else {
      this.subscribeToSaveResponse(this.paiementRedevanceService.update(paiementRedevance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPaiementRedevance | null>): void {
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

  protected updateForm(paiementRedevance: IPaiementRedevance): void {
    this.paiementRedevance = paiementRedevance;
    this.paiementRedevanceFormService.resetForm(this.editForm, paiementRedevance);

    this.calculRedevancesSharedCollection.update(calculRedevances =>
      this.calculRedevanceService.addCalculRedevanceToCollectionIfMissing<ICalculRedevance>(calculRedevances, paiementRedevance.calcul),
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
            this.paiementRedevance?.calcul,
          ),
        ),
      )
      .subscribe((calculRedevances: ICalculRedevance[]) => this.calculRedevancesSharedCollection.set(calculRedevances));
  }
}
