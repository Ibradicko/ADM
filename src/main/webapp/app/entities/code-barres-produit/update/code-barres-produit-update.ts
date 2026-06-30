import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICodeBarresProduit } from '../code-barres-produit.model';
import { CodeBarresProduitService } from '../service/code-barres-produit.service';

import { CodeBarresProduitFormGroup, CodeBarresProduitFormService } from './code-barres-produit-form.service';

@Component({
  selector: 'jhi-code-barres-produit-update',
  templateUrl: './code-barres-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CodeBarresProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  codeBarresProduit: ICodeBarresProduit | null = null;
  typeCodeBarresValues = Object.keys(TypeCodeBarres);

  produitsSharedCollection = signal<IProduit[]>([]);

  protected codeBarresProduitService = inject(CodeBarresProduitService);
  protected codeBarresProduitFormService = inject(CodeBarresProduitFormService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CodeBarresProduitFormGroup = this.codeBarresProduitFormService.createCodeBarresProduitFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ codeBarresProduit }) => {
      this.codeBarresProduit = codeBarresProduit;
      if (codeBarresProduit) {
        this.updateForm(codeBarresProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const codeBarresProduit = this.codeBarresProduitFormService.getCodeBarresProduit(this.editForm);
    if (codeBarresProduit.id === null) {
      this.subscribeToSaveResponse(this.codeBarresProduitService.create(codeBarresProduit));
    } else {
      this.subscribeToSaveResponse(this.codeBarresProduitService.update(codeBarresProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICodeBarresProduit | null>): void {
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

  protected updateForm(codeBarresProduit: ICodeBarresProduit): void {
    this.codeBarresProduit = codeBarresProduit;
    this.codeBarresProduitFormService.resetForm(this.editForm, codeBarresProduit);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, codeBarresProduit.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.codeBarresProduit?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
