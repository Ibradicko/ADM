import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
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

  boutiquesSharedCollection = signal<IBoutique[]>([]);

  protected groupeArticleService = inject(GroupeArticleService);
  protected groupeArticleFormService = inject(GroupeArticleFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GroupeArticleFormGroup = this.groupeArticleFormService.createGroupeArticleFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ groupeArticle }) => {
      this.groupeArticle = groupeArticle;
      if (groupeArticle) {
        this.updateForm(groupeArticle);
      }

      this.loadRelationshipsOptions();
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

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, groupeArticle.boutique),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.groupeArticle?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));
  }
}
