import dayjs from 'dayjs/esm';

import { DecimalPipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { TranslateDirective } from 'app/shared/language';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { BoutiqueService } from 'app/entities/boutique/service/boutique.service';
import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { CalculRedevanceService } from 'app/entities/calcul-redevance/service/calcul-redevance.service';
import { StatutRedevance } from 'app/entities/enumerations/statut-redevance.model';
import { ExploitationBoutiqueService } from 'app/entities/exploitation-boutique/service/exploitation-boutique.service';
import { IPaiementRedevance, NewPaiementRedevance } from 'app/entities/paiement-redevance/paiement-redevance.model';
import { PaiementRedevanceService } from 'app/entities/paiement-redevance/service/paiement-redevance.service';
import { IRegleRedevance } from 'app/entities/regle-redevance/regle-redevance.model';
import { RegleRedevanceService } from 'app/entities/regle-redevance/service/regle-redevance.service';
import { IRegularisationRedevance, NewRegularisationRedevance } from 'app/entities/regularisation-redevance/regularisation-redevance.model';
import { RegularisationRedevanceService } from 'app/entities/regularisation-redevance/service/regularisation-redevance.service';
import { RoyaltyReceiptPrinterService } from './royalty-receipt-printer.service';

type OngletRoyalties = 'calculs' | 'historique' | 'regles';

interface MessageRedevance {
  type: 'success' | 'danger' | 'info';
  key: string;
}

@Component({
  selector: 'jhi-royalties',
  templateUrl: './royalties.html',
  styleUrl: './royalties.scss',
  imports: [DecimalPipe, FontAwesomeModule, FormsModule, RouterLink, TranslateDirective, TranslateModule],
})
export default class RoyaltiesComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);

  readonly boutiques = signal<IBoutique[]>([]);
  readonly calculs = signal<ICalculRedevance[]>([]);
  readonly paiements = signal<IPaiementRedevance[]>([]);
  readonly regularisations = signal<IRegularisationRedevance[]>([]);
  readonly regles = signal<IRegleRedevance[]>([]);
  readonly chargement = signal(false);
  readonly enregistrement = signal(false);
  readonly message = signal<MessageRedevance | null>(null);
  readonly ongletActif = signal<OngletRoyalties>('calculs');
  readonly modalDetailOuvert = signal(false);

  readonly boutiqueId = signal<number | null>(null);
  readonly statut = signal<string>('');
  readonly dateDebut = signal<string>(dayjs().startOf('month').format('YYYY-MM-DD'));
  readonly dateFin = signal<string>(dayjs().endOf('month').format('YYYY-MM-DD'));
  readonly recherche = signal('');
  readonly calculSelectionneId = signal<number | null>(null);
  readonly statutCible = signal<keyof typeof StatutRedevance>('VALIDEE');

  readonly paiementMontant = signal<number | null>(null);
  readonly paiementDate = signal<string>(dayjs().format('YYYY-MM-DD'));
  readonly paiementMode = signal('VIREMENT');
  readonly paiementCommentaire = signal('');
  readonly modesPaiement = ['VIREMENT', 'ESPECES', 'CARTE', 'MOBILE_MONEY'];

  readonly regularisationMontant = signal<number | null>(null);
  readonly regularisationDate = signal<string>(dayjs().format('YYYY-MM-DD'));
  readonly regularisationMotif = signal('');

  readonly statutsDisponibles = Object.values(StatutRedevance);

  readonly calculsFiltres = computed(() => {
    const texte = this.recherche().trim().toLowerCase();
    const debut = this.dateDebut();
    const fin = this.dateFin();

    return this.calculs().filter(calcul => {
      if (this.boutiqueId() && calcul.boutique?.id !== this.boutiqueId()) {
        return false;
      }
      if (this.statut() && calcul.statut !== this.statut()) {
        return false;
      }
      if (debut && calcul.periodeDebut?.isBefore(dayjs(debut), 'day')) {
        return false;
      }
      if (fin && calcul.periodeFin?.isAfter(dayjs(fin), 'day')) {
        return false;
      }
      if (!texte) {
        return true;
      }

      const haystack = [calcul.reference, calcul.boutique?.nom, calcul.locataire?.nom, calcul.statut]
        .filter(Boolean)
        .join(' ')
        .toLowerCase();

      return haystack.includes(texte);
    });
  });

  /** Options du filtre boutique : "Boutique - Locataire" derive des calculs charges, sans appel API dedie. */
  readonly boutiqueOptions = computed(() => {
    const options = new Map<number, string>();
    for (const calcul of this.calculs()) {
      const id = calcul.boutique?.id;
      if (!id || options.has(id)) {
        continue;
      }
      const nomBoutique = calcul.boutique?.nom ?? `#${id}`;
      options.set(id, calcul.locataire?.nom ? `${nomBoutique} - ${calcul.locataire.nom}` : nomBoutique);
    }
    return Array.from(options.entries())
      .map(([id, label]) => ({ id, label }))
      .sort((a, b) => a.label.localeCompare(b.label));
  });

  readonly calculSelectionne = computed(
    () => this.calculs().find(calcul => calcul.id === this.calculSelectionneId()) ?? this.calculsFiltres()[0] ?? null,
  );
  readonly paiementsSelectionnes = computed(() =>
    this.paiements()
      .filter(paiement => paiement.calcul?.id === this.calculSelectionne()?.id)
      .sort((a, b) => (b.datePaiement?.valueOf() ?? 0) - (a.datePaiement?.valueOf() ?? 0)),
  );
  readonly montantPayeSelectionne = computed(() =>
    this.paiementsSelectionnes().reduce((total, paiement) => total + (paiement.montant ?? 0), 0),
  );
  readonly resteSelectionne = computed(() =>
    Math.max(0, (this.calculSelectionne()?.montantRedevance ?? 0) - this.montantPayeSelectionne()),
  );
  readonly progressionPaiementSelectionne = computed(() => {
    const total = this.calculSelectionne()?.montantRedevance ?? 0;
    if (total <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((this.montantPayeSelectionne() / total) * 100));
  });
  readonly paiementPossible = computed(
    () => this.permissionsUi.peutGererRedevances() && !!this.calculSelectionne() && this.resteSelectionne() > 0 && !this.calculAnnule(),
  );
  readonly calculAnnule = computed(() => this.calculSelectionne()?.statut === StatutRedevance.ANNULEE);
  readonly montantPaiementInvalide = computed(() => {
    const montant = this.paiementMontant();
    return montant !== null && (montant <= 0 || montant > this.resteSelectionne());
  });
  readonly regularisationsSelectionnees = computed(() =>
    this.regularisations()
      .filter(regularisation => regularisation.calcul?.id === this.calculSelectionne()?.id)
      .sort((a, b) => (b.dateRegularisation?.valueOf() ?? 0) - (a.dateRegularisation?.valueOf() ?? 0)),
  );
  readonly resume = computed(() => {
    const calculs = this.calculsFiltres().filter(calcul => calcul.statut !== StatutRedevance.ANNULEE);
    const ids = new Set(calculs.map(calcul => calcul.id));
    const paiements = this.paiements().filter(paiement => ids.has(paiement.calcul?.id ?? -1));
    const regularisations = this.regularisations().filter(regularisation => ids.has(regularisation.calcul?.id ?? -1));

    const chiffreAffaires = calculs.reduce((total, calcul) => total + (calcul.chiffreAffaires ?? 0), 0);
    const montantDu = calculs.reduce((total, calcul) => total + (calcul.montantRedevance ?? 0), 0);
    const montantPaye = paiements.reduce((total, paiement) => total + (paiement.montant ?? 0), 0);
    const montantRegularise = regularisations.reduce((total, regularisation) => total + (regularisation.montant ?? 0), 0);

    return {
      nombreCalculs: calculs.length,
      chiffreAffaires,
      montantDu,
      montantPaye,
      montantRegularise,
      solde: montantDu - montantPaye - montantRegularise,
    };
  });
  readonly reglesActives = computed(() => this.regles().filter(regle => regle.actif));
  readonly estVueLocataire = computed(() => this.permissionsUi.estLocataire());
  readonly estVueVendeur = computed(() => this.permissionsUi.estProfilVente() && !this.permissionsUi.estProfilBoutique());
  /** Manager boutique ou vendeur : lecture seule, restreinte aux boutiques accessibles de l'utilisateur. */
  readonly estVuePerimetreBoutique = computed(
    () =>
      (this.permissionsUi.estProfilBoutique() || this.permissionsUi.estProfilVente()) &&
      !this.permissionsUi.estAdmin() &&
      !this.permissionsUi.estProfilAdm(),
  );
  readonly estVueGlobale = computed(() => !this.estVueLocataire() && !this.estVuePerimetreBoutique());
  readonly afficherColonneBoutique = computed(() => !this.estVuePerimetreBoutique() || this.boutiques().length > 1);
  readonly afficherColonneLocataire = computed(() => this.estVueGlobale());

  readonly historiquePaiements = computed(() => {
    const idsVisibles = new Set(this.calculsFiltres().map(calcul => calcul.id));
    const parCalcul = new Map(this.calculs().map(calcul => [calcul.id, calcul] as const));
    return this.paiements()
      .filter(paiement => idsVisibles.has(paiement.calcul?.id ?? -1))
      .map(paiement => ({ paiement, calcul: parCalcul.get(paiement.calcul?.id ?? -1) ?? null }))
      .sort((a, b) => (b.paiement.datePaiement?.valueOf() ?? 0) - (a.paiement.datePaiement?.valueOf() ?? 0));
  });

  private readonly boutiqueService = inject(BoutiqueService);
  private readonly exploitationBoutiqueService = inject(ExploitationBoutiqueService);
  private readonly calculRedevanceService = inject(CalculRedevanceService);
  private readonly paiementRedevanceService = inject(PaiementRedevanceService);
  private readonly regularisationRedevanceService = inject(RegularisationRedevanceService);
  private readonly regleRedevanceService = inject(RegleRedevanceService);
  private readonly receiptPrinter = inject(RoyaltyReceiptPrinterService);

  ngOnInit(): void {
    void this.initialiser();
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
  }

  formatDate(valeur: dayjs.Dayjs | null | undefined, pattern = 'DD/MM/YYYY'): string {
    return valeur ? valeur.format(pattern) : '--';
  }

  statutKey(statut: string | null | undefined): string {
    return statut ? `admSupervisionVentesApp.StatutRedevance.${statut}` : 'royalties.common.unavailable';
  }

  statutClasse(statut: string | null | undefined): string {
    return `royalty-status royalty-status--${(statut ?? 'UNKNOWN').toLowerCase().replaceAll('_', '-')}`;
  }

  progressionStyle(): string {
    return `${this.progressionPaiementSelectionne()}%`;
  }

  definirOnglet(onglet: OngletRoyalties): void {
    this.ongletActif.set(onglet);
  }

  selectionnerCalcul(id: number): void {
    this.calculSelectionneId.set(id);
    this.paiementMontant.set(this.resteSelectionne() || null);
  }

  ouvrirDetailCalcul(id: number): void {
    this.selectionnerCalcul(id);
    this.modalDetailOuvert.set(true);
  }

  fermerModalDetail(): void {
    this.modalDetailOuvert.set(false);
  }

  solderRedevance(): void {
    this.paiementMontant.set(this.resteSelectionne() || null);
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);
    try {
      // Sequential on purpose: chargerRedevances() reads this.boutiques() (built by
      // chargerReferentiels()) both to scope the calcul-redevances query and to filter the
      // response for locataire/manager boutique views. Running them in parallel raced the two
      // async chains and could filter every result out when boutiques() was still empty at
      // read time.
      await this.chargerReferentiels();
      await this.chargerRedevances();
      if (this.calculSelectionne() && !this.calculSelectionneId()) {
        this.calculSelectionneId.set(this.calculSelectionne()?.id ?? null);
      }
    } catch {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.loadFailed',
      });
    } finally {
      this.chargement.set(false);
    }
  }

  async mettreAJourStatutCalcul(): Promise<void> {
    const calcul = this.calculSelectionne();
    if (!calcul || !this.permissionsUi.peutGererRedevances()) {
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);

    try {
      await firstValueFrom(
        this.calculRedevanceService.partialUpdate({
          id: calcul.id,
          statut: this.statutCible(),
        }),
      );
      await this.recharger();
      this.message.set({
        type: 'success',
        key: 'royalties.messages.statusUpdated',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.statusUpdateFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async enregistrerPaiement(): Promise<void> {
    const calcul = this.calculSelectionne();
    if (!calcul || !this.paiementPossible()) {
      return;
    }
    if (!this.paiementMontant() || this.paiementMontant()! <= 0) {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.paymentAmountRequired',
      });
      return;
    }
    if (this.paiementMontant()! > this.resteSelectionne()) {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.paymentExceedsBalance',
      });
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);

    try {
      const payload: NewPaiementRedevance = {
        id: null,
        reference: `PAY-RED-${Date.now()}`,
        montant: this.paiementMontant(),
        datePaiement: this.paiementDate() ? dayjs(this.paiementDate()) : dayjs(),
        modePaiement: this.paiementMode(),
        commentaire: this.paiementCommentaire(),
        calcul: { id: calcul.id, reference: calcul.reference ?? null },
      };

      const paiementCree = await firstValueFrom(this.paiementRedevanceService.create(payload));
      this.paiementMontant.set(null);
      this.paiementMode.set('VIREMENT');
      this.paiementCommentaire.set('');
      await this.recharger();
      this.paiementMontant.set(this.resteSelectionne() || null);
      this.message.set({
        type: 'success',
        key: 'royalties.messages.paymentSaved',
      });
      this.imprimerRecuPaiement(paiementCree, calcul);
    } catch {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.paymentSaveFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  imprimerRecuPaiement(paiement: IPaiementRedevance, calculSource?: ICalculRedevance | null): void {
    const calcul = calculSource ?? this.calculs().find(item => item.id === paiement.calcul?.id) ?? this.calculSelectionne();
    if (!calcul) {
      return;
    }

    const paiementsDuCalcul = this.paiements().filter(item => item.calcul?.id === calcul.id);
    const imprime = this.receiptPrinter.imprimer(paiement, calcul, paiementsDuCalcul);
    if (!imprime) {
      this.message.set({ type: 'danger', key: 'royalties.messages.printFailed' });
    }
  }

  imprimerDernierRecuCalcul(calculId: number): void {
    const dernierPaiement = this.paiements()
      .filter(paiement => paiement.calcul?.id === calculId)
      .sort((a, b) => (b.datePaiement?.valueOf() ?? 0) - (a.datePaiement?.valueOf() ?? 0))[0];
    if (dernierPaiement) {
      this.imprimerRecuPaiement(dernierPaiement);
    }
  }

  montantPayeCalcul(calculId: number): number {
    return this.paiements()
      .filter(paiement => paiement.calcul?.id === calculId)
      .reduce((total, paiement) => total + (paiement.montant ?? 0), 0);
  }

  resteCalcul(calcul: ICalculRedevance): number {
    return Math.max(0, (calcul.montantRedevance ?? 0) - this.montantPayeCalcul(calcul.id));
  }

  async enregistrerRegularisation(): Promise<void> {
    const calcul = this.calculSelectionne();
    if (!calcul || !this.permissionsUi.peutGererRedevances()) {
      return;
    }
    if (!this.regularisationMontant() || !this.regularisationMotif().trim()) {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.adjustmentFieldsRequired',
      });
      return;
    }

    this.enregistrement.set(true);
    this.message.set(null);

    try {
      const payload: NewRegularisationRedevance = {
        id: null,
        reference: `REG-${Date.now()}`,
        montant: this.regularisationMontant(),
        motif: this.regularisationMotif().trim(),
        dateRegularisation: this.regularisationDate() ? dayjs(this.regularisationDate()) : dayjs(),
        calcul: { id: calcul.id, reference: calcul.reference ?? null },
      };

      await firstValueFrom(this.regularisationRedevanceService.create(payload));
      this.regularisationMontant.set(null);
      this.regularisationMotif.set('');
      await this.recharger();
      this.message.set({
        type: 'success',
        key: 'royalties.messages.adjustmentSaved',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'royalties.messages.adjustmentSaveFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  private async initialiser(): Promise<void> {
    await this.recharger();
  }

  private async chargerReferentiels(): Promise<void> {
    if (this.estVueLocataire()) {
      const exploitations = await firstValueFrom(this.exploitationBoutiqueService.findMesExploitations());
      const boutiques = exploitations
        .filter(exploitation => exploitation.statut === 'ACTIF' && exploitation.boutique?.id)
        .map(exploitation => exploitation.boutique as IBoutique);

      this.boutiques.set(boutiques);
      if (!this.boutiqueId() && boutiques.length === 1) {
        this.boutiqueId.set(boutiques[0].id);
      }
      return;
    }

    if (this.estVuePerimetreBoutique()) {
      const ids = this.permissionsUi.boutiqueIds();
      const boutiquesResponse = await firstValueFrom(
        this.boutiqueService.query({
          ...(ids.length ? { 'id.in': ids.join(',') } : { 'id.equals': -1 }),
          size: 500,
          sort: ['nom,asc'],
        }),
      );
      this.boutiques.set(boutiquesResponse.body ?? []);
      if (!this.boutiqueId() && this.boutiques().length === 1) {
        this.boutiqueId.set(this.boutiques()[0].id);
      }
      return;
    }

    // Vue globale (admin/manager ADM) : le filtre boutique est derive des calculs charges (boutiqueOptions),
    // aucun referentiel boutique/locataire separe n'est necessaire ici.
    this.boutiques.set([]);
  }

  private async chargerRedevances(): Promise<void> {
    const calculParams: Record<string, unknown> = { size: 1000, sort: ['dateCalcul,desc'] };
    const paiementParams: Record<string, unknown> = { size: 1000, sort: ['datePaiement,desc'] };
    const regularisationParams: Record<string, unknown> = { size: 1000, sort: ['dateRegularisation,desc'] };
    const regleParams: Record<string, unknown> = { size: 1000, sort: ['priorite,asc'] };

    if ((this.estVueLocataire() || this.estVuePerimetreBoutique()) && this.boutiques().length > 0) {
      const boutiqueIds = this.boutiques()
        .map(boutique => boutique.id)
        .filter((id): id is number => !!id)
        .join(',');
      calculParams['boutiqueId.in'] = boutiqueIds;
      regleParams['boutiqueId.in'] = boutiqueIds;
    }

    const calculsResponse = await this.queryOptionnelle(
      () => firstValueFrom(this.calculRedevanceService.query(calculParams)),
      new HttpResponse<ICalculRedevance[]>({ body: [] }),
    );
    const calculs = this.filtrerCalculsBoutique(calculsResponse.body ?? []);
    const calculIds = new Set(calculs.map(calcul => calcul.id));
    const calculIdsParam = Array.from(calculIds).join(',');

    if (calculIdsParam) {
      paiementParams['calculId.in'] = calculIdsParam;
      regularisationParams['calculId.in'] = calculIdsParam;
    } else if (this.estVueLocataire() || this.estVuePerimetreBoutique()) {
      paiementParams['calculId.equals'] = -1;
      regularisationParams['calculId.equals'] = -1;
    }

    const [paiementsResponse, regularisationsResponse, reglesResponse] = await Promise.all([
      this.queryOptionnelle(
        () => firstValueFrom(this.paiementRedevanceService.query(paiementParams)),
        new HttpResponse<IPaiementRedevance[]>({ body: [] }),
      ),
      this.queryOptionnelle(
        () => firstValueFrom(this.regularisationRedevanceService.query(regularisationParams)),
        new HttpResponse<IRegularisationRedevance[]>({ body: [] }),
      ),
      this.estVueLocataire() || this.estVuePerimetreBoutique()
        ? Promise.resolve(new HttpResponse<IRegleRedevance[]>({ body: [] }))
        : firstValueFrom(this.regleRedevanceService.query(regleParams)),
    ]);

    this.calculs.set(calculs);
    this.paiements.set((paiementsResponse.body ?? []).filter(paiement => calculIds.has(paiement.calcul?.id ?? -1)));
    this.regularisations.set((regularisationsResponse.body ?? []).filter(regularisation => calculIds.has(regularisation.calcul?.id ?? -1)));
    this.regles.set(reglesResponse.body ?? []);
    if (!this.calculSelectionneId() && this.calculs()[0]) {
      this.selectionnerCalcul(this.calculs()[0].id);
    }
  }

  private filtrerCalculsBoutique(calculs: ICalculRedevance[]): ICalculRedevance[] {
    if (!this.estVueLocataire() && !this.estVuePerimetreBoutique()) {
      return calculs;
    }

    const boutiqueIds = new Set(
      this.boutiques()
        .map(boutique => boutique.id)
        .filter((id): id is number => !!id),
    );
    return calculs.filter(calcul => calcul.boutique?.id && boutiqueIds.has(calcul.boutique.id));
  }

  private async queryOptionnelle<T>(requete: () => Promise<T>, valeurFallback: T): Promise<T> {
    try {
      return await requete();
    } catch {
      return valeurFallback;
    }
  }
}
