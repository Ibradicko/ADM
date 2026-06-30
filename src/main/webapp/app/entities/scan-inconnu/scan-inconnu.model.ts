import dayjs from 'dayjs/esm';

import { IBoutique } from 'app/entities/boutique/boutique.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface IScanInconnu {
  id: number;
  codeScanne?: string | null;
  ecranOrigine?: string | null;
  dateScan?: dayjs.Dayjs | null;
  commentaire?: string | null;
  resolu?: boolean | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
  produitAffecte?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewScanInconnu = Omit<IScanInconnu, 'id'> & { id: null };
