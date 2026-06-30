import dayjs from 'dayjs/esm';

import { StatutPaiement } from 'app/entities/enumerations/statut-paiement.model';
import { IModePaiementRef } from 'app/entities/mode-paiement-ref/mode-paiement-ref.model';
import { IVente } from 'app/entities/vente/vente.model';

export interface IPaiementVente {
  id: number;
  montant?: number | null;
  statut?: keyof typeof StatutPaiement | null;
  referencePaiement?: string | null;
  datePaiement?: dayjs.Dayjs | null;
  vente?: Pick<IVente, 'id' | 'numeroTicket'> | null;
  modePaiement?: Pick<IModePaiementRef, 'id' | 'libelle'> | null;
}

export type NewPaiementVente = Omit<IPaiementVente, 'id'> & { id: null };
