import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { TypeActionAudit } from 'app/entities/enumerations/type-action-audit.model';
import { IUser } from 'app/entities/user/user.model';

export interface IJournalAudit {
  id: number;
  typeAction?: keyof typeof TypeActionAudit | null;
  entiteConcernee?: string | null;
  identifiantEntite?: string | null;
  description?: string | null;
  adresseIp?: string | null;
  dateAction?: dayjs.Dayjs | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  utilisateur?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewJournalAudit = Omit<IJournalAudit, 'id'> & { id: null };
