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
import { TypeRegleRedevance } from 'app/entities/enumerations/type-regle-redevance.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IRegleRedevance } from '../regle-redevance.model';
import { RegleRedevanceService } from '../service/regle-redevance.service';

import { RegleRedevanceFormGroup, RegleRedevanceFormService } from './regle-redevance-form.service';
import { LocataireService } from 'app/entities/locataire/service/locataire.service';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';

@Component({
  selector: 'jhi-regle-redevance-update',
  templateUrl: './regle-redevance-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class RegleRedevanceUpdate implements OnInit {
  readonly isSaving = signal(false);
  regleRedevance: IRegleRedevance | null = null;
  typeRegleRedevanceValues = Object.keys(TypeRegleRedevance);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  locatairesSharedCollection = signal<ILocataire[]>([]);
  groupeArticlesSharedCollection = signal<IGroupeArticle[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected regleRedevanceService = inject(RegleRedevanceService);
  protected regleRedevanceFormService = inject(RegleRedevanceFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected locataireService = inject(LocataireService);
  protected groupeArticleService = inject(GroupeArticleService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RegleRedevanceFormGroup = this.regleRedevanceFormService.createRegleRedevanceFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareLocataire = (o1: ILocataire | null, o2: ILocataire | null): boolean => this.locataireService.compareLocataire(o1, o2);

  compareGroupeArticle = (o1: IGroupeArticle | null, o2: IGroupeArticle | null): boolean =>
    this.groupeArticleService.compareGroupeArticle(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ regleRedevance }) => {
      this.regleRedevance = regleRedevance;
      if (regleRedevance) {
        this.updateForm(regleRedevance);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const regleRedevance = this.regleRedevanceFormService.getRegleRedevance(this.editForm);
    if (regleRedevance.id === null) {
      this.subscribeToSaveResponse(this.regleRedevanceService.create(regleRedevance));
    } else {
      this.subscribeToSaveResponse(this.regleRedevanceService.update(regleRedevance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRegleRedevance | null>): void {
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

  protected updateForm(regleRedevance: IRegleRedevance): void {
    this.regleRedevance = regleRedevance;
    this.regleRedevanceFormService.resetForm(this.editForm, regleRedevance);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, regleRedevance.boutique),
    );
    this.locatairesSharedCollection.update(locataires =>
      this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, regleRedevance.locataire),
    );
    this.groupeArticlesSharedCollection.update(groupeArticles =>
      this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(groupeArticles, regleRedevance.groupeArticle),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, regleRedevance.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query()
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.regleRedevance?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => this.boutiquesSharedCollection.set(boutiques));

    this.locataireService
      .query()
      .pipe(map((res: HttpResponse<ILocataire[]>) => res.body ?? []))
      .pipe(
        map((locataires: ILocataire[]) =>
          this.locataireService.addLocataireToCollectionIfMissing<ILocataire>(locataires, this.regleRedevance?.locataire),
        ),
      )
      .subscribe((locataires: ILocataire[]) => this.locatairesSharedCollection.set(locataires));

    this.groupeArticleService
      .query()
      .pipe(map((res: HttpResponse<IGroupeArticle[]>) => res.body ?? []))
      .pipe(
        map((groupeArticles: IGroupeArticle[]) =>
          this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(
            groupeArticles,
            this.regleRedevance?.groupeArticle,
          ),
        ),
      )
      .subscribe((groupeArticles: IGroupeArticle[]) => this.groupeArticlesSharedCollection.set(groupeArticles));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.regleRedevance?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
