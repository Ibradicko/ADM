import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';
import JsBarcode from 'jsbarcode';
import { firstValueFrom } from 'rxjs';

import { UiPermissionService } from 'app/core/services/ui-permission.service';
import { ICodeBarresProduit, NewCodeBarresProduit } from 'app/entities/code-barres-produit/code-barres-produit.model';
import { CodeBarresProduitService } from 'app/entities/code-barres-produit/service/code-barres-produit.service';
import { IBoutique } from 'app/entities/boutique/boutique.model';
import { IEtiquetteProduit, NewEtiquetteProduit } from 'app/entities/etiquette-produit/etiquette-produit.model';
import { EtiquetteProduitService } from 'app/entities/etiquette-produit/service/etiquette-produit.service';
import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';
import { IHistoriqueCodeBarres } from 'app/entities/historique-code-barres/historique-code-barres.model';
import { HistoriqueCodeBarresService } from 'app/entities/historique-code-barres/service/historique-code-barres.service';
import { ILotEtiquettes, NewLotEtiquettes } from 'app/entities/lot-etiquettes/lot-etiquettes.model';
import { LotEtiquettesService } from 'app/entities/lot-etiquettes/service/lot-etiquettes.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IScanInconnu } from 'app/entities/scan-inconnu/scan-inconnu.model';
import { ScanInconnuService } from 'app/entities/scan-inconnu/service/scan-inconnu.service';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';

interface MessageCatalogue {
  type: 'success' | 'danger' | 'info';
  key: string;
}

interface LotEtiquetteSynthese {
  lot: ILotEtiquettes;
  etiquettes: IEtiquetteProduit[];
  produitPrincipal: string;
  quantiteTotale: number;
  imprime: boolean;
  dateImpression?: dayjs.Dayjs | null;
}

type OngletCatalogue = 'recherche' | 'codes' | 'scans' | 'etiquettes' | 'historique';

@Component({
  selector: 'jhi-catalogue-identification',
  templateUrl: './catalogue-identification.html',
  imports: [FormsModule, RouterLink, FontAwesomeModule, FormatMediumDatetimePipe, TranslateDirective, TranslateModule],
})
export default class CatalogueIdentificationComponent implements OnInit {
  readonly permissionsUi = inject(UiPermissionService);

  readonly chargement = signal(false);
  readonly enregistrement = signal(false);
  readonly message = signal<MessageCatalogue | null>(null);
  readonly onglet = signal<OngletCatalogue>('recherche');

  readonly produits = signal<IProduit[]>([]);
  readonly codesBarres = signal<ICodeBarresProduit[]>([]);
  readonly scansInconnus = signal<IScanInconnu[]>([]);
  readonly historiques = signal<IHistoriqueCodeBarres[]>([]);
  readonly lotsEtiquettes = signal<ILotEtiquettes[]>([]);
  readonly etiquettesProduits = signal<IEtiquetteProduit[]>([]);

  readonly rechercheGlobale = signal('');
  readonly rechercheCodeBarres = signal('');
  readonly filtreBoutique = signal('ALL');
  readonly filtreTypeCode = signal<'ALL' | keyof typeof TypeCodeBarres>('ALL');
  readonly filtreResolution = signal<'ALL' | 'OPEN' | 'DONE'>('ALL');

  readonly typesCodeBarres = Object.values(TypeCodeBarres);

  readonly formulaireCodeBarres = {
    produitId: null as number | null,
    code: '',
    type: TypeCodeBarres.INTERNE as keyof typeof TypeCodeBarres,
    principal: true,
    actif: true,
    genereParSysteme: true,
  };

  readonly formulaireEtiquettes = {
    produitId: null as number | null,
    reference: '',
    formatImpression: 'A6',
    quantite: 1,
  };

  readonly resolutionProduitParScan: Record<number, number | null> = {};

