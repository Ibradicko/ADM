import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IInventaireStock } from 'app/entities/inventaire-stock/inventaire-stock.model';
import { ILigneInventaireStock } from 'app/entities/ligne-inventaire-stock/ligne-inventaire-stock.model';
import { ILigneReceptionProduit } from 'app/entities/ligne-reception-produit/ligne-reception-produit.model';
import { IMouvementStock } from 'app/entities/mouvement-stock/mouvement-stock.model';
import { IReceptionProduit } from 'app/entities/reception-produit/reception-produit.model';
import { ILigneTransfertStock } from 'app/entities/ligne-transfert-stock/ligne-transfert-stock.model';
import { ITransfertStock } from 'app/entities/transfert-stock/transfert-stock.model';

export interface ScanReceptionPayload {
  produitId?: number | null;
  codeBarres?: string | null;
  quantiteRecue: number;
}

export interface ValidateReceptionPayload {
  depotId: number;
}

export interface ScanInventairePayload {
  produitId?: number | null;
  codeBarres?: string | null;
  quantiteComptee: number;
  commentaire?: string | null;
}

export interface CloseInventairePayload {
  applyAdjustments: boolean;
}

export interface ValidateTransfertPayload {
  depotOrigineId: number;
  depotDestinationId: number;
}

export interface ScanTransfertPayload {
  produitId?: number | null;
  codeBarres?: string | null;
  quantite: number;
}

export interface ReverseMouvementPayload {
  motif?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AdmStockWorkflowService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = inject(ApplicationConfigService).getEndpointFor('api');

  scanReception(receptionId: number, payload: ScanReceptionPayload): Observable<ILigneReceptionProduit> {
    return this.http.post<ILigneReceptionProduit>(`${this.resourceUrl}/reception-produits/${receptionId}/scan`, payload);
  }

  validateReception(receptionId: number, payload: ValidateReceptionPayload): Observable<IReceptionProduit> {
    return this.http.post<IReceptionProduit>(`${this.resourceUrl}/reception-produits/${receptionId}/validate`, payload);
  }

  startInventaire(inventaireId: number): Observable<IInventaireStock> {
    return this.http.post<IInventaireStock>(`${this.resourceUrl}/inventaire-stocks/${inventaireId}/start`, {});
  }

  scanInventaire(inventaireId: number, payload: ScanInventairePayload): Observable<ILigneInventaireStock> {
    return this.http.post<ILigneInventaireStock>(`${this.resourceUrl}/inventaire-stocks/${inventaireId}/scan`, payload);
  }

  closeInventaire(inventaireId: number, payload: CloseInventairePayload): Observable<IInventaireStock> {
    return this.http.post<IInventaireStock>(`${this.resourceUrl}/inventaire-stocks/${inventaireId}/close`, payload);
  }

  validateTransfert(transfertId: number, payload: ValidateTransfertPayload): Observable<ITransfertStock> {
    return this.http.post<ITransfertStock>(`${this.resourceUrl}/transfert-stocks/${transfertId}/validate`, payload);
  }

  scanTransfert(transfertId: number, payload: ScanTransfertPayload): Observable<ILigneTransfertStock> {
    return this.http.post<ILigneTransfertStock>(`${this.resourceUrl}/transfert-stocks/${transfertId}/scan`, payload);
  }

  reverseMouvement(mouvementId: number, payload: ReverseMouvementPayload): Observable<IMouvementStock> {
    return this.http.post<IMouvementStock>(`${this.resourceUrl}/mouvement-stocks/${mouvementId}/reverse`, payload);
  }
}
