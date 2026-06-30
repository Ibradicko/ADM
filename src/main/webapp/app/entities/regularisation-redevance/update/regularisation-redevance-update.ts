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
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRegularisationRedevance } from '../regularisation-redevance.model';
import { RegularisationRedevanceService } from '../service/regularisation-redevance.service';

import { RegularisationRedevanceFormGroup, RegularisationRedevanceFormService } from './regularisation-redevance-form.service';

@Component({
  selector: 'jhi-regularisation-redevance-update',
  templateUrl: './regularisation-redevance-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RegularisationRedevanceUpdate implements OnInit {
  readonly isSaving = signal(false);
  regularisationRedevance: IRegularisationRedevance | null = null;

  calculRedevancesSharedCollection = signal<ICalculRedevance[]>([]);

  protected regularisationRedevanceService = inject(RegularisationRedevanceService);
  protected regularisationRedevanceFormService = inject(RegularisationRedevanceFormService);
  protected calculRedevanceService = inject(CalculRedevanceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RegularisationRedevanceFormGroup = this.regularisationRedevanceFormService.createRegularisationRedevanceFormGroup();

  compareCalculRedevance = (o1: ICalculRedevance | null, o2: ICalculRedevance | null): boolean =>
    this.calculRedevanceService.compareCalculRedevance(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ regularisationRedevance }) => {
      this.regularisationRedevance = regularisationRedevance;
      if (regularisationRedevance) {
        this.updateForm(regularisationRedevance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const regularisationRedevance = this.regularisationRedevanceFormService.getRegularisationRedevance(this.editForm);
    if (regularisationRedevance.id === null) {
      this.subscribeToSaveResponse(this.regularisationRedevanceService.create(regularisationRedevance));
    } else {
      this.subscribeToSaveResponse(this.regularisationRedevanceService.update(regularisationRedevance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRegularisationRedevance | null>): void {
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

  protected updateForm(regularisationRedevance: IRegularisationRedevance): void {
    this.regularisationRedevance = regularisationRedevance;
    this.regularisationRedevanceFormService.resetForm(this.editForm, regularisationRedevance);

    this.calculRedevancesSharedCollection.update(calculRedevances =>
      this.calculRedevanceService.addCalculRedevanceToCollectionIfMissing<ICalculRedevance>(
        calculRedevances,
        regularisationRedevance.calcul,
      ),
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
            this.regularisationRedevance?.calcul,
          ),
        ),
      )
      .subscribe((calculRedevances: ICalculRedevance[]) => this.calculRedevancesSharedCollection.set(calculRedevances));
  }
}
