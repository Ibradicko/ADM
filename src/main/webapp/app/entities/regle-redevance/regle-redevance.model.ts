import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { TypeRegleRedevance } from 'app/entities/enumerations/type-regle-redevance.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { ILocataire } from 'app/entities/locataire/locataire.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface IRegleRedevance {
  id: number;
  code?: string | null;
  typeRegle?: keyof typeof TypeRegleRedevance | null;
  taux?: number | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  priorite?: number | null;
  actif?: boolean | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  locataire?: Pick<ILocataire, 'id' | 'nom' | 'code'> | null;
  groupeArticle?: Pick<IGroupeArticle, 'id' | 'libelle'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewRegleRedevance = Omit<IRegleRedevance, 'id'> & { id: null };
