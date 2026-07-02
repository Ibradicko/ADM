import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { GroupeArticleService } from 'app/entities/groupe-article/service/groupe-article.service';
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
  uniteMesuresSharedCollection = signal<IUniteMesure[]>([]);
  readonly sauvegardeEtApprovisionnement = signal(false);
  readonly articleSauvegarde = signal<IProduit | null>(null);
  readonly boutiqueVerrouillee = computed(() => this.permissionsUi.estProfilBoutique() && !this.isEdition());
  readonly boutiqueCourante = computed(() => this.editForm.controls.boutique.value);
  readonly groupeSelectionne = computed(() => this.editForm.controls.groupeArticle.value);

  protected permissionsUi = inject(UiPermissionService);
  protected produitService = inject(ProduitService);
  protected produitFormService = inject(ProduitFormService);
  protected boutiqueService = inject(BoutiqueService);
  protected groupeArticleService = inject(GroupeArticleService);
  protected uniteMesureService = inject(UniteMesureService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);

  editForm: ProduitFormGroup = this.produitFormService.createProduitFormGroup();

  compareBoutique = (o1: IBoutique | null, o2: IBoutique | null): boolean => this.boutiqueService.compareBoutique(o1, o2);

  compareGroupeArticle = (o1: IGroupeArticle | null, o2: IGroupeArticle | null): boolean =>
    this.groupeArticleService.compareGroupeArticle(o1, o2);

  compareUniteMesure = (o1: IUniteMesure | null, o2: IUniteMesure | null): boolean => this.uniteMesureService.compareUniteMesure(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produit }) => {
      this.produit = produit;
      if (produit) {
        this.updateForm(produit);
      } else {
        this.initialiserValeursParDefaut();
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
    this.enregistrer(false);
  }

  saveAndSupply(): void {
    this.enregistrer(true);
  }

  approvisionnerArticle(produit: IProduit | null = this.produit ?? this.articleSauvegarde()): void {
    if (!produit?.id) {
      return;
    }

    this.router.navigate(['/stock-operations'], {
      queryParams: { onglet: 'approvisionnement', produitId: produit.id },
    });
  }

  onGroupeArticleChange(): void {
    this.editForm.controls.tauxRedevanceApplicable.setValue(null);
  }

  protected enregistrer(approvisionnerApresSauvegarde: boolean): void {
    this.isSaving.set(true);
    this.sauvegardeEtApprovisionnement.set(approvisionnerApresSauvegarde);
    const produit = this.produitFormService.getProduit(this.editForm);
    if (produit.id === null) {
      this.subscribeToSaveResponse(this.produitService.create(produit));
    } else {
      this.subscribeToSaveResponse(this.produitService.update(produit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProduit | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: produit => this.onSaveSuccess(produit),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(produit: IProduit | null): void {
    this.articleSauvegarde.set(produit);
    if (this.sauvegardeEtApprovisionnement() && produit?.id) {
      this.approvisionnerArticle(produit);
      return;
    }
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
    this.uniteMesuresSharedCollection.update(uniteMesures =>
      this.uniteMesureService.addUniteMesureToCollectionIfMissing<IUniteMesure>(uniteMesures, produit.uniteMesure),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boutiqueService
      .query({ size: 500, sort: ['nom,asc'] })
      .pipe(map((res: HttpResponse<IBoutique[]>) => res.body ?? []))
      .pipe(
        map((boutiques: IBoutique[]) =>
          this.boutiqueService.addBoutiqueToCollectionIfMissing<IBoutique>(boutiques, this.produit?.boutique),
        ),
      )
      .subscribe((boutiques: IBoutique[]) => {
        const boutiquesAccessibles = this.filtrerBoutiquesPourManager(boutiques);
        this.boutiquesSharedCollection.set(boutiquesAccessibles);
        this.selectionnerBoutiqueParDefaut(boutiquesAccessibles);
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
    if (this.boutiqueVerrouillee()) {
      this.editForm.controls.boutique.disable();
    }
  }

  private filtrerBoutiquesPourManager(boutiques: IBoutique[]): IBoutique[] {
    if (!this.permissionsUi.estProfilBoutique() || this.permissionsUi.estAdmin() || this.permissionsUi.estProfilAdm()) {
      return boutiques;
    }

    const boutiqueIds = new Set(this.permissionsUi.boutiqueIds());
    return boutiques.filter(boutique => boutiqueIds.has(boutique.id));
  }

  private initialiserValeursParDefaut(): void {
    if (!this.editForm.controls.statut.value) {
      this.editForm.controls.statut.setValue('ACTIF');
    }
    if (!this.editForm.controls.typePrix.value) {
      this.editForm.controls.typePrix.setValue('STANDARD');
    }
  }
}