  readonly boutiquesDisponibles = computed<Pick<IBoutique, 'id' | 'nom'>[]>(() =>
    [
      ...new Map(
        this.produits()
          .filter(produit => produit.boutique?.id)
          .map(produit => [produit.boutique!.id, { id: produit.boutique!.id, nom: produit.boutique!.nom ?? '' }]),
      ).values(),
    ].sort((a, b) => (a.nom ?? '').localeCompare(b.nom ?? '')),
  );

  readonly produitsTries = computed(() => [...this.produits()].sort((a, b) => (a.designation ?? '').localeCompare(b.designation ?? '')));

  readonly resultatsRechercheCodeBarres = computed(() => {
    const texte = this.rechercheCodeBarres().trim().toLowerCase();
    const filtreBoutique = this.filtreBoutique();
    if (!texte) {
      return [];
    }

    return [...this.codesBarres()]
      .filter(codeBarres => {
        const matchesBoutique =
          filtreBoutique === 'ALL' || String(this.produitParId(codeBarres.produit?.id)?.boutique?.id ?? '') === filtreBoutique;
        const contenu = [codeBarres.code, codeBarres.produit?.designation, this.produitParId(codeBarres.produit?.id)?.codeInterne]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
        return matchesBoutique && contenu.includes(texte);
      })
      .sort((a, b) => {
        const aExact = (a.code ?? '').toLowerCase() === texte ? 1 : 0;
        const bExact = (b.code ?? '').toLowerCase() === texte ? 1 : 0;
        return bExact - aExact;
      })
      .slice(0, 12);
  });

  readonly scansCorrespondants = computed(() => {
    const texte = this.rechercheCodeBarres().trim().toLowerCase();
    if (!texte) {
      return [];
    }
    return this.scansInconnus()
      .filter(scan => (scan.codeScanne ?? '').toLowerCase().includes(texte))
      .slice(0, 10);
  });

  readonly codesBarresFiltres = computed(() => {
    const texte = this.rechercheGlobale().trim().toLowerCase();
    const boutique = this.filtreBoutique();
    const type = this.filtreTypeCode();

    return this.codesBarres().filter(codeBarres => {
      const produit = this.produitParId(codeBarres.produit?.id);
      const matchesTexte =
        !texte ||
        [codeBarres.code, codeBarres.type, produit?.designation, produit?.codeInterne, produit?.boutique?.nom]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()
          .includes(texte);
      const matchesBoutique = boutique === 'ALL' || String(produit?.boutique?.id ?? '') === boutique;
      const matchesType = type === 'ALL' || codeBarres.type === type;
      return matchesTexte && matchesBoutique && matchesType;
    });
  });

  readonly scansFiltres = computed(() => {
    const texte = this.rechercheGlobale().trim().toLowerCase();
    const boutique = this.filtreBoutique();
    const resolution = this.filtreResolution();

    return this.scansInconnus().filter(scan => {
      const matchesTexte =
        !texte ||
        [scan.codeScanne, scan.ecranOrigine, scan.commentaire, scan.boutique?.nom, scan.produitAffecte?.designation]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()
          .includes(texte);
      const matchesBoutique = boutique === 'ALL' || String(scan.boutique?.id ?? '') === boutique;
      const matchesResolution = resolution === 'ALL' || (resolution === 'DONE' && !!scan.resolu) || (resolution === 'OPEN' && !scan.resolu);
      return matchesTexte && matchesBoutique && matchesResolution;
    });
  });

