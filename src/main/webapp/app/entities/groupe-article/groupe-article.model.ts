import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';

export interface IGroupeArticle {
  id: number;
  code?: string | null;
  libelle?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
}

export type NewGroupeArticle = Omit<IGroupeArticle, 'id'> & { id: null };
