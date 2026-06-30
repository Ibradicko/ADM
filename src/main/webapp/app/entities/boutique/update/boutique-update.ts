import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypeBoutique } from 'app/entities/enumerations/type-boutique.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IBoutique } from '../boutique.model';
import { BoutiqueService } from '../service/boutique.service';

import { BoutiqueFormGroup, BoutiqueFormService } from './boutique-form.service';

@Component({
  selector: 'jhi-boutique-update',
  templateUrl: './boutique-update.html',
  imports: [TranslateModule, FontAwesomeModule, AlertError, FormsModule, ReactiveFormsModule],
})
export class BoutiqueUpdate implements OnInit {
  readonly isSaving = signal(false);
  readonly messageMetier = signal<string | null>(null);
  boutique: IBoutique | null = null;
  typeBoutiqueValues = Object.keys(TypeBoutique);
  statutGeneralValues = Object.keys(StatutGeneral);

  protected boutiqueService = inject(BoutiqueService);
  protected boutiqueFormService = inject(BoutiqueFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BoutiqueFormGroup = this.boutiqueFormService.createBoutiqueFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ boutique }) => {
      this.boutique = boutique;
      if (boutique) {
        this.updateForm(boutique);
      } else {
        this.editForm.patchValue({ statut: 'ACTIF', type: 'COMMERCE' });
      }
    });
    this.editForm.controls.nom.valueChanges.subscribe(nom => {
      if (this.editForm.controls.id.value === null) {
        this.editForm.controls.code.setValue(this.genererCode(nom ?? '', 'BTQ'), { emitEvent: false });
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.messageMetier.set(null);
    this.editForm.markAllAsTouched();
    this.isSaving.set(true);
    const boutique = this.boutiqueFormService.getBoutique(this.editForm);
    boutique.code = boutique.code?.trim() || this.genererCode(boutique.nom ?? '', 'BTQ');
    boutique.telephone = boutique.telephone?.trim() || null;
    if (boutique.id === null) {
      this.subscribeToSaveResponse(this.boutiqueService.create(boutique));
    } else {
      this.subscribeToSaveResponse(this.boutiqueService.update(boutique));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBoutique | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: error => {
        this.messageMetier.set(this.messageErreurCreation(error));
        this.onSaveError();
      },
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

  protected updateForm(boutique: IBoutique): void {
    this.boutique = boutique;
    this.boutiqueFormService.resetForm(this.editForm, boutique);
  }

  protected champInvalide(champ: 'nom' | 'telephone' | 'statut'): boolean {
    const control = this.editForm.controls[champ];
    return control.invalid && (control.dirty || control.touched);
  }

  private genererCode(source: string, prefixe: string): string {
    const base = source
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toUpperCase()
      .replace(/[^A-Z0-9]+/g, '_')
      .replace(/^_+|_+$/g, '')
      .slice(0, 24);
    return `${prefixe}_${base || dayjs().format('YYYYMMDDHHmmss')}`.slice(0, 30);
  }

  private messageErreurCreation(error: unknown): string {
    const maybeError = error as { error?: { message?: string; detail?: string }; headers?: { get?: (name: string) => string | null } };
    const headerError = maybeError.headers?.get?.('X-admSupervisionVentes-error');
    const message = maybeError.error?.message ?? maybeError.error?.detail ?? headerError ?? '';

    if (message.includes('codeAlreadyUsed')) {
      return 'Ce code boutique est deja utilise. Modifiez le nom ou renseignez un code different.';
    }
    return 'La creation a echoue. Verifiez les informations de la boutique, puis reessayez.';
  }
}
