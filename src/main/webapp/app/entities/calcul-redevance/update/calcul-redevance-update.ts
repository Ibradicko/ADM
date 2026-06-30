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
import { StatutRedevance } from 'app/entities/enumerations/statut-redevance.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ICalculRedevance } from '../calcul-redevance.model';
import { CalculRedevanceService } from '../service/calcul-redevance.service';

import { CalculRedevanceFormGroup, CalculRedevanceFormService } from './calcul-redevance-form.service';

@Component({
  selector: 'jhi-calcul-redevance-update',
  templateUrl: './calcul-redevance-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class CalculRedevanceUpdate implements OnInit {
  readonly isSaving = signal(false);
  calculRedevance: ICalculRedevance | null = null;
  statutRedevanceValues = Object.keys(StatutRedevance);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  locatairesSharedCollection = signal<ILocataire[]>([]);

  protected calculRedevanceService = inject(CalculRedevanceService);
  protected calculRedevanceFormService = inject(CalculRedevanceFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected locataireService = inject(LocataireService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CalculRedevanceFormGroup = this.calculRedevanceFormService.createCalculRedevanceFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareLocataire = (o1: ILocataire | null, o2: ILocataire | null): boolean => this.locataireService.compareLocataire(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calculRedevance }) => {
      this.calculRedevance = calculRedevance;
      if (calculRedevance) {
        this.updateForm(calculRedevance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const calculRedevance = this.calculRedevanceFormService.getCalculRedevance(this.editForm);
    if (calculRedevance.id === null) {
      this.subscribeToSaveResponse(this.calculRedevanceService.create(calculRedevance));
    } else {
      this.subscribeToSaveResponse(this.calculRedevanceService.update(calculRedevance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICalculRedevance | null>): void {
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

  protected updateForm(calculRedevance: ICalculRedevance): void {
    this.calculRedevance = calculRedevance;
    this.calculRedevanceFormService.resetForm(this.editForm, calculRedevance);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, calculRedevance.boutique),
    );
    this.locatairesSharedCollection.update(locataires =>
      this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, calculRedevance.locataire),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.calculRedevance?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.locataireService
      .query()
      .pipe(map((res: HttpResponse<ILocataire[]>) => res.body ?? []))
      .pipe(
        map((locataires: ILocataire[]) =>
          this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, this.calculRedevance?.locataire),
        ),
      )
      .subscribe((locataires: ILocataire[]) => this.locatairesSharedCollection.set(locataires));
  }
}
