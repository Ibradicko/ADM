import dayjs from 'dayjs/esm';

import { IVente } from 'app/entities/vente/vente.model';

export interface ITicketCaisse {
  id: number;
  numero?: string | null;
  dateEmission?: dayjs.Dayjs | null;
  nombreImpressions?: number | null;
  contenu?: string | null;
  vente?: Pick<IVente, 'id' | 'numeroTicket'> | null;
}

export type NewTicketCaisse = Omit<ITicketCaisse, 'id'> & { id: null };
