import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { FormatExport } from 'app/entities/enumerations/format-export.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IRapportExport } from '../rapport-export.model';
import { RapportExportService } from '../service/rapport-export.service';

import { RapportExportFormGroup, RapportExportFormService } from './rapport-export-form.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';

@Component({
  selector: 'jhi-rapport-export-update',
  templateUrl: './rapport-export-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class RapportExportUpdate implements OnInit {
  readonly isSaving = signal(false);
  rapportExport: IRapportExport | null = null;
  formatExportValues = Object.keys(FormatExport);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  locatairesSharedCollection = signal<ILocataire[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected rapportExportService = inject(RapportExportService);
  protected rapportExportFormService = inject(RapportExportFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected locataireService = inject(LocataireService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RapportExportFormGroup = this.rapportExportFormService.createRapportExportFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareLocataire = (o1: ILocataire | null, o2: ILocataire | null): boolean => this.locataireService.compareLocataire(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rapportExport }) => {
      this.rapportExport = rapportExport;
      if (rapportExport) {
        this.updateForm(rapportExport);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const rapportExport = this.rapportExportFormService.getRapportExport(this.editForm);
    if (rapportExport.id === null) {
      this.subscribeToSaveResponse(this.rapportExportService.create(rapportExport));
    } else {
      this.subscribeToSaveResponse(this.rapportExportService.update(rapportExport));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRapportExport | null>): void {
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

  protected updateForm(rapportExport: IRapportExport): void {
    this.rapportExport = rapportExport;
    this.rapportExportFormService.resetForm(this.editForm, rapportExport);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, rapportExport.boutique),
    );
    this.locatairesSharedCollection.update(locataires =>
      this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, rapportExport.locataire),
    );
    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, rapportExport.utilisateur));
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.rapportExport?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.locataireService
      .query()
      .pipe(map((res: HttpResponse<ILocataire[]>) => res.body ?? []))
      .pipe(
        map((locataires: ILocataire[]) =>
          this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, this.rapportExport?.locataire),
        ),
      )
      .subscribe((locataires: ILocataire[]) => this.locatairesSharedCollection.set(locataires));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.rapportExport?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
