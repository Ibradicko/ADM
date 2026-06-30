import dayjs from 'dayjs/esm';

import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface ICodeBarresProduit {
  id: number;
  code?: string | null;
  type?: keyof typeof TypeCodeBarres | null;
  principal?: boolean | null;
  genereParSysteme?: boolean | null;
  actif?: boolean | null;
  dateAffectation?: dayjs.Dayjs | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewCodeBarresProduit = Omit<ICodeBarresProduit, 'id'> & { id: null };