  readonly historiquesFiltres = computed(() => {
    const texte = this.rechercheGlobale().trim().toLowerCase();
    return this.historiques().filter(historique => {
      if (!texte) {
        return true;
      }
      return [
        historique.ancienCode,
        historique.nouveauCode,
        historique.motif,
        historique.produit?.designation,
        historique.utilisateur?.login,
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(texte);
    });
  });

  readonly lotsSynthese = computed<LotEtiquetteSynthese[]>(() =>
    this.lotsEtiquettes()
      .map(lot => {
        const etiquettes = this.etiquettesProduits().filter(etiquette => etiquette.lot?.id === lot.id);
        const produitPrincipal =
          [...new Set(etiquettes.map(etiquette => etiquette.produit?.designation).filter((valeur): valeur is string => !!valeur))].join(
            ', ',
          ) || '';
        const quantiteTotale = etiquettes.reduce((total, etiquette) => total + (etiquette.quantite ?? 0), 0);
        const imprime = etiquettes.length > 0 && etiquettes.every(etiquette => !!etiquette.imprimee);
        const dateImpression = etiquettes
          .map(etiquette => etiquette.dateImpression)
          .filter((valeur): valeur is dayjs.Dayjs => !!valeur)
          .sort((a, b) => b.valueOf() - a.valueOf())[0];

        return {
          lot,
          etiquettes,
          produitPrincipal,
          quantiteTotale,
          imprime,
          dateImpression,
        };
      })
      .filter(lot => {
        const texte = this.rechercheGlobale().trim().toLowerCase();
        const boutique = this.filtreBoutique();
        const produitLie = lot.etiquettes[0]?.produit ? this.produitParId(lot.etiquettes[0].produit?.id) : undefined;
        const matchesTexte =
          !texte ||
          [lot.lot.reference, lot.lot.formatImpression, lot.produitPrincipal].filter(Boolean).join(' ').toLowerCase().includes(texte);
        const matchesBoutique = boutique === 'ALL' || String(produitLie?.boutique?.id ?? '') === boutique;
        return matchesTexte && matchesBoutique;
      }),
  );

  readonly totalCodesActifs = computed(() => this.codesBarres().filter(codeBarres => !!codeBarres.actif).length);
  readonly totalScansOuverts = computed(() => this.scansInconnus().filter(scan => !scan.resolu).length);
  readonly totalLotsImpression = computed(() => this.lotsSynthese().length);
  readonly totalProduitsIdentifies = computed(
    () =>
      new Set(
        this.codesBarres()
          .map(codeBarres => codeBarres.produit?.id)
          .filter((valeur): valeur is number => typeof valeur === 'number'),
      ).size,
  );

  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly produitService = inject(ProduitService);
  private readonly codeBarresProduitService = inject(CodeBarresProduitService);
  private readonly scanInconnuService = inject(ScanInconnuService);
  private readonly historiqueCodeBarresService = inject(HistoriqueCodeBarresService);
  private readonly lotEtiquettesService = inject(LotEtiquettesService);
  private readonly etiquetteProduitService = inject(EtiquetteProduitService);

  ngOnInit(): void {
    const onglet = this.activatedRoute.snapshot.queryParamMap.get('onglet');
    if (onglet === 'codes' || onglet === 'recherche' || onglet === 'scans' || onglet === 'etiquettes' || onglet === 'historique') {
      this.onglet.set(onglet);
    }

    const produitId = Number(this.activatedRoute.snapshot.queryParamMap.get('produitId'));
    if (Number.isFinite(produitId) && produitId > 0) {
      this.formulaireCodeBarres.produitId = produitId;
      this.formulaireEtiquettes.produitId = produitId;
    }

    void this.recharger();
  }

  produitParId(produitId: number | null | undefined): IProduit | undefined {
    if (!produitId) {
      return undefined;
    }
    return this.produits().find(produit => produit.id === produitId);
  }

  imageProduitSrc(produit: IProduit | null | undefined): string | null {
    return produit?.image && produit.imageContentType
      ? `data:${produit.imageContentType};base64,${produit.image}`
      : 'content/images/default-article.svg';
  }

  reinitialiserFiltres(): void {
    this.rechercheGlobale.set('');
    this.rechercheCodeBarres.set('');
    this.filtreBoutique.set('ALL');
    this.filtreTypeCode.set('ALL');
    this.filtreResolution.set('ALL');
  }

  selectionnerProduit(produitId: number): void {
    this.formulaireCodeBarres.produitId = produitId;
    this.formulaireEtiquettes.produitId = produitId;
  }

  genererCodeAutomatique(): void {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    if (!this.formulaireCodeBarres.produitId) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.selectProductForBarcode',
      });
      return;
    }

    const produit = this.produitParId(this.formulaireCodeBarres.produitId);
    const prefixe =
      (produit?.codeInterne ?? 'ADM')
        .replace(/[^A-Za-z0-9]/g, '')
        .toUpperCase()
        .slice(0, 6) || 'ADM';
    const suffixe = `${dayjs().format('YYMMDDHHmmss')}${Math.floor(10 + Math.random() * 90)}`;
    this.formulaireCodeBarres.code = `${prefixe}${suffixe}`.slice(0, 30);
    this.formulaireCodeBarres.type = TypeCodeBarres.INTERNE;
    this.formulaireCodeBarres.genereParSysteme = true;
  }

  async recharger(): Promise<void> {
    this.chargement.set(true);
    this.message.set(null);

    try {
      const [produitsResponse, codesResponse, scansResponse, historiquesResponse, lotsResponse, etiquettesResponse] = await Promise.all([
        firstValueFrom(this.produitService.query({ size: 500, eagerload: true, sort: ['designation,asc'] })),
        firstValueFrom(this.codeBarresProduitService.query({ size: 500, eagerload: true, sort: ['dateAffectation,desc'] })),
        firstValueFrom(this.scanInconnuService.query({ size: 500, eagerload: true, sort: ['dateScan,desc'] })),
        firstValueFrom(this.historiqueCodeBarresService.query({ size: 500, eagerload: true, sort: ['dateChangement,desc'] })),
        firstValueFrom(this.lotEtiquettesService.query({ size: 500, sort: ['dateGeneration,desc'] })),
        firstValueFrom(this.etiquetteProduitService.query({ size: 500, eagerload: true, sort: ['id,desc'] })),
      ]);

      this.produits.set(produitsResponse.body ?? []);
      this.codesBarres.set(codesResponse.body ?? []);
      this.scansInconnus.set(scansResponse.body ?? []);
      this.historiques.set(historiquesResponse.body ?? []);
      this.lotsEtiquettes.set(lotsResponse.body ?? []);
      this.etiquettesProduits.set(etiquettesResponse.body ?? []);
    } catch {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.loadFailed',
      });
    } finally {
      this.chargement.set(false);
    }
  }

  async enregistrerCodeBarres(): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    if (!this.formulaireCodeBarres.produitId || !this.formulaireCodeBarres.code.trim()) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.productAndBarcodeRequired',
      });
      return;
    }

    const produit = this.produitParId(this.formulaireCodeBarres.produitId);
    if (!produit) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.productNotFound',
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      const payload: NewCodeBarresProduit = {
        id: null,
        code: this.formulaireCodeBarres.code.trim(),
        type: this.formulaireCodeBarres.type,
        principal: this.formulaireCodeBarres.principal,
        actif: this.formulaireCodeBarres.actif,
        genereParSysteme: this.formulaireCodeBarres.genereParSysteme,
        dateAffectation: dayjs(),
        produit: { id: produit.id, designation: produit.designation ?? undefined },
      };
      await firstValueFrom(this.codeBarresProduitService.create(payload));
      this.formulaireCodeBarres.code = '';
      await this.recharger();
      this.onglet.set('codes');
      this.message.set({
        type: 'success',
        key: 'catalogueIdentification.messages.barcodeAdded',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.barcodeCreationFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async resoudreScan(scan: IScanInconnu): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    const produitId = this.resolutionProduitParScan[scan.id];
    if (!produitId) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.selectProductForUnknownScan',
      });
      return;
    }

    const produit = this.produitParId(produitId);
    if (!produit) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.resolutionProductNotFound',
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      await firstValueFrom(
        this.scanInconnuService.partialUpdate({
          id: scan.id,
          resolu: true,
          produitAffecte: { id: produit.id, designation: produit.designation ?? undefined },
        }),
      );
      await this.recharger();
      this.onglet.set('scans');
      this.message.set({
        type: 'success',
        key: 'catalogueIdentification.messages.unknownScanResolved',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.unknownScanResolutionFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async creerLotEtiquettes(): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    if (!this.formulaireEtiquettes.produitId || this.formulaireEtiquettes.quantite < 1) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.productAndLabelQuantityRequired',
      });
      return;
    }

    const produit = this.produitParId(this.formulaireEtiquettes.produitId);
    if (!produit) {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.productNotFound',
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      const nouveauLot: NewLotEtiquettes = {
        id: null,
        reference: this.formulaireEtiquettes.reference.trim() || `LOT-${produit.id}-${dayjs().format('YYYYMMDDHHmmss')}`,
        dateGeneration: dayjs(),
        formatImpression: this.formulaireEtiquettes.formatImpression.trim() || 'A6',
        nombreEtiquettes: this.formulaireEtiquettes.quantite,
      };

      const lot = await firstValueFrom(this.lotEtiquettesService.create(nouveauLot));

      const nouvelleEtiquette: NewEtiquetteProduit = {
        id: null,
        quantite: this.formulaireEtiquettes.quantite,
        imprimee: false,
        dateImpression: null,
        produit: { id: produit.id, designation: produit.designation ?? undefined },
        lot: { id: lot.id, reference: lot.reference ?? undefined },
      };

      await firstValueFrom(this.etiquetteProduitService.create(nouvelleEtiquette));
      this.formulaireEtiquettes.reference = '';
      this.formulaireEtiquettes.quantite = 1;
      await this.recharger();
      this.onglet.set('etiquettes');
      this.message.set({
        type: 'success',
        key: 'catalogueIdentification.messages.labelBatchPrepared',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.labelBatchPreparationFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async marquerLotImprime(lotSynthese: LotEtiquetteSynthese): Promise<void> {
    if (!this.permissionsUi.peutGererArticlesBoutique()) {
      return;
    }
    if (lotSynthese.etiquettes.length === 0) {
      this.message.set({
        type: 'info',
        key: 'catalogueIdentification.messages.noLabelsInBatch',
      });
      return;
    }

    this.enregistrement.set(true);
    try {
      await Promise.all(
        lotSynthese.etiquettes.map(etiquette =>
          firstValueFrom(
            this.etiquetteProduitService.partialUpdate({
              id: etiquette.id,
              imprimee: true,
              dateImpression: dayjs(),
            }),
          ),
        ),
      );
      await this.recharger();
      this.message.set({
        type: 'success',
        key: 'catalogueIdentification.messages.labelBatchPrinted',
      });
    } catch {
      this.message.set({
        type: 'danger',
        key: 'catalogueIdentification.messages.printUpdateFailed',
      });
    } finally {
      this.enregistrement.set(false);
    }
  }

  async imprimerLotEtiquettes(lotSynthese: LotEtiquetteSynthese): Promise<void> {
    if (lotSynthese.etiquettes.length === 0) {
      this.message.set({ type: 'info', key: 'catalogueIdentification.messages.noLabelsInBatch' });
      return;
    }

    const fenetreImpression = window.open('', '_blank', 'width=920,height=720');
    if (!fenetreImpression) {
      this.message.set({ type: 'danger', key: 'catalogueIdentification.messages.printWindowFailed' });
      return;
    }

    const etiquettesHtml = lotSynthese.etiquettes
      .flatMap(etiquette => {
        const produitComplet = this.produitParId(etiquette.produit?.id);
        const produit = produitComplet ?? etiquette.produit;
        const quantite = Math.max(1, Number(etiquette.quantite ?? 1));
        const codePrincipal = this.codePrincipalProduit(produit?.id);
        const code = codePrincipal?.code ?? produitComplet?.codeInterne ?? '';
        const codeBarresSvg = this.genererCodeBarresSvg(code, codePrincipal?.type);

        return Array.from(
          { length: quantite },
          () => `<section class="label">
  <div class="label__shop">${this.echapperHtml(produitComplet?.boutique?.nom ?? '')}</div>
  <div class="label__name">${this.echapperHtml(produit?.designation ?? 'Article')}</div>
  <div class="label__price">${this.echapperHtml(this.formatMontant(produitComplet?.prixVente))}</div>
  <div class="barcode">${codeBarresSvg}</div>
  <div class="label__code">${this.echapperHtml(code || '--')}</div>
</section>`,
        );
      })
      .join('');

    const titre = this.echapperHtml(lotSynthese.lot.reference ?? 'Etiquettes');
    fenetreImpression.document.open();
    fenetreImpression.document.write(`<!doctype html>
<html lang="fr">
  <head>
    <meta charset="utf-8">
    <title>${titre}</title>
    <style>
      @page { size: A4; margin: 9mm; }
      * { box-sizing: border-box; }
      body { margin: 0; color: #111827; background: #fff; font-family: Arial, sans-serif; }
      .sheet { display: grid; grid-template-columns: repeat(3, 1fr); gap: 6mm; }
      .label {
        min-height: 39mm;
        padding: 4mm;
        border: 1px solid #111827;
        break-inside: avoid;
        display: grid;
        align-content: space-between;
        gap: 1.5mm;
      }
      .label__shop { font-size: 8px; font-weight: 700; text-transform: uppercase; }
      .label__name { min-height: 18px; font-size: 11px; font-weight: 700; line-height: 1.15; }
      .label__price { font-size: 13px; font-weight: 800; }
      .barcode {
        display: flex;
        justify-content: center;
        min-height: 42px;
        overflow: hidden;
      }
      .barcode svg { display: block; width: 100%; max-width: 48mm; height: 42px; }
      .barcode__fallback { font-family: "Courier New", monospace; font-size: 10px; overflow-wrap: anywhere; }
      .label__code { font-family: "Courier New", monospace; font-size: 8px; text-align: center; overflow-wrap: anywhere; }
      @media screen {
        body { padding: 18px; background: #f8fafc; }
        .sheet {
          max-width: 210mm;
          margin: 0 auto;
          padding: 9mm;
          background: #fff;
          box-shadow: 0 16px 36px rgba(15, 23, 42, 0.16);
        }
      }
    </style>
  </head>
  <body>
    <main class="sheet">${etiquettesHtml}</main>
    <script>
      window.addEventListener('load', () => {
        window.focus();
        window.print();
      });
    </script>
  </body>
</html>`);
    fenetreImpression.document.close();
    this.message.set({ type: 'success', key: 'catalogueIdentification.messages.labelPrintOpened' });
  }

  codePrincipalProduit(produitId: number | null | undefined): ICodeBarresProduit | undefined {
    if (!produitId) {
      return undefined;
    }

    return (
      this.codesBarres().find(code => code.produit?.id === produitId && code.actif && code.principal) ??
      this.codesBarres().find(code => code.produit?.id === produitId && code.actif)
    );
  }

  libelleCodeArticle(produitId: number | null | undefined): string {
    return this.codePrincipalProduit(produitId)?.code ?? '';
  }

  formatMontant(valeur: number | null | undefined): string {
    return typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
  }

  private genererCodeBarresSvg(code: string | null | undefined, type: keyof typeof TypeCodeBarres | null | undefined): string {
    const valeur = code?.trim();
    if (!valeur) {
      return '<span class="barcode__fallback">--</span>';
    }

    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    const format = this.formatJsBarcode(type, valeur);

    try {
      JsBarcode(svg, valeur, {
        format,
        displayValue: false,
        height: 38,
        margin: 0,
        width: format === 'CODE128' ? 1.25 : 1.5,
      });
      return svg.outerHTML;
    } catch {
      return `<span class="barcode__fallback">${this.echapperHtml(valeur)}</span>`;
    }
  }

  private formatJsBarcode(type: keyof typeof TypeCodeBarres | null | undefined, code: string): 'EAN13' | 'EAN8' | 'CODE128' {
    if (type === TypeCodeBarres.EAN13 && /^\d{13}$/.test(code)) {
      return 'EAN13';
    }

    if (type === TypeCodeBarres.EAN8 && /^\d{8}$/.test(code)) {
      return 'EAN8';
    }

    return 'CODE128';
  }

  private echapperHtml(valeur: string): string {
    return valeur
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }
}
