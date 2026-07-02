import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypePrix } from 'app/entities/enumerations/type-prix.model';
import { IFamilleArticle } from 'app/entities/famille-article/famille-article.model';
import { IGroupeArticle } from 'app/entities/groupe-article/groupe-article.model';
import { ISousFamilleArticle } from 'app/entities/sous-famille-article/sous-famille-article.model';
import { IUniteMesure } from 'app/entities/unite-mesure/unite-mesure.model';

export interface IProduit {
  id: number;
  codeInterne?: string | null;
  designation?: string | null;
  description?: string | null;
  typePrix?: keyof typeof TypePrix | null;
  prixVente?: number | null;
  tauxRedevanceApplicable?: number | null;
  statut?: keyof typeof StatutGeneral | null;
  dateCreation?: dayjs.Dayjs | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  groupeArticle?: Pick<IGroupeArticle, 'id' | 'libelle' | 'tauxRedevance'> | null;
  familleArticle?: Pick<IFamilleArticle, 'id' | 'libelle' | 'groupeArticle'> | null;
  sousFamilleArticle?: Pick<ISousFamilleArticle, 'id' | 'libelle' | 'familleArticle'> | null;
  uniteMesure?: Pick<IUniteMesure, 'id' | 'code'> | null;
}

export type NewProduit = Omit<IProduit, 'id'> & { id: null };
