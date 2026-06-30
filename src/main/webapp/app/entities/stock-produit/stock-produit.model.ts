import dayjs from 'dayjs/esm';

import { IDepotStock } from 'app/entities/depot-stock/depot-stock.model';
import { IProduit } from 'app/entities/produit/produit.model';

export interface IStockProduit {
  id: number;
  quantiteTheorique?: number | null;
  stockAlerte?: number | null;
  dateDernierMouvement?: dayjs.Dayjs | null;
  produit?: Pick<IProduit, 'id' | 'designation'> | null;
  depot?: Pick<IDepotStock, 'id' | 'code'> | null;
}

export type NewStockProduit = Omit<IStockProduit, 'id'> & { id: null };
