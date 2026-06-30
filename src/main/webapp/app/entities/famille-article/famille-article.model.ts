import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';

export interface IFamilleArticle {
  id: number;
  code?: string | null;
  libelle?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  groupeArticle?: Pick<IGroupeArticle, 'id' | 'libelle'> | null;
}

export type NewFamilleArticle = Omit<IFamilleArticle, 'id'> & { id: null };
