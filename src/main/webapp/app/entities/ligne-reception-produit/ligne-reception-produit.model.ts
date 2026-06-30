import { IProduit } from 'app/entities/produit/produit.model';
import { IReceptionProduit } from 'app/entities/reception-produit/reception-produit.model';

export interface ILigneReceptionProduit {
  id: number;
  quantiteAttendue?: number | null;
  quantiteRecue?: number | null;
  ecart?: number | null;
  codeBarresScanne?: string | null;
  reception?: Pick<IReceptionProduit, 'id' | 'reference'> | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
}

export type NewLigneReceptionProduit = Omit<ILigneReceptionProduit, 'id'> & { id: null };
