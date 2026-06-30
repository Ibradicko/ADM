import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypeLocataire } from 'app/entities/enumerations/type-locataire.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { ILocataire } from '../locataire.model';
import { LocataireService } from '../service/locataire.service';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { LocataireFormGroup, LocataireFormService } from './locataire-form.service';

@Component({
  selector: 'jhi-locataire-update',
  templateUrl: './locataire-update.html',
  imports: [TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LocataireUpdate implements OnInit {
  readonly isSaving = signal(false);
  readonly loginGenere = signal<string | null>(null);
  readonly credentialsContext = signal<'creation' | 'reset'>('creation');
  readonly resetMdpEnCours = signal(false);
  readonly resetMdpOk = signal(false);
  locataire: ILocataire | null = null;
  typeLocataireValues = Object.keys(TypeLocataire);
  statutGeneralValues = Object.keys(StatutGeneral);

  protected locataireService = inject(LocataireService);
  protected locataireFormService = inject(LocataireFormService);
  protected activatedRoute = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private readonly appConfig = inject(ApplicationConfigService);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LocataireFormGroup = this.locataireFormService.createLocataireFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ locataire }) => {
      this.locataire = locataire;
      if (locataire) {
        this.updateForm(locataire);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    this.loginGenere.set(null);
    const locataire = this.locataireFormService.getLocataire(this.editForm);
    if (locataire.id === null) {
      this.subscribeToSaveResponse(this.locataireService.create(locataire));
    } else {
      this.subscribeToSaveResponse(this.locataireService.update(locataire));
    }
  }

  reinitialiserMotDePasse(): void {
    const id = this.editForm.controls.id.value;
    if (!id) return;
    this.resetMdpEnCours.set(true);
    this.resetMdpOk.set(false);
    this.http
      .post<{
        login: string;
        temporaryPassword: string;
      }>(`${this.appConfig.getEndpointFor('api/locataires')}/${id}/reinitialiser-mot-de-passe`, {})
      .subscribe({
        next: response => {
          this.resetMdpEnCours.set(false);
          this.resetMdpOk.set(true);
          this.credentialsContext.set('reset');
          this.loginGenere.set(response.login);
        },
        error: () => this.resetMdpEnCours.set(false),
      });
  }

  closeCredentialsModal(): void {
    this.loginGenere.set(null);
    if (this.credentialsContext() === 'creation') {
      this.previousState();
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILocataire | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: saved => this.onSaveSuccess(saved),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(saved?: ILocataire | null): void {
    if (saved?.loginGenere) {
      this.credentialsContext.set('creation');
      this.loginGenere.set(saved.loginGenere);
    } else {
      this.previousState();
    }
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(locataire: ILocataire): void {
    this.locataire = locataire;
    this.locataireFormService.resetForm(this.editForm, locataire);
  }
}
