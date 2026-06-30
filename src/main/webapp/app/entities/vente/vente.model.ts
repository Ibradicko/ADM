import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutVente } from 'app/entities/enumerations/statut-vente.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { IUser } from 'app/entities/user/user.model';

export interface IVente {
  id: number;
  numeroTicket?: string | null;
  dateHeure?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutVente | null;
  referencePassager?: string | null;
  referenceCarteEmbarquement?: string | null;
  montantBrut?: number | null;
  montantRemise?: number | null;
  montantNet?: number | null;
  commentaire?: string | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  locataire?: Pick<ILocataire, 'id' | 'nom' | 'code'> | null;
  vendeur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewVente = Omit<IVente, 'id'> & { id: null };
