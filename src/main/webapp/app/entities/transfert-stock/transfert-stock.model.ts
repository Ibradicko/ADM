import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutMouvementStock } from 'app/entities/enumerations/statut-mouvement-stock.model';
import { IUser } from 'app/entities/user/user.model';

export interface ITransfertStock {
  id: number;
  reference?: string | null;
  dateTransfert?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutMouvementStock | null;
  motif?: string | null;
  boutiqueOrigine?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  boutiqueDestination?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTransfertStock = Omit<ITransfertStock, 'id'> & { id: null };
