import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IFamilleArticle } from 'app/entities/famille-article/famille-article.model';

export interface ISousFamilleArticle {
  id: number;
  code?: string | null;
  libelle?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  familleArticle?: Pick<IFamilleArticle, 'id' | 'libelle'> | null;
}

export type NewSousFamilleArticle = Omit<ISousFamilleArticle, 'id'> & { id: null };
