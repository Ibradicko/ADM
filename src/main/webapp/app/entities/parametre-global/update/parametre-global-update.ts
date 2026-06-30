import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { IParametreGlobal } from '../parametre-global.model';
import { ParametreGlobalService } from '../service/parametre-global.service';

import { ParametreGlobalFormGroup, ParametreGlobalFormService } from './parametre-global-form.service';

@Component({
  selector: 'jhi-parametre-global-update',
  templateUrl: './parametre-global-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ParametreGlobalUpdate implements OnInit {
  readonly isSaving = signal(false);
  parametreGlobal: IParametreGlobal | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected parametreGlobalService = inject(ParametreGlobalService);
  protected parametreGlobalFormService = inject(ParametreGlobalFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ParametreGlobalFormGroup = this.parametreGlobalFormService.createParametreGlobalFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parametreGlobal }) => {
      this.parametreGlobal = parametreGlobal;
      if (parametreGlobal) {
        this.updateForm(parametreGlobal);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertErrorModel>('admSupervisionVentesApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const parametreGlobal = this.parametreGlobalFormService.getParametreGlobal(this.editForm);
    if (parametreGlobal.id === null) {
      this.subscribeToSaveResponse(this.parametreGlobalService.create(parametreGlobal));
    } else {
      this.subscribeToSaveResponse(this.parametreGlobalService.update(parametreGlobal));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IParametreGlobal | null>): void {
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

  protected updateForm(parametreGlobal: IParametreGlobal): void {
    this.parametreGlobal = parametreGlobal;
    this.parametreGlobalFormService.resetForm(this.editForm, parametreGlobal);
  }
}
