import dayjs from 'dayjs/esm';

import { Injectable, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { IPaiementRedevance } from 'app/entities/paiement-redevance/paiement-redevance.model';

@Injectable({ providedIn: 'root' })
export class RoyaltyReceiptPrinterService {
  private readonly translateService = inject(TranslateService);

  /** Opens a print-ready receipt window for a royalty payment. Returns false if the popup was blocked. */
  imprimer(paiement: IPaiementRedevance, calcul: ICalculRedevance, paiementsDuCalcul: IPaiementRedevance[]): boolean {
    const fenetre = window.open('', '_blank', 'width=460,height=720');
    if (!fenetre) {
      return false;
    }

    const montantPaye = paiementsDuCalcul.reduce((total, item) => total + (item.montant ?? 0), 0);
    const reste = Math.max(0, (calcul.montantRedevance ?? 0) - montantPaye);

    const t = (key: string, params?: Record<string, unknown>) => `${this.translateService.instant(key, params)}`;
    const formatMontant = (valeur: number | null | undefined) =>
      typeof valeur === 'number' ? `${valeur.toLocaleString('fr-FR')} F CFA` : '--';
    const formatDate = (valeur: dayjs.Dayjs | null | undefined, pattern = 'DD/MM/YYYY') => (valeur ? valeur.format(pattern) : '--');
    const ligne = (label: string, valeur: string, accent = false) => `
      <tr class="${accent ? 'is-accent' : ''}">
        <td class="label">${this.echapperHtml(label)}</td>
        <td class="value">${this.echapperHtml(valeur)}</td>
      </tr>`;

    const contenu = `<!doctype html>
<html lang="fr">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${this.echapperHtml(t('royalties.receipt.title'))} - ${this.echapperHtml(paiement.reference ?? '')}</title>
    <style>
      :root {
        color-scheme: light;
        --text: #111827;
        --muted: #6b7280;
        --line: #d1d5db;
        --soft: #f9fafb;
        --primary: #1d4ed8;
        --success: #047857;
      }

      * { box-sizing: border-box; }

      body {
        margin: 0;
        padding: 18px;
        color: var(--text);
        background: #eef2f7;
        font-family: Arial, Helvetica, sans-serif;
      }

      .receipt {
        width: 100%;
        max-width: 420px;
        margin: 0 auto;
        overflow: hidden;
        border: 1px solid rgba(17, 24, 39, 0.1);
        border-radius: 18px;
        background: #fff;
        box-shadow: 0 18px 50px rgba(15, 23, 42, 0.16);
      }

      .header {
        position: relative;
        overflow: hidden;
        padding: 18px;
        color: #fff;
        background: linear-gradient(135deg, #0f172a, #1d4ed8);
      }

      .header::after {
        position: absolute;
        right: -42px;
        bottom: -52px;
        width: 130px;
        height: 130px;
        content: '';
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.1);
      }

      h1 {
        position: relative;
        z-index: 1;
        margin: 0;
        font-size: 18px;
        line-height: 1.2;
        letter-spacing: -0.02em;
        text-align: center;
      }

      .subtitle {
        position: relative;
        z-index: 1;
        margin: 6px 0 0;
        color: rgba(255, 255, 255, 0.78);
        font-size: 12px;
        text-align: center;
      }

      .body { padding: 16px; }

      .amount-card {
        margin-bottom: 14px;
        padding: 14px;
        border: 1px solid rgba(4, 120, 87, 0.18);
        border-radius: 14px;
        background: linear-gradient(135deg, #ecfdf5, #ffffff);
        text-align: center;
      }

      .amount-label {
        display: block;
        color: var(--muted);
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 0.05em;
        text-transform: uppercase;
      }

      .amount {
        display: block;
        margin-top: 4px;
        color: var(--success);
        font-size: 22px;
        font-weight: 800;
        letter-spacing: -0.04em;
      }

      table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
      }

      td {
        padding: 7px 0;
        vertical-align: top;
        border-bottom: 1px dashed var(--line);
      }

      tr:last-child td { border-bottom: 0; }
      td.label { width: 52%; color: var(--muted); }
      td.value { text-align: right; font-weight: 700; }
      tr.is-accent td.value { color: var(--primary); }

      .section {
        margin-top: 14px;
        padding: 12px;
        border: 1px solid rgba(209, 213, 219, 0.85);
        border-radius: 14px;
        background: var(--soft);
      }

      .section-title {
        margin: 0 0 8px;
        color: #374151;
        font-size: 11px;
        font-weight: 800;
        letter-spacing: 0.06em;
        text-transform: uppercase;
      }

      .footer {
        padding: 12px 16px 16px;
        color: var(--muted);
        font-size: 11px;
        text-align: center;
      }

      @media print {
        body { padding: 0; background: #fff; }
        .receipt { max-width: none; border: 0; border-radius: 0; box-shadow: none; }
      }
    </style>
  </head>
  <body>
    <main class="receipt">
      <header class="header">
        <h1>${this.echapperHtml(t('royalties.receipt.title'))}</h1>
        <p class="subtitle">${this.echapperHtml(t('royalties.receipt.subtitle'))}</p>
      </header>

      <section class="body">
        <div class="amount-card">
          <span class="amount-label">${this.echapperHtml(t('royalties.fields.amount'))}</span>
          <span class="amount">${this.echapperHtml(formatMontant(paiement.montant))}</span>
        </div>

        <div class="section">
          <p class="section-title">${this.echapperHtml(t('royalties.receipt.paymentReference'))}</p>
          <table>
            ${ligne(t('royalties.receipt.paymentReference'), paiement.reference ?? '--', true)}
            ${ligne(t('royalties.fields.date'), formatDate(paiement.datePaiement))}
            ${ligne(t('royalties.fields.paymentMethod'), paiement.modePaiement ?? '--')}
          </table>
        </div>

        <div class="section">
          <p class="section-title">${this.echapperHtml(t('royalties.receipt.calculationReference'))}</p>
          <table>
            ${ligne(t('royalties.fields.shop'), calcul.boutique?.nom ?? '--')}
            ${ligne(t('royalties.fields.tenant'), calcul.locataire?.nom ?? '--')}
            ${ligne(t('royalties.receipt.calculationReference'), calcul.reference ?? '--', true)}
            ${ligne(t('royalties.fields.period'), `${formatDate(calcul.periodeDebut)} - ${formatDate(calcul.periodeFin)}`)}
          </table>
        </div>

        <div class="section">
          <p class="section-title">${this.echapperHtml(t('royalties.payment.remaining'))}</p>
          <table>
            ${ligne(t('royalties.fields.royaltyAmount'), formatMontant(calcul.montantRedevance))}
            ${ligne(t('royalties.payment.alreadyPaid'), formatMontant(montantPaye))}
            ${ligne(t('royalties.payment.remaining'), formatMontant(reste), true)}
          </table>
        </div>
      </section>

      <footer class="footer">${this.echapperHtml(t('royalties.receipt.printedOn', { date: dayjs().format('DD/MM/YYYY HH:mm') }))}</footer>
    </main>
  </body>
</html>`;

    fenetre.document.open();
    fenetre.document.write(contenu);
    fenetre.document.close();
    fenetre.focus();
    setTimeout(() => fenetre.print(), 250);
    return true;
  }

  private echapperHtml(valeur: string): string {
    return valeur
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#039;');
  }
}
