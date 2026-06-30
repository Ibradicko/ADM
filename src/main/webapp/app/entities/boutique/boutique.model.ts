import dayjs from 'dayjs/esm';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypeBoutique } from 'app/entities/enumerations/type-boutique.model';

export interface IBoutique {
  id: number;
  code?: string | null;
  nom?: string | null;
  type?: keyof typeof TypeBoutique | null;
  emplacement?: string | null;
  telephone?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  dateCreation?: dayjs.Dayjs | null;
}

export type NewBoutique = Omit<IBoutique, 'id'> & { id: null };
