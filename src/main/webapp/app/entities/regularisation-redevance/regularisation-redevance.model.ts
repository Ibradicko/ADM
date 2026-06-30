import dayjs from 'dayjs/esm';

import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';

export interface IRegularisationRedevance {
  id: number;
  reference?: string | null;
  montant?: number | null;
  motif?: string | null;
  dateRegularisation?: dayjs.Dayjs | null;
  calcul?: Pick<ICalculRedevance, 'id' | 'reference'> | null;
}

export type NewRegularisationRedevance = Omit<IRegularisationRedevance, 'id'> & { id: null };
