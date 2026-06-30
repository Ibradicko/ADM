import dayjs from 'dayjs/esm';

import { IProduit } from 'app/entities/produit/produit.model';
import { IUser } from 'app/entities/user/user.model';

export interface IHistoriqueCodeBarres {
  id: number;
  ancienCode?: string | null;
  nouveauCode?: string | null;
  motif?: string | null;
  dateChangement?: dayjs.Dayjs | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewHistoriqueCodeBarres = Omit<IHistoriqueCodeBarres, 'id'> & { id: null };
