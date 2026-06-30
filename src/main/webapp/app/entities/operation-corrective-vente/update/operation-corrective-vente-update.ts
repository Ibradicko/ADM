import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TypeOperationCorrective } from 'app/entities/enumerations/type-operation-corrective.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { VenteService } from 'app/entities/vente/service/vente.service';
import { IVente } from 'app/entities/vente/vente.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IOperationCorrectiveVente } from '../operation-corrective-vente.model';
import { OperationCorrectiveVenteService } from '../service/operation-corrective-vente.service';

import { OperationCorrectiveVenteFormGroup, OperationCorrectiveVenteFormService } from './operation-corrective-vente-form.service';

@Component({
  selector: 'jhi-operation-corrective-vente-update',
  templateUrl: './operation-corrective-vente-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class OperationCorrectiveVenteUpdate implements OnInit {
  readonly isSaving = signal(false);
  operationCorrectiveVente: IOperationCorrectiveVente | null = null;
  typeOperationCorrectiveValues = Object.keys(TypeOperationCorrective);

  ventesSharedCollection = signal<IVente[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected operationCorrectiveVenteService = inject(OperationCorrectiveVenteService);
  protected operationCorrectiveVenteFormService = inject(OperationCorrectiveVenteFormService);
  protected venteService = inject(VenteService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OperationCorrectiveVenteFormGroup = this.operationCorrectiveVenteFormService.createOperationCorrectiveVenteFormGroup();

  compareVente = (o1: IVente | null, o2: IVente | null): boolean => this.venteService.compareVente(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ operationCorrectiveVente }) => {
      this.operationCorrectiveVente = operationCorrectiveVente;
      if (operationCorrectiveVente) {
        this.updateForm(operationCorrectiveVente);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const operationCorrectiveVente = this.operationCorrectiveVenteFormService.getOperationCorrectiveVente(this.editForm);
    if (operationCorrectiveVente.id === null) {
      this.subscribeToSaveResponse(this.operationCorrectiveVenteService.create(operationCorrectiveVente));
    } else {
      this.subscribeToSaveResponse(this.operationCorrectiveVenteService.update(operationCorrectiveVente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IOperationCorrectiveVente | null>): void {
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

  protected updateForm(operationCorrectiveVente: IOperationCorrectiveVente): void {
    this.operationCorrectiveVente = operationCorrectiveVente;
    this.operationCorrectiveVenteFormService.resetForm(this.editForm, operationCorrectiveVente);

    this.ventesSharedCollection.update(ventes =>
      this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, operationCorrectiveVente.vente),
    );
    this.usersSharedCollection.update(users =>
      this.userService.addUserToCollectionIfMissing<IUser>(users, operationCorrectiveVente.utilisateur),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.venteService
      .query()
      .pipe(map((res: HttpResponse<IVente[]>) => res.body ?? []))
      .pipe(
        map((ventes: IVente[]) => this.venteService.addVenteToCollectionIfMissing<IVente>(ventes, this.operationCorrectiveVente?.vente)),
      )
      .subscribe((ventes: IVente[]) => this.ventesSharedCollection.set(ventes));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(
        map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.operationCorrectiveVente?.utilisateur)),
      )
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
