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
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IJournalAudit } from '../journal-audit.model';
import { JournalAuditService } from '../service/journal-audit.service';

import { JournalAuditFormGroup, JournalAuditFormService } from './journal-audit-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TypeActionAudit } from 'app/entities/enumerations/type-action-audit.model';

@Component({
  selector: 'jhi-journal-audit-update',
  templateUrl: './journal-audit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class JournalAuditUpdate implements OnInit {
  readonly isSaving = signal(false);
  journalAudit: IJournalAudit | null = null;
  typeActionAuditValues = Object.keys(TypeActionAudit);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected journalAuditService = inject(JournalAuditService);
  protected journalAuditFormService = inject(JournalAuditFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JournalAuditFormGroup = this.journalAuditFormService.createJournalAuditFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ journalAudit }) => {
      this.journalAudit = journalAudit;
      if (journalAudit) {
        this.updateForm(journalAudit);
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
    const journalAudit = this.journalAuditFormService.getJournalAudit(this.editForm);
    if (journalAudit.id === null) {
      this.subscribeToSaveResponse(this.journalAuditService.create(journalAudit));
    } else {
      this.subscribeToSaveResponse(this.journalAuditService.update(journalAudit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IJournalAudit | null>): void {
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

  protected updateForm(journalAudit: IJournalAudit): void {
    this.journalAudit = journalAudit;
    this.journalAuditFormService.resetForm(this.editForm, journalAudit);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, journalAudit.boutique),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, journalAudit.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.journalAudit?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.journalAudit?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
