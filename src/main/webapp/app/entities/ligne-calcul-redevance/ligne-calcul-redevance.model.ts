import { ICalculRedevance } from 'app/entities/calcul-redevance/calcul-redevance.model';
import { IVente } from 'app/entities/vente/vente.model';

export interface ILigneCalculRedevance {
  id: number;
  baseCalcul?: number | null;
  tauxApplique?: number | null;
  montantRedevance?: number | null;
  calcul?: Pick<ICalculRedevance, 'id' | 'reference'> | null;
  vente?: Pick<IVente, 'id' | 'numeroTicket'> | null;
}

export type NewLigneCalculRedevance = Omit<ILigneCalculRedevance, 'id'> & { id: null };
