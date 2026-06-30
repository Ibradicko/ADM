import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IFamilleArticle } from '../famille-article.model';
import { FamilleArticleService } from '../service/famille-article.service';

import { FamilleArticleFormGroup, FamilleArticleFormService } from './famille-article-form.service';

@Component({
  selector: 'jhi-famille-article-update',
  templateUrl: './famille-article-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class FamilleArticleUpdate implements OnInit {
  readonly isSaving = signal(false);
  familleArticle: IFamilleArticle | null = null;
  statutGeneralValues = Object.keys(StatutGeneral);

  groupeArticlesSharedCollection = signal<IGroupeArticle[]>([]);

  protected familleArticleService = inject(FamilleArticleService);
  protected familleArticleFormService = inject(FamilleArticleFormService);
  protected groupeArticleService = inject(GroupeArticleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FamilleArticleFormGroup = this.familleArticleFormService.createFamilleArticleFormGroup();

  compareGroupeArticle = (o1: IGroupeArticle | null, o2: IGroupeArticle | null): boolean =>
    this.groupeArticleService.compareGroupeArticle(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ familleArticle }) => {
      this.familleArticle = familleArticle;
      if (familleArticle) {
        this.updateForm(familleArticle);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const familleArticle = this.familleArticleFormService.getFamilleArticle(this.editForm);
    if (familleArticle.id === null) {
      this.subscribeToSaveResponse(this.familleArticleService.create(familleArticle));
    } else {
      this.subscribeToSaveResponse(this.familleArticleService.update(familleArticle));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFamilleArticle | null>): void {
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

  protected updateForm(familleArticle: IFamilleArticle): void {
    this.familleArticle = familleArticle;
    this.familleArticleFormService.resetForm(this.editForm, familleArticle);

    this.groupeArticlesSharedCollection.update(groupeArticles =>
      this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(groupeArticles, familleArticle.groupeArticle),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.groupeArticleService
      .query()
      .pipe(map((res: HttpResponse<IGroupeArticle[]>) => res.body ?? []))
      .pipe(
        map((groupeArticles: IGroupeArticle[]) =>
          this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(
            groupeArticles,
            this.familleArticle?.groupeArticle,
          ),
        ),
      )
      .subscribe((groupeArticles: IGroupeArticle[]) => this.groupeArticlesSharedCollection.set(groupeArticles));
  }
}
