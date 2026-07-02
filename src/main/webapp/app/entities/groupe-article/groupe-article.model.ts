import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';

export interface IGroupeArticle {
  id: number;
  code?: string | null;
  libelle?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  tauxRedevance?: number | null;
}

export type NewGroupeArticle = Omit<IGroupeArticle, 'id'> & { id: null };
