import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutRedevance } from 'app/entities/enumerations/statut-redevance.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';

export interface ICalculRedevance {
  id: number;
  reference?: string | null;
  periodeDebut?: dayjs.Dayjs | null;
  periodeFin?: dayjs.Dayjs | null;
  chiffreAffaires?: number | null;
  montantRedevance?: number | null;
  statut?: keyof typeof StatutRedevance | null;
  dateCalcul?: dayjs.Dayjs | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  locataire?: Pick<ILocataire, 'id' | 'nom' | 'code'> | null;
}

export type NewCalculRedevance = Omit<ICalculRedevance, 'id'> & { id: null };
