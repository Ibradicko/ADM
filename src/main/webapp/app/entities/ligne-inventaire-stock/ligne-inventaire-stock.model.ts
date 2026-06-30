import { IInventaireStock } from 'app/entities/inventaire-stock/inventaire-stock.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface ILigneInventaireStock {
  id: number;
  quantiteTheorique?: number | null;
  quantiteComptee?: number | null;
  ecart?: number | null;
  commentaire?: string | null;
  inventaire?: Pick<IInventaireStock, 'id' | 'reference'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewLigneInventaireStock = Omit<ILigneInventaireStock, 'id'> & { id: null };
