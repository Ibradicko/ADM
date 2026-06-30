import { IProduit } from 'app/entities/produit/produit.model';
import { ITransfertStock } from 'app/entities/transfert-stock/transfert-stock.model';

export interface ILigneTransfertStock {
  id: number;
  quantite?: number | null;
  commentaire?: string | null;
  transfert?: Pick<ITransfertStock, 'id' | 'reference'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewLigneTransfertStock = Omit<ILigneTransfertStock, 'id'> & { id: null };
