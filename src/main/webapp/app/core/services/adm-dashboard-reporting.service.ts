import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';

export interface DashboardOverviewResponse {
  grossSales?: number | null;
  netSales?: number | null;
  validatedSalesCount?: number | null;
  pendingSalesCount?: number | null;
  stockAlertCount?: number | null;
  unresolvedUnknownScans?: number | null;
  royaltyOutstandingAmount?: number | null;
}

export interface DashboardSalesByDayPointResponse {
  day?: string | null;
  validatedSalesCount?: number | null;
  grossAmount?: number | null;
  netAmount?: number | null;
}

export interface DashboardStockAlertResponse {
  stockProduitId?: number | null;
  produitId?: number | null;
  produitDesignation?: string | null;
  depotId?: number | null;
  depotCode?: string | null;
  boutiqueId?: number | null;
  boutiqueNom?: string | null;
  quantiteTheorique?: number | null;
  stockAlerte?: number | null;
}

export interface DashboardRoyaltyByGroupeArticleResponse {
  groupeArticleId?: number | null;
  groupeArticleCode?: string | null;
  groupeArticleLibelle?: string | null;
  chiffreAffaires?: number | null;
  montantRedevance?: number | null;
  tauxEffectif?: number | null;
  nombreArticlesVendus?: number | null;
}

export interface GenerateRapportExportPayload {
  typeRapport: string;
  format: 'PDF' | 'EXCEL';
  periodeDebut?: string | null;
  periodeFin?: string | null;
  boutiqueId?: number | null;
  locataireId?: number | null;
  depotId?: number | null;
  produitId?: number | null;
  statutVente?: string | null;
  minMontantNet?: number | null;
}

export interface RapportExportLite {
  id: number;
  reference?: string | null;
  typeRapport?: string | null;
  format?: string | null;
  cheminFichier?: string | null;
  dateGeneration?: string | null;
  periodeDebut?: string | null;
  periodeFin?: string | null;
  boutique?: { id?: number | null; nom?: string | null } | null;
  locataire?: { id?: number | null; nom?: string | null } | null;
  utilisateur?: { id?: number | null; login?: string | null } | null;
}

export interface RapportExportPreviewResponse {
  export?: RapportExportLite | null;
  preview?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdmDashboardReportingService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly dashboardUrl = this.applicationConfigService.getEndpointFor('api/dashboard');
  private readonly reportingUrl = this.applicationConfigService.getEndpointFor('api/reporting');

  getOverview(req?: Record<string, unknown>): Observable<DashboardOverviewResponse> {
    const params = createRequestOption(req);
    return this.http.get<DashboardOverviewResponse>(`${this.dashboardUrl}/overview`, { params });
  }

  getSalesByDay(req?: Record<string, unknown>): Observable<DashboardSalesByDayPointResponse[]> {
    const params = createRequestOption(req);
    return this.http.get<DashboardSalesByDayPointResponse[]>(`${this.dashboardUrl}/sales-by-day`, { params });
  }

  getStockAlerts(req?: Record<string, unknown>): Observable<DashboardStockAlertResponse[]> {
    const params = createRequestOption(req);
    return this.http.get<DashboardStockAlertResponse[]>(`${this.dashboardUrl}/stock-alerts`, { params });
  }

  getRoyaltyByGroupeArticle(req?: Record<string, unknown>): Observable<DashboardRoyaltyByGroupeArticleResponse[]> {
    const params = createRequestOption(req);
    return this.http.get<DashboardRoyaltyByGroupeArticleResponse[]>(`${this.dashboardUrl}/redevances-par-groupe-article`, { params });
  }

  generateExport(payload: GenerateRapportExportPayload): Observable<RapportExportPreviewResponse> {
    return this.http.post<RapportExportPreviewResponse>(`${this.reportingUrl}/exports/generate`, payload);
  }

  previewExport(rapportExportId: number): Observable<RapportExportPreviewResponse> {
    return this.http.get<RapportExportPreviewResponse>(`${this.reportingUrl}/exports/${rapportExportId}/preview`);
  }

  downloadExport(rapportExportId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.reportingUrl}/exports/${rapportExportId}/download`, {
      observe: 'response',
      responseType: 'blob',
    });
  }
}
