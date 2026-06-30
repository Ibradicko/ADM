import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutMouvementStock } from 'app/entities/enumerations/statut-mouvement-stock.model';
import { TypeMouvementStock } from 'app/entities/enumerations/type-mouvement-stock.model';
import { IUser } from 'app/entities/user/user.model';

export interface IMouvementStock {
  id: number;
  reference?: string | null;
  typeMouvement?: keyof typeof TypeMouvementStock | null;
  statut?: keyof typeof StatutMouvementStock | null;
  dateMouvement?: dayjs.Dayjs | null;
  motif?: string | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewMouvementStock = Omit<IMouvementStock, 'id'> & { id: null };
