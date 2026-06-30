import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map, switchMap } from 'rxjs/operators';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { DepotStockService } from 'app/entities/depot-stock/service/depot-stock.service';
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { IFamilleArticle } from 'app/entities/famille-article/famille-article.model';
import { FamilleArticleService } from 'app/entities/famille-article/service/famille-article.service';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
import { ISousFamilleArticle } from 'app/entities/sous-famille-article/sous-famille-article.model';
import { SousFamilleArticleService } from 'app/entities/sous-famille-article/service/sous-famille-article.service';
import { StockProduitService } from 'app/entities/stock-produit/service/stock-produit.service';
import { IUniteMesure } from 'app/entities/unite-mesure/unite-mesure.model';
import { UniteMesureService } from 'app/entities/unite-mesure/service/unite-mesure.service';
import { AlertError } from 'app/shared/alert/alert-error';

import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';
import { ProduitFormGroup, ProduitFormService } from './produit-form.service';

@Component({
  selector: 'jhi-produit-update',
  templateUrl: './produit-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, RouterLink, TranslateModule],
})
export class ProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  produit: IProduit | null = null;
  typePrixValues = Object.keys(TypePrix);
  statutGeneralValues = Object.keys(StatutGeneral);

  boutiquesSharedCollection = signal<IBoutique[]>([]);
  groupeArticlesSharedCollection = signal<IGroupeArticle[]>([]);
  familleArticlesSharedCollection = signal<IFamilleArticle[]>([]);
  sousFamilleArticlesSharedCollection = signal<ISousFamilleArticle[]>([]);
  uniteMesuresSharedCollection = signal<IUniteMesure[]>([]);
  depotsSharedCollection = signal<IDepotStock[]>([]);
  readonly stockInitialDepotId = signal<number | null>(null);
  readonly stockInitialQuantite = signal(0);
  readonly stockInitialErreur = signal(false);

  protected produitService = inject(ProduitService);
  protected produitFormService = inject(ProduitFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected groupeArticleService = inject(GroupeArticleService);
  protected familleArticleService = inject(FamilleArticleService);
  protected sousFamilleArticleService = inject(SousFamilleArticleService);
  protected uniteMesureService = inject(UniteMesureService);
  protected depotStockService = inject(DepotStockService);
  protected stockProduitService = inject(StockProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: ProduitFormGroup = this.produitFormService.createProduitFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareGroupeArticle = (o1: IGroupeArticle | null, o2: IGroupeArticle | null): boolean =>
    this.groupeArticleService.compareGroupeArticle(o1, o2);

  compareFamilleArticle = (o1: IFamilleArticle | null, o2: IFamilleArticle | null): boolean =>
    this.familleArticleService.compareFamilleArticle(o1, o2);

  compareSousFamilleArticle = (o1: ISousFamilleArticle | null, o2: ISousFamilleArticle | null): boolean =>
    this.sousFamilleArticleService.compareSousFamilleArticle(o1, o2);

  compareUniteMesure = (o1: IUniteMesure | null, o2: IUniteMesure | null): boolean => this.uniteMesureService.compareUniteMesure(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produit }) => {
      this.produit = produit;
      if (produit) {
        this.updateForm(produit);
      }

      this.loadRelationshipsOptions();
    });
  }

  isEdition(): boolean {
    return this.produit !== null;
  }

  titrePage(): string {
    return this.isEdition() ? 'productCustom.update.editTitle' : 'productCustom.update.createTitle';
  }

  sousTitrePage(): string {
    return this.isEdition() ? 'productCustom.update.editSubtitle' : 'productCustom.update.createSubtitle';
  }

  champInvalide(nomChamp: string): boolean {
    const champ = this.editForm.get(nomChamp);
    return !!champ && champ.invalid && (champ.dirty || champ.touched);
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
  }

  formatTaux(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur}%` : '--';
  }

  formatValeur(valeur: string | null | undefined): string {
    return valeur?.trim() ? valeur : '--';
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    if (!this.isEdition() && this.stockInitialQuantite() > 0 && !this.stockInitialDepotId()) {
      this.stockInitialErreur.set(true);
      return;
    }
    this.isSaving.set(true);
    this.stockInitialErreur.set(false);
    const produit = this.produitFormService.getProduit(this.editForm);
    if (produit.id === null) {
      this.subscribeToSaveResponse(
        this.produitService.create(produit).pipe(
          switchMap(produitCree => {
            const depotId = this.stockInitialDepotId();
            if (this.stockInitialQuantite() <= 0 || !depotId) {
              return [produitCree];
            }
            return this.stockProduitService
              .create({
                id: null,
                quantiteTheorique: this.stockInitialQuantite(),
                stockAlerte: null,
                produit: { id: produitCree.id, designation: produitCree.designation },
                depot: { id: depotId },
              })
              .pipe(map(() => produitCree));
          }),
        ),
      );
    } else {
      this.subscribeToSaveResponse(this.produitService.update(produit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProduit | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Point d'extension si une gestion metier plus fine des erreurs est ajoutee plus tard.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(produit: IProduit): void {
    this.produit = produit;
    this.produitFormService.resetForm(this.editForm, produit);

    this.boutiquesSharedCollection.update(boutiques =>
      this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, produit.boutique),
    );
    this.groupeArticlesSharedCollection.update(groupeArticles =>
      this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(groupeArticles, produit.groupeArticle),
    );
    this.familleArticlesSharedCollection.update(familleArticles =>
      this.familleArticleService.addFamilleArticleToCollectionIfMissing<IFamilleArticle>(familleArticles, produit.familleArticle),
    );
    this.sousFamilleArticlesSharedCollection.update(sousFamilleArticles =>
      this.sousFamilleArticleService.addSousFamilleArticleToCollectionIfMissing<ISousFamilleArticle>(
        sousFamilleArticles,
        produit.sousFamilleArticle,
      ),
    );
    this.uniteMesuresSharedCollection.update(uniteMesures =>
      this.uniteMesureService.addUniteMesureToCollectionIfMissing<IUniteMesure>(uniteMesures, produit.uniteMesure),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.depotStockService
      .query({ size: 500, sort: ['code,asc'] })
      .pipe(map((res: HttpResponse<IDepotStock[]>) => res.body ?? []))
      .subscribe((depots: IDepotStock[]) => this.depotsSharedCollection.set(depots));

    this.boutiqueService
      .query({ size: 500, sort: ['nom,asc'] })
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.produit?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => {
        this.boutiquesSharedCollection.set(boutiques);
        this.selectionnerBoutiqueParDefaut(boutiques);
      });

    this.groupeArticleService
      .query({ size: 500, sort: ['libelle,asc'] })
      .pipe(map((res: HttpResponse<IGroupeArticle[]>) => res.body ?? []))
      .pipe(
        map((groupeArticles: IGroupeArticle[]) =>
          this.groupeArticleService.addGroupeArticleToCollectionIfMissing<IGroupeArticle>(groupeArticles, this.produit?.groupeArticle),
        ),
      )
      .subscribe((groupeArticles: IGroupeArticle[]) => this.groupeArticlesSharedCollection.set(groupeArticles));

    this.familleArticleService
      .query({ size: 500, sort: ['libelle,asc'] })
      .pipe(map((res: HttpResponse<IFamilleArticle[]>) => res.body ?? []))
      .pipe(
        map((familleArticles: IFamilleArticle[]) =>
          this.familleArticleService.addFamilleArticleToCollectionIfMissing<IFamilleArticle>(familleArticles, this.produit?.familleArticle),
        ),
      )
      .subscribe((familleArticles: IFamilleArticle[]) => this.familleArticlesSharedCollection.set(familleArticles));

    this.sousFamilleArticleService
      .query({ size: 500, sort: ['libelle,asc'] })
      .pipe(map((res: HttpResponse<ISousFamilleArticle[]>) => res.body ?? []))
      .pipe(
        map((sousFamilleArticles: ISousFamilleArticle[]) =>
          this.sousFamilleArticleService.addSousFamilleArticleToCollectionIfMissing<ISousFamilleArticle>(
            sousFamilleArticles,
            this.produit?.sousFamilleArticle,
          ),
        ),
      )
      .subscribe((sousFamilleArticles: ISousFamilleArticle[]) => this.sousFamilleArticlesSharedCollection.set(sousFamilleArticles));

    this.uniteMesureService
      .query({ size: 500, sort: ['code,asc'] })
      .pipe(map((res: HttpResponse<IUniteMesure[]>) => res.body ?? []))
      .pipe(
        map((uniteMesures: IUniteMesure[]) =>
          this.uniteMesureService.addUniteMesureToCollectionIfMissing<IUniteMesure>(uniteMesures, this.produit?.uniteMesure),
        ),
      )
      .subscribe((uniteMesures: IUniteMesure[]) => this.uniteMesuresSharedCollection.set(uniteMesures));
  }

  private selectionnerBoutiqueParDefaut(boutiques: IBoutique[]): void {
    if (this.isEdition() || this.editForm.controls.boutique.value || boutiques.length !== 1) {
      return;
    }

    this.editForm.controls.boutique.setValue(boutiques[0]);
  }

  depotsStockInitial(): IDepotStock[] {
    const boutiqueId = this.editForm.controls.boutique.value?.id;
    return this.depotsSharedCollection().filter(depot => !boutiqueId || depot.boutique?.id === boutiqueId);
  }
}
