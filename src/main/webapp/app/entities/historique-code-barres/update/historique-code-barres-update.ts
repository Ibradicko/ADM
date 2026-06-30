import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IHistoriqueCodeBarres } from '../historique-code-barres.model';
import { HistoriqueCodeBarresService } from '../service/historique-code-barres.service';

import { HistoriqueCodeBarresFormGroup, HistoriqueCodeBarresFormService } from './historique-code-barres-form.service';

@Component({
  selector: 'jhi-historique-code-barres-update',
  templateUrl: './historique-code-barres-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class HistoriqueCodeBarresUpdate implements OnInit {
  readonly isSaving = signal(false);
  historiqueCodeBarres: IHistoriqueCodeBarres | null = null;

  produitsSharedCollection = signal<IProduit[]>([]);
  usersSharedCollection = signal<IUser[]>([]);

  protected historiqueCodeBarresService = inject(HistoriqueCodeBarresService);
  protected historiqueCodeBarresFormService = inject(HistoriqueCodeBarresFormService);
  protected produitService = inject(ProduitService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HistoriqueCodeBarresFormGroup = this.historiqueCodeBarresFormService.createHistoriqueCodeBarresFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ historiqueCodeBarres }) => {
      this.historiqueCodeBarres = historiqueCodeBarres;
      if (historiqueCodeBarres) {
        this.updateForm(historiqueCodeBarres);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const historiqueCodeBarres = this.historiqueCodeBarresFormService.getHistoriqueCodeBarres(this.editForm);
    if (historiqueCodeBarres.id === null) {
      this.subscribeToSaveResponse(this.historiqueCodeBarresService.create(historiqueCodeBarres));
    } else {
      this.subscribeToSaveResponse(this.historiqueCodeBarresService.update(historiqueCodeBarres));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IHistoriqueCodeBarres | null>): void {
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

  protected updateForm(historiqueCodeBarres: IHistoriqueCodeBarres): void {
    this.historiqueCodeBarres = historiqueCodeBarres;
    this.historiqueCodeBarresFormService.resetForm(this.editForm, historiqueCodeBarres);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, historiqueCodeBarres.produit),
    );
    this.usersSharedCollection.update(users =>
      this.userService.addUserToCollectionIfMissing<IUser>(users, historiqueCodeBarres.utilisateur),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.historiqueCodeBarres?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.historiqueCodeBarres?.utilisateur)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
