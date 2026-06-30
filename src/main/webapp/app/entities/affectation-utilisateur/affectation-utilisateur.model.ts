import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAffectationUtilisateur {
  id: number;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  actif?: boolean | null;
  user?: Pick<IUser, 'id' | 'login' | 'firstName' | 'lastName' | 'email'> | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  profil?: Pick<IProfilMetier, 'id' | 'code'> | null;
}

export type NewAffectationUtilisateur = Omit<IAffectationUtilisateur, 'id'> & { id: null };
