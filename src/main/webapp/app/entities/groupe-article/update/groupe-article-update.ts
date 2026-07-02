import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IGroupeArticle } from '../groupe-article.model';
import { GroupeArticleService } from '../service/groupe-article.service';

import { GroupeArticleFormGroup, GroupeArticleFormService } from './groupe-article-form.service';

@Component({
  selector: 'jhi-groupe-article-update',
  templateUrl: './groupe-article-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class GroupeArticleUpdate implements OnInit {
  readonly isSaving = signal(false);
  groupeArticle: IGroupeArticle | null = null;
  statutGeneralValues = Object.keys(StatutGeneral);

  protected groupeArticleService = inject(GroupeArticleService);
  protected groupeArticleFormService = inject(GroupeArticleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GroupeArticleFormGroup = this.groupeArticleFormService.createGroupeArticleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ groupeArticle }) => {
      this.groupeArticle = groupeArticle;
      if (groupeArticle) {
        this.updateForm(groupeArticle);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const groupeArticle = this.groupeArticleFormService.getGroupeArticle(this.editForm);
    if (groupeArticle.id === null) {
      this.subscribeToSaveResponse(this.groupeArticleService.create(groupeArticle));
    } else {
      this.subscribeToSaveResponse(this.groupeArticleService.update(groupeArticle));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IGroupeArticle | null>): void {
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

  protected updateForm(groupeArticle: IGroupeArticle): void {
    this.groupeArticle = groupeArticle;
    this.groupeArticleFormService.resetForm(this.editForm, groupeArticle);
  }
}
