import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { ProfilMetierService } from 'app/entities/profil-metier/service/profil-metier.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';
import { IPermissionMetier } from '../permission-metier.model';
import { PermissionMetierService } from '../service/permission-metier.service';

import { PermissionMetierFormGroup, PermissionMetierFormService } from './permission-metier-form.service';

@Component({
  selector: 'jhi-permission-metier-update',
  templateUrl: './permission-metier-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PermissionMetierUpdate implements OnInit {
  readonly isSaving = signal(false);
  permissionMetier: IPermissionMetier | null = null;

  profilMetiersSharedCollection = signal<IProfilMetier[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected permissionMetierService = inject(PermissionMetierService);
  protected permissionMetierFormService = inject(PermissionMetierFormService);
  protected profilMetierService = inject(ProfilMetierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PermissionMetierFormGroup = this.permissionMetierFormService.createPermissionMetierFormGroup();

  compareProfilMetier = (o1: IProfilMetier | null, o2: IProfilMetier | null): boolean =>
    this.profilMetierService.compareProfilMetier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permissionMetier }) => {
      this.permissionMetier = permissionMetier;
      if (permissionMetier) {
        this.updateForm(permissionMetier);
      }

      this.loadRelationshipsOptions();
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
    const permissionMetier = this.permissionMetierFormService.getPermissionMetier(this.editForm);
    if (permissionMetier.id === null) {
      this.subscribeToSaveResponse(this.permissionMetierService.create(permissionMetier));
    } else {
      this.subscribeToSaveResponse(this.permissionMetierService.update(permissionMetier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPermissionMetier | null>): void {
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

  protected updateForm(permissionMetier: IPermissionMetier): void {
    this.permissionMetier = permissionMetier;
    this.permissionMetierFormService.resetForm(this.editForm, permissionMetier);

    this.profilMetiersSharedCollection.update(profilMetiers =>
      this.profilMetierService.addProfilMetierToCollectionIfMissing<IProfilMetier>(profilMetiers, ...(permissionMetier.profilses ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilMetierService
      .query()
      .pipe(map((res: HttpResponse<IProfilMetier[]>) => res.body ?? []))
      .pipe(
        map((profilMetiers: IProfilMetier[]) =>
          this.profilMetierService.addProfilMetierToCollectionIfMissing<IProfilMetier>(
            profilMetiers,
            ...(this.permissionMetier?.profilses ?? []),
          ),
        ),
      )
      .subscribe((profilMetiers: IProfilMetier[]) => this.profilMetiersSharedCollection.set(profilMetiers));
  }
}
