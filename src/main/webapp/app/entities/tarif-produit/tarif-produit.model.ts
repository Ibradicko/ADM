import dayjs from 'dayjs/esm';

import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface ITarifProduit {
  id: number;
  montant?: number | null;
  typePrix?: keyof typeof TypePrix | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  actif?: boolean | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewTarifProduit = Omit<ITarifProduit, 'id'> & { id: null };
