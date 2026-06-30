import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IFamilleArticle } from 'app/entities/famille-article/famille-article.model';
import { FamilleArticleService } from 'app/entities/famille-article/service/famille-article.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SousFamilleArticleService } from '../service/sous-famille-article.service';
import { ISousFamilleArticle } from '../sous-famille-article.model';

import { SousFamilleArticleFormGroup, SousFamilleArticleFormService } from './sous-famille-article-form.service';

@Component({
  selector: 'jhi-sous-famille-article-update',
  templateUrl: './sous-famille-article-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SousFamilleArticleUpdate implements OnInit {
  readonly isSaving = signal(false);
  sousFamilleArticle: ISousFamilleArticle | null = null;
  statutGeneralValues = Object.keys(StatutGeneral);

  familleArticlesSharedCollection = signal<IFamilleArticle[]>([]);

  protected sousFamilleArticleService = inject(SousFamilleArticleService);
  protected sousFamilleArticleFormService = inject(SousFamilleArticleFormService);
  protected familleArticleService = inject(FamilleArticleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SousFamilleArticleFormGroup = this.sousFamilleArticleFormService.createSousFamilleArticleFormGroup();

  compareFamilleArticle = (o1: IFamilleArticle | null, o2: IFamilleArticle | null): boolean =>
    this.familleArticleService.compareFamilleArticle(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sousFamilleArticle }) => {
      this.sousFamilleArticle = sousFamilleArticle;
      if (sousFamilleArticle) {
        this.updateForm(sousFamilleArticle);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const sousFamilleArticle = this.sousFamilleArticleFormService.getSousFamilleArticle(this.editForm);
    if (sousFamilleArticle.id === null) {
      this.subscribeToSaveResponse(this.sousFamilleArticleService.create(sousFamilleArticle));
    } else {
      this.subscribeToSaveResponse(this.sousFamilleArticleService.update(sousFamilleArticle));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISousFamilleArticle | null>): void {
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

  protected updateForm(sousFamilleArticle: ISousFamilleArticle): void {
    this.sousFamilleArticle = sousFamilleArticle;
    this.sousFamilleArticleFormService.resetForm(this.editForm, sousFamilleArticle);

    this.familleArticlesSharedCollection.update(familleArticles =>
      this.familleArticleService.addFamilleArticleToCollectionIfMissing<IFamilleArticle>(
        familleArticles,
        sousFamilleArticle.familleArticle,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.familleArticleService
      .query()
      .pipe(map((res: HttpResponse<IFamilleArticle[]>) => res.body ?? []))
      .pipe(
        map((familleArticles: IFamilleArticle[]) =>
          this.familleArticleService.addFamilleArticleToCollectionIfMissing<IFamilleArticle>(
            familleArticles,
            this.sousFamilleArticle?.familleArticle,
          ),
        ),
      )
      .subscribe((familleArticles: IFamilleArticle[]) => this.familleArticlesSharedCollection.set(familleArticles));
  }
}
