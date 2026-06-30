import { IProfilMetier } from 'app/entities/profil-metier/profil-metier.model';

export interface IPermissionMetier {
  id: number;
  code?: string | null;
  libelle?: string | null;
  module?: string | null;
  description?: string | null;
  profilses?: Pick<IProfilMetier, 'id' | 'code'>[] | null;
}

export type NewPermissionMetier = Omit<IPermissionMetier, 'id'> & { id: null };
