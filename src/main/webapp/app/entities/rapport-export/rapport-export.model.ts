import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { FormatExport } from 'app/entities/enumerations/format-export.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { IUser } from 'app/entities/user/user.model';

export interface IRapportExport {
  id: number;
  reference?: string | null;
  typeRapport?: string | null;
  format?: keyof typeof FormatExport | null;
  periodeDebut?: dayjs.Dayjs | null;
  periodeFin?: dayjs.Dayjs | null;
  cheminFichier?: string | null;
  dateGeneration?: dayjs.Dayjs | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  locataire?: Pick<ILocataire, 'id' | 'nom' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewRapportExport = Omit<IRapportExport, 'id'> & { id: null };
