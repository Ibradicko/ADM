import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IPermissionMetier } from 'app/entities/permission-metier/permission-metier.model';

export interface IProfilMetier {
  id: number;
  code?: string | null;
  libelle?: string | null;
  description?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  permissionses?: Pick<IPermissionMetier, 'id' | 'code'>[] | null;
}

export type NewProfilMetier = Omit<IProfilMetier, 'id'> & { id: null };
