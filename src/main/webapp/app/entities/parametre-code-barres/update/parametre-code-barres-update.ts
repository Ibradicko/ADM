import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IParametreCodeBarres } from '../parametre-code-barres.model';
import { ParametreCodeBarresService } from '../service/parametre-code-barres.service';

import { ParametreCodeBarresFormGroup, ParametreCodeBarresFormService } from './parametre-code-barres-form.service';

@Component({
  selector: 'jhi-parametre-code-barres-update',
  templateUrl: './parametre-code-barres-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ParametreCodeBarresUpdate implements OnInit {
  readonly isSaving = signal(false);
  parametreCodeBarres: IParametreCodeBarres | null = null;
  typeCodeBarresValues = Object.keys(TypeCodeBarres);

  protected parametreCodeBarresService = inject(ParametreCodeBarresService);
  protected parametreCodeBarresFormService = inject(ParametreCodeBarresFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ParametreCodeBarresFormGroup = this.parametreCodeBarresFormService.createParametreCodeBarresFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parametreCodeBarres }) => {
      this.parametreCodeBarres = parametreCodeBarres;
      if (parametreCodeBarres) {
        this.updateForm(parametreCodeBarres);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const parametreCodeBarres = this.parametreCodeBarresFormService.getParametreCodeBarres(this.editForm);
    if (parametreCodeBarres.id === null) {
      this.subscribeToSaveResponse(this.parametreCodeBarresService.create(parametreCodeBarres));
    } else {
      this.subscribeToSaveResponse(this.parametreCodeBarresService.update(parametreCodeBarres));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IParametreCodeBarres | null>): void {
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

  protected updateForm(parametreCodeBarres: IParametreCodeBarres): void {
    this.parametreCodeBarres = parametreCodeBarres;
    this.parametreCodeBarresFormService.resetForm(this.editForm, parametreCodeBarres);
  }
}
