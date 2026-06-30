import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { IUser } from 'app/entities/user/user.model';

export interface IReceptionProduit {
  id: number;
  reference?: string | null;
  dateReception?: dayjs.Dayjs | null;
  fournisseur?: string | null;
  commentaire?: string | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewReceptionProduit = Omit<IReceptionProduit, 'id'> & { id: null };
