import dayjs from 'dayjs/esm';

import { TypeOperationCorrective } from 'app/entities/enumerations/type-operation-corrective.model';
import { IUser } from 'app/entities/user/user.model';
import { IVente } from 'app/entities/vente/vente.model';

export interface IOperationCorrectiveVente {
  id: number;
  typeOperation?: keyof typeof TypeOperationCorrective | null;
  motif?: string | null;
  montantImpact?: number | null;
  dateOperation?: dayjs.Dayjs | null;
  vente?: Pick<IVente, 'id' | 'numeroTicket'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewOperationCorrectiveVente = Omit<IOperationCorrectiveVente, 'id'> & { id: null };
