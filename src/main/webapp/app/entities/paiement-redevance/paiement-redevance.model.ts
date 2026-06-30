import dayjs from 'dayjs/esm';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';

export interface IPaiementRedevance {
  id: number;
  reference?: string | null;
  montant?: number | null;
  datePaiement?: dayjs.Dayjs | null;
  modePaiement?: string | null;
  commentaire?: string | null;
  calcul?: Pick<ICalculRedevance, 'id' | 'reference'> | null;
}

export type NewPaiementRedevance = Omit<IPaiementRedevance, 'id'> & { id: null };
