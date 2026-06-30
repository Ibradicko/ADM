import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IModePaiementRef } from '../mode-paiement-ref.model';
import { ModePaiementRefService } from '../service/mode-paiement-ref.service';

import { ModePaiementRefFormGroup, ModePaiementRefFormService } from './mode-paiement-ref-form.service';

@Component({
  selector: 'jhi-mode-paiement-ref-update',
  templateUrl: './mode-paiement-ref-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ModePaiementRefUpdate implements OnInit {
  readonly isSaving = signal(false);
  modePaiementRef: IModePaiementRef | null = null;

  protected modePaiementRefService = inject(ModePaiementRefService);
  protected modePaiementRefFormService = inject(ModePaiementRefFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ModePaiementRefFormGroup = this.modePaiementRefFormService.createModePaiementRefFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ modePaiementRef }) => {
      this.modePaiementRef = modePaiementRef;
      if (modePaiementRef) {
        this.updateForm(modePaiementRef);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const modePaiementRef = this.modePaiementRefFormService.getModePaiementRef(this.editForm);
    if (modePaiementRef.id === null) {
      this.subscribeToSaveResponse(this.modePaiementRefService.create(modePaiementRef));
    } else {
      this.subscribeToSaveResponse(this.modePaiementRefService.update(modePaiementRef));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IModePaiementRef | null>): void {
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

  protected updateForm(modePaiementRef: IModePaiementRef): void {
    this.modePaiementRef = modePaiementRef;
    this.modePaiementRefFormService.resetForm(this.editForm, modePaiementRef);
  }
}
