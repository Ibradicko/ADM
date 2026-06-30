import { IProduit } from 'app/entities/produit/produit.model';
import { IVente } from 'app/entities/vente/vente.model';

export interface ILigneVente {
  id: number;
  quantite?: number | null;
  prixUnitaire?: number | null;
  remise?: number | null;
  montantLigne?: number | null;
  codeBarresScanne?: string | null;
  vente?: Pick<IVente, 'id' | 'numeroTicket'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewLigneVente = Omit<ILigneVente, 'id'> & { id: null };
