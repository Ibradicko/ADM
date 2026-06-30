import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StatutPaiement } from 'app/entities/enumerations/statut-paiement.model';
import { IModePaiementRef } from 'app/entities/mode-paiement-ref/mode-paiement-ref.model';
import { ModePaiementRefService } from 'app/entities/mode-paiement-ref/service/mode-paiement-ref.service';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPaiementVente } from '../paiement-vente.model';
import { PaiementVenteService } from '../service/paiement-vente.service';

import { PaiementVenteFormGroup, PaiementVenteFormService } from './paiement-vente-form.service';

@Component({
  selector: 'jhi-paiement-vente-update',
  templateUrl: './paiement-vente-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PaiementVenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  paiementVente: IPaiementVente | null = null;
  statutPaiementValues = Object.keys(StatutPaiement);

  ventesSharedCollection = signal<IVente[]>([]);
  modePaiementRefsSharedCollection = signal<IModePaiementRef[]>([]);

  protected paiementVenteService = inject(PaiementVenteService);
  protected paiementVenteFormService = inject(PaiementVenteFormService);
  protected venteService = inject(VenteService);
  protected modePaiementRefService = inject(ModePaiementRefService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaiementVenteFormGroup = this.paiementVenteFormService.createPaiementVenteFormGroup();

  compareVente = (o1: IVente | null, o2: IVente | null): boolean => this.venteService.compareVente(o1, o2);

  compareModePaiementRef = (o1: IModePaiementRef | null, o2: IModePaiementRef | null): boolean =>
    this.modePaiementRefService.compareModePaiementRef(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paiementVente }) => {
      this.paiementVente = paiementVente;
      if (paiementVente) {
        this.updateForm(paiementVente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const paiementVente = this.paiementVenteFormService.getPaiementVente(this.editForm);
    if (paiementVente.id === null) {
      this.subscribeToSaveResponse(this.paiementVenteService.create(paiementVente));
    } else {
      this.subscribeToSaveResponse(this.paiementVenteService.update(paiementVente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPaiementVente | null>): void {
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

  protected updateForm(paiementVente: IPaiementVente): void {
    this.paiementVente = paiementVente;
    this.paiementVenteFormService.resetForm(this.editForm, paiementVente);

    this.ventesSharedCollection.update(ventes => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, paiementVente.vente));
    this.modePaiementRefsSharedCollection.update(modePaiementRefs =>
      this.modePaiementRefService.addModePaiementRefToCollectionIfMissing<IModePaiementRef>(modePaiementRefs, paiementVente.modePaiement),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.venteService
      .query()
      .pipe(map((res: HttpResponse<IVente[]>) => res.body ?? []))
      .pipe(map((ventes: IVente[]) => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, this.paiementVente?.vente)))
      .subscribe((ventes: IVente[]) => this.ventesSharedCollection.set(ventes));

    this.modePaiementRefService
      .query()
      .pipe(map((res: HttpResponse<IModePaiementRef[]>) => res.body ?? []))
      .pipe(
        map((modePaiementRefs: IModePaiementRef[]) =>
          this.modePaiementRefService.addModePaiementRefToCollectionIfMissing<IModePaiementRef>(
            modePaiementRefs,
            this.paiementVente?.modePaiement,
          ),
        ),
      )
      .subscribe((modePaiementRefs: IModePaiementRef[]) => this.modePaiementRefsSharedCollection.set(modePaiementRefs));
  }
}
