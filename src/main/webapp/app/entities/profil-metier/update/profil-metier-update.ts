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
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IPermissionMetier } from 'app/entities/permission-metier/permission-metier.model';
import { PermissionMetierService } from 'app/entities/permission-metier/service/permission-metier.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';

import { IProfilMetier } from '../profil-metier.model';
import { ProfilMetierService } from '../service/profil-metier.service';

import { ProfilMetierFormGroup, ProfilMetierFormService } from './profil-metier-form.service';

@Component({
  selector: 'jhi-profil-metier-update',
  templateUrl: './profil-metier-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfilMetierUpdate implements OnInit {
  readonly isSaving = signal(false);
  profilMetier: IProfilMetier | null = null;
  statutGeneralValues = Object.keys(StatutGeneral);

  permissionMetiersSharedCollection = signal<IPermissionMetier[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected profilMetierService = inject(ProfilMetierService);
  protected profilMetierFormService = inject(ProfilMetierFormService);
  protected permissionMetierService = inject(PermissionMetierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfilMetierFormGroup = this.profilMetierFormService.createProfilMetierFormGroup();

  comparePermissionMetier = (o1: IPermissionMetier | null, o2: IPermissionMetier | null): boolean =>
    this.permissionMetierService.comparePermissionMetier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profilMetier }) => {
      this.profilMetier = profilMetier;
      if (profilMetier) {
        this.updateForm(profilMetier);
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
    const profilMetier = this.profilMetierFormService.getProfilMetier(this.editForm);
    if (profilMetier.id === null) {
      this.subscribeToSaveResponse(this.profilMetierService.create(profilMetier));
    } else {
      this.subscribeToSaveResponse(this.profilMetierService.update(profilMetier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfilMetier | null>): void {
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

  protected updateForm(profilMetier: IProfilMetier): void {
    this.profilMetier = profilMetier;
    this.profilMetierFormService.resetForm(this.editForm, profilMetier);

    this.permissionMetiersSharedCollection.update(permissionMetiers =>
      this.permissionMetierService.addPermissionMetierToCollectionIfMissing<IPermissionMetier>(
        permissionMetiers,
        ...(profilMetier.permissionses ?? []),
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.permissionMetierService
      .query()
      .pipe(map((res: HttpResponse<IPermissionMetier[]>) => res.body ?? []))
      .pipe(
        map((permissionMetiers: IPermissionMetier[]) =>
          this.permissionMetierService.addPermissionMetierToCollectionIfMissing<IPermissionMetier>(
            permissionMetiers,
            ...(this.profilMetier?.permissionses ?? []),
          ),
        ),
      )
      .subscribe((permissionMetiers: IPermissionMetier[]) => this.permissionMetiersSharedCollection.set(permissionMetiers));
  }
}
