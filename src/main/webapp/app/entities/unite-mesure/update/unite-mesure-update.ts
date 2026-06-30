import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { UniteMesureService } from '../service/unite-mesure.service';
import { IUniteMesure } from '../unite-mesure.model';

import { UniteMesureFormGroup, UniteMesureFormService } from './unite-mesure-form.service';

@Component({
  selector: 'jhi-unite-mesure-update',
  templateUrl: './unite-mesure-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class UniteMesureUpdate implements OnInit {
  readonly isSaving = signal(false);
  uniteMesure: IUniteMesure | null = null;

  protected uniteMesureService = inject(UniteMesureService);
  protected uniteMesureFormService = inject(UniteMesureFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UniteMesureFormGroup = this.uniteMesureFormService.createUniteMesureFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ uniteMesure }) => {
      this.uniteMesure = uniteMesure;
      if (uniteMesure) {
        this.updateForm(uniteMesure);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const uniteMesure = this.uniteMesureFormService.getUniteMesure(this.editForm);
    if (uniteMesure.id === null) {
      this.subscribeToSaveResponse(this.uniteMesureService.create(uniteMesure));
    } else {
      this.subscribeToSaveResponse(this.uniteMesureService.update(uniteMesure));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IUniteMesure | null>): void {
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

  protected updateForm(uniteMesure: IUniteMesure): void {
    this.uniteMesure = uniteMesure;
    this.uniteMesureFormService.resetForm(this.editForm, uniteMesure);
  }
}
