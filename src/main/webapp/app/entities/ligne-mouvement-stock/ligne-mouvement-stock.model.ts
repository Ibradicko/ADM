import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { IMouvementStock } from 'app/entities/mouvement-stock/mouvement-stock.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface ILigneMouvementStock {
  id: number;
  quantite?: number | null;
  stockAvant?: number | null;
  stockApres?: number | null;
  commentaire?: string | null;
  mouvement?: Pick<IMouvementStock, 'id' | 'reference'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
  depot?: Pick<IDepotStock, 'id' | 'code'> | null;
}

export type NewLigneMouvementStock = Omit<ILigneMouvementStock, 'id'> & { id: null };
