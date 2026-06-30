import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILotEtiquettes } from '../lot-etiquettes.model';
import { LotEtiquettesService } from '../service/lot-etiquettes.service';

import { LotEtiquettesFormGroup, LotEtiquettesFormService } from './lot-etiquettes-form.service';

@Component({
  selector: 'jhi-lot-etiquettes-update',
  templateUrl: './lot-etiquettes-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LotEtiquettesUpdate implements OnInit {
  readonly isSaving = signal(false);
  lotEtiquettes: ILotEtiquettes | null = null;

  protected lotEtiquettesService = inject(LotEtiquettesService);
  protected lotEtiquettesFormService = inject(LotEtiquettesFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LotEtiquettesFormGroup = this.lotEtiquettesFormService.createLotEtiquettesFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lotEtiquettes }) => {
      this.lotEtiquettes = lotEtiquettes;
      if (lotEtiquettes) {
        this.updateForm(lotEtiquettes);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const lotEtiquettes = this.lotEtiquettesFormService.getLotEtiquettes(this.editForm);
    if (lotEtiquettes.id === null) {
      this.subscribeToSaveResponse(this.lotEtiquettesService.create(lotEtiquettes));
    } else {
      this.subscribeToSaveResponse(this.lotEtiquettesService.update(lotEtiquettes));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILotEtiquettes | null>): void {
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

  protected updateForm(lotEtiquettes: ILotEtiquettes): void {
    this.lotEtiquettes = lotEtiquettes;
    this.lotEtiquettesFormService.resetForm(this.editForm, lotEtiquettes);
  }
}
