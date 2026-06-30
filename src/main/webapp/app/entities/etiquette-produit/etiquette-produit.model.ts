import dayjs from 'dayjs/esm';

import { ILotEtiquettes } from 'app/entities/lot-etiquettes/lot-etiquettes.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface IEtiquetteProduit {
  id: number;
  quantite?: number | null;
  imprimee?: boolean | null;
  dateImpression?: dayjs.Dayjs | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
  lot?: Pick<ILotEtiquettes, 'id' | 'reference'> | null;
}

export type NewEtiquetteProduit = Omit<IEtiquetteProduit, 'id'> & { id: null };
