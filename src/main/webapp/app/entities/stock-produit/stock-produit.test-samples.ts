import dayjs from 'dayjs/esm';

import { IStockProduit, NewStockProduit } from './stock-produit.model';

export const sampleWithRequiredData: IStockProduit = {
  id: 3975,
  quantiteTheorique: 23.92,
};

export const sampleWithPartialData: IStockProduit = {
  id: 8892,
  quantiteTheorique: 13148.42,
  dateDernierMouvement: dayjs('2026-05-04T15:56'),
};

export const sampleWithFullData: IStockProduit = {
  id: 15539,
  quantiteTheorique: 16362.92,
  stockAlerte: 26844.33,
  dateDernierMouvement: dayjs('2026-05-04T16:08'),
};

export const sampleWithNewData: NewStockProduit = {
  quantiteTheorique: 8777.16,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
