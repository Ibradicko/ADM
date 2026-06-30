import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { StatutInventaire } from 'app/entities/enumerations/statut-inventaire.model';
import { TypeInventaire } from 'app/entities/enumerations/type-inventaire.model';
import { IUser } from 'app/entities/user/user.model';

export interface IInventaireStock {
  id: number;
  reference?: string | null;
  typeInventaire?: keyof typeof TypeInventaire | null;
  statut?: keyof typeof StatutInventaire | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  depot?: Pick<IDepotStock, 'id' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewInventaireStock = Omit<IInventaireStock, 'id'> & { id: null };
