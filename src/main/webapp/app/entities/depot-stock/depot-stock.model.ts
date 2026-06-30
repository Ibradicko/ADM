import { IBoutique } from 'app/entities/boutique/boutique.model';

export interface IDepotStock {
  id: number;
  code?: string | null;
  libelle?: string | null;
  emplacement?: string | null;
  actif?: boolean | null;
  boutique?: Pick<IBoutique, 'id' | 'nom' | 'code'> | null;
}

export type NewDepotStock = Omit<IDepotStock, 'id'> & { id: null };
